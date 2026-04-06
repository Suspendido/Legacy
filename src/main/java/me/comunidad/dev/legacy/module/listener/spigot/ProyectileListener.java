package me.comunidad.dev.legacy.module.listener.spigot;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import me.comunidad.dev.legacy.framework.Module;
import me.comunidad.dev.legacy.module.combat.ProfileManager;
import me.comunidad.dev.legacy.module.listener.ListenerManager;
import me.comunidad.dev.legacy.utils.Tasks;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/*
 * Copyright (c) 2026. @Comunidad, made since 3/4/2026
 * Use or redistribution of this source file is only permitted
 * if explicit permission is given by the author.
 */
public class ProyectileListener extends Module<ListenerManager> {

    private final Set<UUID> rodHit = new HashSet<>();

    private static final Set<String> ROD_SOUNDS = Set.of(
            "entity.fishing_bobber.throw"
    );

    public ProyectileListener(ListenerManager manager) {
        super(manager);
        cancelRodSounds();
    }

    private ProfileManager profile() {
        return getInstance().getProfileManager();
    }

    /**
     * Intercepta los sonidos de la caña de pescar y los reemplaza
     * por el sonido de snowball para el jugador que lo recibe.
     */
    private void cancelRodSounds() {
        ProtocolLibrary.getProtocolManager().addPacketListener(
                new PacketAdapter(getInstance(), ListenerPriority.NORMAL,
                        PacketType.Play.Server.NAMED_SOUND_EFFECT) {
                    @Override
                    public void onPacketSending(PacketEvent event) {
                        try {
                            if (event.getPacket().getSoundEffects().size() == 0) return;

                            String key = event.getPacket()
                                    .getSoundEffects()
                                    .read(0)
                                    .getKey()
                                    .getKey();

                            if (!ROD_SOUNDS.contains(key)) return;

                            event.setCancelled(true);

                            Player player = event.getPlayer();
                            if (player != null) {
                                player.playSound(
                                        player.getLocation(),
                                        Sound.ENTITY_SNOWBALL_THROW,
                                        SoundCategory.PLAYERS,
                                        0.5f, 0.1f
                                );
                            }
                        } catch (Exception ignored) {}
                    }
                }
        );
    }

    @EventHandler
    public void onVelocity(PlayerVelocityEvent e) {
        if (!rodHit.contains(e.getPlayer().getUniqueId())) return;
        e.setCancelled(true);
    }

    @EventHandler
    public void onProyectileLaunch(ProjectileLaunchEvent e) {
        Projectile entity = e.getEntity();
        if (!(entity.getShooter() instanceof Player player)) return;

        ProfileManager p = profile();

        if (entity instanceof EnderPearl pearl) {
            applyLegacyPearl(player, pearl, p.pearlSpeed, p.pearlGravity);

        } else if (entity instanceof Arrow arrow) {
            applySpeed(arrow, p.arrowSpeed);
            applyGravity(arrow, p.arrowGravity, 0.05);

        } else if (entity instanceof Snowball || entity instanceof Egg) {
            applySpeed(entity, p.snowballSpeed);
            applyGravity(entity, p.snowballGravity, 0.03);

        } else if (entity instanceof Trident trident) {
            applySpeed(trident, p.tridentSpeed);
            applyGravity(trident, p.tridentGravity, 0.05);
        }
    }

    @EventHandler
    public void onProyectileHit(ProjectileHitEvent e) {
        Projectile projectile = e.getEntity();

        if (!(projectile instanceof Snowball || projectile instanceof Egg)) return;
        if (!(e.getHitEntity() instanceof Player player)) return;
        if (!(projectile.getShooter() instanceof Player shooter)) return;

        player.setNoDamageTicks(0);
        player.damage(0.01, shooter);

        Tasks.executeLater(getManager(), 1L, () -> {
            if (!player.isOnline()) return;
            applyProjectileKB(player, shooter);
        });
    }

    @EventHandler
    public void onRodHit(PlayerFishEvent e) {
        if (e.getState() != PlayerFishEvent.State.CAUGHT_ENTITY) return;
        if (!(e.getCaught() instanceof Player damaged)) return;

        Player attacker = e.getPlayer();
        ProfileManager p = profile();

        damaged.setNoDamageTicks(0);
        damaged.damage(0.01, attacker);
        rodHit.add(damaged.getUniqueId());
        e.getHook().remove();

        Tasks.executeLater(getManager(), 2L, () -> rodHit.remove(damaged.getUniqueId()));
        Tasks.executeLater(getManager(), 1L, () -> {
            if (!damaged.isOnline()) return;

            Vector velocity = calculateKB(
                    damaged, attacker,
                    p.rodKbHorizontal,
                    p.rodKbVertical,
                    p.rodKbVerticalLimit
            );
            if (damaged == attacker) return;
            damaged.setVelocity(velocity);
        });
    }

    private void applyProjectileKB(Player damaged, Player shooter) {
        ProfileManager p = profile();
        damaged.setVelocity(calculateKB(
                damaged, shooter,
                p.projKbHorizontal,
                p.projKbVertical,
                p.projKbVerticalLimit
        ));
    }

    private Vector calculateKB(Player damaged, Player attacker, double horizontal, double vertical, double verticalLimit) {
        double dx = attacker.getLocation().getX() - damaged.getLocation().getX();
        double dz = attacker.getLocation().getZ() - damaged.getLocation().getZ();

        while (dx * dx + dz * dz < 1.0E-4) {
            dx = (Math.random() - Math.random()) * 0.01;
            dz = (Math.random() - Math.random()) * 0.01;
        }

        double magnitude = Math.sqrt(dx * dx + dz * dz);
        Vector velocity  = damaged.getVelocity();

        velocity.setX(velocity.getX() / 2 - dx / magnitude * horizontal);
        velocity.setY(velocity.getY() / 2 + vertical);
        velocity.setZ(velocity.getZ() / 2 - dz / magnitude * horizontal);

        if (velocity.getY() > verticalLimit) velocity.setY(verticalLimit);

        return velocity;
    }

    private void applyLegacyPearl(Player player, EnderPearl pearl, double speed, double gravity) {
        pearl.setVelocity(player.getEyeLocation().getDirection().normalize().multiply(speed));
        applyGravity(pearl, gravity, 0.03);
    }

    /**
     * Escala la velocidad del proyectil preservando dirección.
     * Si multiplier=1.0 no hace nada.
     */
    private void applySpeed(Projectile projectile, double multiplier) {
        if (multiplier == 1.0) return;
        projectile.setVelocity(projectile.getVelocity().multiply(multiplier));
    }

    /**
     * Ajusta la gravedad de un proyectil.
     * <p>
     * Minecraft aplica gravedad internamente tick a tick restando un valor fijo
     * a la velocidad Y. No hay API directa para cambiar ese valor, así que:
     *   - gravity=0 → setGravity(false), sin caída
     *   - gravity=vanilla → sin cambio
     *   - gravity!=vanilla → setGravity(true) + compensación Y en el primer tick
     *
     * @param vanillaRef  gravedad que Minecraft aplica por defecto para este tipo:
     *                    flecha/trident=0.05, perla/snowball/egg=0.03
     */
    private void applyGravity(Projectile projectile, double gravity, double vanillaRef) {
        if (gravity <= 0) {
            projectile.setGravity(false);
            return;
        }

        projectile.setGravity(true);

        if (gravity == vanillaRef) return; // sin cambio necesario

        // Compensar la diferencia respecto al valor vanilla en el primer tick.
        // Si queremos MENOS gravedad (gravity < vanillaRef) → compensation positivo (empuje hacia arriba).
        // Si queremos MÁS gravedad (gravity > vanillaRef) → compensation negativo (empuje hacia abajo).
        double compensation = vanillaRef - gravity;
        getInstance().getServer().getScheduler().runTaskLater(getInstance(), () -> {
            if (!projectile.isValid()) return;
            projectile.setVelocity(projectile.getVelocity().add(new Vector(0, compensation, 0)));
        }, 1L);
    }
}
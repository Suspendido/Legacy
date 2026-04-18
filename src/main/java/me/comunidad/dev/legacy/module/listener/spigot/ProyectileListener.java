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
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.bukkit.util.Vector;

import java.util.*;

/*
 * Copyright (c) 2026. @Comunidad, made since 3/4/2026
 * Use or redistribution of this source file is only permitted
 * if explicit permission is given by the author.
 */
public class ProyectileListener extends Module<ListenerManager> {

    private final Map<Integer, UUID> hiddenHooks = new HashMap<>();

    private static final Set<String> ROD_SOUNDS = Set.of(
            "entity.fishing_bobber.throw"
    );

    public ProyectileListener(ListenerManager manager) {
        super(manager);
        cancelRodSounds();
        cancelHook();
    }

    private ProfileManager profile() {
        return getInstance().getProfileManager();
    }

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

    private void cancelHook() {
        ProtocolLibrary.getProtocolManager().addPacketListener(
                new PacketAdapter(getInstance(), ListenerPriority.NORMAL, PacketType.Play.Server.ENTITY_METADATA) {
                    @Override
                    public void onPacketSending(PacketEvent event) {
                        try {
                            int id = event.getPacket().getIntegers().read(0);
                            UUID target = hiddenHooks.get(id);
                            if (target != null && event.getPlayer().getUniqueId().equals(target)) {
                                event.setCancelled(true);
                            }
                        } catch (Exception ignored) {}
                    }
                }
        );
    }

    @EventHandler
    public void onVelocity(PlayerVelocityEvent e) {
        Player player = e.getPlayer();
        for (Entity entity : player.getWorld().getEntitiesByClass(FishHook.class)) {
            if (entity instanceof FishHook hook && hook.getHookedEntity() == player) {
                e.setCancelled(true);
                break;
            }
        }
    }

    @EventHandler
    public void onProyectileLaunch(ProjectileLaunchEvent e) {
        Projectile entity = e.getEntity();
        if (!(entity.getShooter() instanceof Player player)) return;

        switch (entity) {
            case EnderPearl pearl -> {
                pearl.teleport(player.getEyeLocation().clone().add(player.getEyeLocation().getDirection().multiply(0.16)));
                pearl.setVelocity(player.getEyeLocation().getDirection().normalize().multiply(profile().pearlSpeed));
                applyGravity(pearl, profile().pearlGravity, 0.03);
            }
            case Arrow arrow -> {
                applySpeed(arrow, profile().arrowSpeed);
                applyGravity(arrow, profile().arrowGravity, 0.05);
            }
            case Snowball s -> {
                applySpeed(s, profile().snowballSpeed);
                applyGravity(s, profile().snowballGravity, 0.03);
            }
            case Egg egg -> {
                applySpeed(egg, profile().snowballSpeed);
                applyGravity(egg, profile().snowballGravity, 0.03);
            }
            case Trident trident -> {
                applySpeed(trident, profile().tridentSpeed);
                applyGravity(trident, profile().tridentGravity, 0.05);
            }
            default -> {}
        }
    }

    @EventHandler
    public void onProyectileHit(ProjectileHitEvent e) {
        Projectile projectile = e.getEntity();
        if (!(projectile instanceof Snowball || projectile instanceof Egg || projectile instanceof Arrow || projectile instanceof FishHook)) return;
        if (!(e.getHitEntity() instanceof Player damaged)) return;
        if (damaged.getGameMode() == GameMode.CREATIVE) return;
        if (!(projectile.getShooter() instanceof Player shooter)) return;
        if (damaged == shooter) return;

        if (projectile instanceof Arrow) {
            damaged.setNoDamageTicks(0);
            return;
        }

        if (projectile instanceof FishHook) {
            damaged.setNoDamageTicks(0);
            hiddenHooks.put(projectile.getEntityId(), damaged.getUniqueId());
            Tasks.execute(getManager(), () -> hiddenHooks.remove(projectile.getEntityId()));
        }

        damaged.damage(0.01, shooter);

        Tasks.execute(getManager(), () -> {
            if (!damaged.isOnline()) return;
            damaged.setVelocity(calculateKB(damaged, shooter,
                    projectile instanceof FishHook ? profile().rodKbHorizontal : profile().projKbHorizontal,
                    projectile instanceof FishHook ? profile().rodKbVertical : profile().projKbVertical,
                    projectile instanceof FishHook ? profile().rodKbVerticalLimit : profile().projKbVerticalLimit)
            );
        });
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

        if (damaged.isOnGround()) { // Esto previene que los proyectiles hagan subir infinitamente al jugador, Fix de mierda pero deberia de andar
            velocity.setY(velocity.getY() / 2 + vertical);
        }

        velocity.setZ(velocity.getZ() / 2 - dz / magnitude * horizontal);

        if (velocity.getY() > verticalLimit) velocity.setY(verticalLimit);

        return velocity;
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
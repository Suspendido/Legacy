package me.comunidad.dev.legacy.module.listener.spigot;

import me.comunidad.dev.legacy.framework.Module;
import me.comunidad.dev.legacy.module.combat.ProfileManager;
import me.comunidad.dev.legacy.module.listener.ListenerManager;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.util.Vector;

/*
 * Copyright (c) 2026. @Comunidad, made since 3/4/2026
 * Use or redistribution of this source file is only permitted
 * if explicit permission is given by the author.
 */
public class ProyectileListener extends Module<ListenerManager> {

    public ProyectileListener(ListenerManager manager) {
        super(manager);
    }

    private ProfileManager profile() {
        return getInstance().getProfileManager();
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

    /**
     * En versiones modernas, al lanzar una perla el servidor le suma
     * la velocidad actual del jugador al vector inicial, lo que hace que
     * al correr la perla vuele diferente. En 1.8 esto NO ocurría —
     * la velocidad era solo dirección * speed, sin herencia del movimiento.
     * <p>
     * Se sobreescribe la velocidad completa con solo la dirección de la cámara.
     */
    private void applyLegacyPearl(Player player, EnderPearl pearl, double speed, double gravity) {
        Vector dir = player.getEyeLocation().getDirection().normalize().multiply(speed);
        pearl.setVelocity(dir);

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
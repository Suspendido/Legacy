package me.comunidad.dev.legacy.module.listener.spigot;

import me.comunidad.dev.legacy.framework.Module;
import me.comunidad.dev.legacy.module.combat.ProfileManager;
import me.comunidad.dev.legacy.module.listener.ListenerManager;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileLaunchEvent;

/*
 * Copyright (c) 2026. @Comunidad, made since 3/4/2026
 * Use or redistribution of this source file is only permitted
 * if explicit permission is given by the author.
 */
public class ArrowGravityListener extends Module<ListenerManager> {

    public ArrowGravityListener(ListenerManager manager) {
        super(manager);
    }

    @EventHandler
    public void onArrowLaunch(ProjectileLaunchEvent e) {
        if (!(e.getEntity() instanceof Arrow arrow)) return;
        if (!(arrow.getShooter() instanceof Player)) return;

        double gravity = getInstance().getProfileManager().arrowGravity;
        arrow.setGravity(gravity > 0);
        if (gravity > 0 && gravity != 0.05) {
            // Ajustar la componente Y inicial para compensar la diferencia
            // respecto al valor vanilla (0.05 por tick aprox en 1.21)
            double vanillaGravity = 0.05;
            double compensation = vanillaGravity - gravity;

            getInstance().getServer().getScheduler().runTaskLater(getInstance(), () -> {
                if (!arrow.isValid()) return;
                arrow.setVelocity(arrow.getVelocity().add(new org.bukkit.util.Vector(0, compensation, 0)));
            }, 1L);
        }
    }
}
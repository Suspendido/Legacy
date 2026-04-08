package me.comunidad.dev.legacy.module.listener;

import lombok.Getter;
import me.comunidad.dev.legacy.Core;
import me.comunidad.dev.legacy.framework.Manager;
import me.comunidad.dev.legacy.module.listener.spigot.*;
import me.comunidad.dev.legacy.utils.Utils;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
 * Copyright (c) 2026. @Comunidad, made since 2/4/2026
 * Use or redistribution of this source file is only permitted
 * if explicit permission is given by the author.
 */
@Getter
public class ListenerManager extends Manager {

    private final List<Listener> listeners;
    private final List<BukkitTask> tasks;
    private final CombatListener combatListener;

    public ListenerManager(Core instance) {
        super(instance);
        this.listeners = new ArrayList<>();
        this.tasks = new ArrayList<>();
        this.combatListener = new CombatListener(this);
        this.load();
    }

    private void load() {
        listeners.addAll(Arrays.asList(
                new KnockbackListener(this),
                new PotionListener(this),
                new PreventionListener(this),
//                new CombatListener(this),
                new ProyectileListener(this),
                new ArrowGravityListener(this)
        ));

    }

    @Override
    public void reload() {
        for (Listener listener : listeners) {
            HandlerList.unregisterAll(listener);
        }

        Utils.iterate(tasks, (task) -> {
            task.cancel();
            return true;
        });

        this.load();
    }
}

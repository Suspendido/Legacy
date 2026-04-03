package me.comunidad.dev.legacy.utils;

import me.comunidad.dev.legacy.framework.Manager;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

public class Tasks {

    public static void executeAsync(Manager manager, Runnable runnable) {
        Bukkit.getServer().getScheduler().runTaskAsynchronously(manager.getInstance(), runnable);
    }

    public static void execute(Manager manager, Runnable runnable) {
        Bukkit.getServer().getScheduler().runTask(manager.getInstance(), runnable);
    }

    public static BukkitTask executeTimer(Manager manager, Runnable runnable, long delay, long period) {
        return Bukkit.getScheduler().runTaskTimer(manager.getInstance(), runnable, delay, period);
    }

    public static int executeLater(Manager manager, long ticks, Runnable runnable) {
        return Bukkit.getServer().getScheduler().runTaskLater(manager.getInstance(), runnable, ticks).getTaskId();
    }

    public static void executeLaterAsync(Manager manager, long ticks, Runnable runnable) {
        Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(manager.getInstance(), runnable, ticks);
    }

    public static void executeScheduledAsync(Manager manager, long ticks, Runnable runnable) {
        Bukkit.getServer().getScheduler().runTaskTimerAsynchronously(manager.getInstance(), runnable, 0L, ticks);
    }

    public static void executeScheduled(Manager manager, long ticks, Runnable runnable) {
        Bukkit.getServer().getScheduler().runTaskTimer(manager.getInstance(), runnable, 0L, ticks);
    }

    public static void cancel(int taskId) {
        Bukkit.getServer().getScheduler().cancelTask(taskId);
    }
}
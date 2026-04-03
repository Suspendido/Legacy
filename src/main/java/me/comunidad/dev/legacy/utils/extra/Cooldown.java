package me.comunidad.dev.legacy.utils.extra;

import lombok.Getter;
import me.comunidad.dev.legacy.framework.Manager;
import me.comunidad.dev.legacy.framework.Module;
import me.comunidad.dev.legacy.utils.Formatter;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class Cooldown extends Module<Manager> {

    private final Map<UUID, Long> cooldowns;

    public Cooldown(Manager manager) {
        super(manager);
        this.cooldowns = new ConcurrentHashMap<>();
        getInstance().getCooldowns().add(this);
    }

    public void clean() {
        cooldowns.values().removeIf(next -> next - System.currentTimeMillis() < 0L);
    }

    public void applyCooldown(Player player, int seconds) {
        cooldowns.put(player.getUniqueId(), System.currentTimeMillis() + (seconds * 1000L));
    }

    public void applyCooldownTicks(Player player, int ticks) {
        cooldowns.put(player.getUniqueId(), System.currentTimeMillis() + ticks);
    }

    public boolean hasCooldown(Player player) {
        return cooldowns.containsKey(player.getUniqueId()) && (cooldowns.get(player.getUniqueId()) >= System.currentTimeMillis());
    }

    public void removeCooldown(Player player) {
        cooldowns.remove(player.getUniqueId());
    }

    public String getRemaining(Player player) {
        long l = this.cooldowns.get(player.getUniqueId()) - System.currentTimeMillis();
        return Formatter.getRemaining(l, true);
    }

    public String getRemainingActionBar(Player player) {
        long l = this.cooldowns.get(player.getUniqueId()) - System.currentTimeMillis();
        return Formatter.getRemaining(l, false);
    }

    public String getRemainingOld(Player player) {
        long l = this.cooldowns.get(player.getUniqueId()) - System.currentTimeMillis();
        return Formatter.getRemaining(l, !getConfig().getBoolean("TIMERS_COOLDOWN.OLD_TIMER_FORMAT"));
    }
}
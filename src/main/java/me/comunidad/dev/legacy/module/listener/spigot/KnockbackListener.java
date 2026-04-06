package me.comunidad.dev.legacy.module.listener.spigot;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import me.comunidad.dev.legacy.framework.Module;
import me.comunidad.dev.legacy.module.combat.ProfileManager;
import me.comunidad.dev.legacy.module.listener.events.PlayerSprintResetEvent;
import me.comunidad.dev.legacy.module.listener.ListenerManager;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSprintEvent;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.bukkit.util.Vector;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class KnockbackListener extends Module<ListenerManager> {

    private final Map<UUID, Vector> velocityCache;
    private final Set<UUID> recentlySprinted;

    public KnockbackListener(ListenerManager manager) {
        super(manager);
        this.velocityCache = Maps.newHashMap();
        this.recentlySprinted = Sets.newConcurrentHashSet();
    }

    @EventHandler
    public void onToggleSprint(PlayerToggleSprintEvent event) {
        Player player = event.getPlayer();
        if (event.isSprinting()) {
            if (!getInstance().getProfileManager().requiredGroundCheck || player.isOnGround()) {
                recentlySprinted.add(player.getUniqueId());
            }
        } else {
            recentlySprinted.remove(player.getUniqueId());
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        UUID id = event.getPlayer().getUniqueId();
        recentlySprinted.remove(id);
        velocityCache.remove(id);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerVelocity(PlayerVelocityEvent event) {
        UUID id = event.getPlayer().getUniqueId();
        if (velocityCache.containsKey(id)) {
            event.setVelocity(velocityCache.remove(id));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event instanceof EntityDamageByEntityEvent damageEvent)) return;
        if (!(damageEvent.getEntity() instanceof Player damaged))  return;
        if (!(damageEvent.getDamager() instanceof Player damager)) return;

        if (damaged.getUniqueId().equals(damager.getUniqueId())) return;
        if (event.getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) return;
        if (damaged.getNoDamageTicks() > damaged.getMaximumNoDamageTicks() / 2) return;

        removeKnockbackResistance(damaged);
        applyCustomKnockback(damaged, damager);
    }

    private void removeKnockbackResistance(Player player) {
        if (player.getAttribute(Attribute.KNOCKBACK_RESISTANCE) == null) return;
        for (AttributeModifier modifier : player.getAttribute(Attribute.KNOCKBACK_RESISTANCE).getModifiers()) {
            player.getAttribute(Attribute.KNOCKBACK_RESISTANCE).removeModifier(modifier);
        }
    }

    private void applyCustomKnockback(Player damaged, Player attacker) {
        ProfileManager p = getInstance().getProfileManager();

        double dx = attacker.getLocation().getX() - damaged.getLocation().getX();
        double dz = attacker.getLocation().getZ() - damaged.getLocation().getZ();

        while (dx * dx + dz * dz < 1.0E-4) {
            dx = (Math.random() - Math.random()) * 0.01;
            dz = (Math.random() - Math.random()) * 0.01;
        }

        double magnitude = Math.sqrt(dx * dx + dz * dz);
        Vector velocity  = damaged.getVelocity();

        if (p.kbFriction != 0) {
            velocity.setX(velocity.getX() / p.kbFriction);
            velocity.setY(velocity.getY() / p.kbFriction);
            velocity.setZ(velocity.getZ() / p.kbFriction);
        }

        velocity.setX(velocity.getX() - dx / magnitude * p.kbHorizontal);
        velocity.setY(velocity.getY() + p.kbVertical);
        velocity.setZ(velocity.getZ() - dz / magnitude * p.kbHorizontal);

        // dynamicLimit: si supera el límite, poner a 0 en vez de clampar
        if (velocity.getY() > p.kbVerticalLimit) {
            velocity.setY(p.kbDynamicLimit ? 0.0 : p.kbVerticalLimit);
        }

        if (p.kbLimitHorizontal) {
            double hl = p.kbHorizontalLimit;
            if (Math.abs(velocity.getX()) > hl) velocity.setX(Math.signum(velocity.getX()) * hl);
            if (Math.abs(velocity.getZ()) > hl) velocity.setZ(Math.signum(velocity.getZ()) * hl);
        }

        double extra = getExtraKnockback(attacker);

        // el extra solo aplica si los jugadores NO van en la misma dirección (1.7 related)
        boolean applyExtra = !p.kbOnePointSeven || !sameDirection(attacker, damaged);

        if (extra > 0 && applyExtra) {
            float yaw = attacker.getLocation().getYaw() * ((float) Math.PI / 180F);
            velocity.add(new Vector(
                    -Math.sin(yaw) * extra * p.kbExtraHorizontal,
                    p.kbExtraVertical,
                    Math.cos(yaw) * extra * p.kbExtraHorizontal
            ));
        }

        velocityCache.put(damaged.getUniqueId(), velocity);
        recentlySprinted.remove(attacker.getUniqueId());
    }

    private double getExtraKnockback(Player attacker) {
        ProfileManager p = getInstance().getProfileManager();
        double kb = attacker.getInventory().getItemInMainHand().getEnchantmentLevel(Enchantment.KNOCKBACK);

        if (!attacker.isSprinting()) return kb;

        if (recentlySprinted.contains(attacker.getUniqueId())) {
            Bukkit.getPluginManager().callEvent(new PlayerSprintResetEvent(attacker));
            kb += p.kbSprintResetModifier;
        }

        return kb + p.kbSprintModifier;
    }

    private boolean sameDirection(Player a, Player b) {
        Vector va = a.getVelocity();
        Vector vb = b.getVelocity();
        boolean sameX = Math.signum(va.getX()) == Math.signum(vb.getX());
        boolean sameZ = Math.signum(va.getZ()) == Math.signum(vb.getZ());
        return sameX && sameZ;
    }
}
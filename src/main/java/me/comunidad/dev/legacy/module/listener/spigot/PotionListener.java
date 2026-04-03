package me.comunidad.dev.legacy.module.listener.spigot;

import me.comunidad.dev.legacy.framework.Module;
import me.comunidad.dev.legacy.module.combat.ProfileManager;
import me.comunidad.dev.legacy.module.listener.ListenerManager;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.util.Vector;

public class PotionListener extends Module<ListenerManager> {

    public PotionListener(ListenerManager manager) {
        super(manager);
    }

    private ProfileManager profile() {
        return getInstance().getProfileManager();
    }

    /**
     * Boosts self-splash intensity when the throwing player hits themselves.
     * Threshold and cap come from the active profile.
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPotionSplash(PotionSplashEvent event) {
        if (!(event.getPotion().getShooter() instanceof Player player)) return;
        if (!player.isSprinting()) return;

        ProfileManager p = profile();

        for (var affected : event.getAffectedEntities()) {
            if (!affected.getUniqueId().equals(player.getUniqueId())) continue;

            double intensity = event.getIntensity(affected);
            if (intensity <= p.potionMinSelfIntensity) continue;

            event.setIntensity(affected, Math.min(intensity + p.potionSelfIntensityBoost, p.potionMaxIntensityCap));
        }
    }

    /**
     * Adjusts thrown-potion velocity using values from the active profile:
     *   - speed-multiplier  : scales the look direction
     *   - y-offset          : upward bump added to the velocity
     *   - player-vel-x/z    : how much the thrower's own momentum carries over
     */
    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        if (!(event.getEntity() instanceof ThrownPotion potion)) return;
        if (!(potion.getShooter() instanceof Player player))      return;

        ProfileManager p = profile();

        Vector velocity = player.getLocation().getDirection();
        velocity.multiply(p.potionSpeedMultiplier);

        Vector playerVel = player.getVelocity();
        velocity.add(new Vector(playerVel.getX() *
                p.potionPlayerVelX,
                p.potionYOffset,
                playerVel.getZ() * p.potionPlayerVelZ
        ));

        potion.setVelocity(velocity);
    }
}
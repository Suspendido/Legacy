package me.comunidad.dev.legacy.module.listener.spigot;

import me.comunidad.dev.legacy.framework.Module;
import me.comunidad.dev.legacy.module.lang.Lang;
import me.comunidad.dev.legacy.module.listener.ListenerManager;
import me.comunidad.dev.legacy.utils.Tasks;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Gate;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class PreventionListener extends Module<ListenerManager> {

    public PreventionListener(ListenerManager manager) {
        super(manager);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getItem() == null) return;
        if (event.getItem().getType() != Material.ENDER_PEARL) return;
        if (event.getAction() == Action.RIGHT_CLICK_AIR) return;

        Block clicked = event.getClickedBlock();
        if (clicked == null) return;

        if (isInvalidPearlBlock(clicked)) {
            event.setCancelled(true);
        }
    }

    private boolean isInvalidPearlBlock(Block block) {
        Material type = block.getType();
        String name = type.name();

        if (type == Material.STRING) return true;
        if (name.endsWith("_FENCE")) return true;
        if (name.endsWith("_STAINED_GLASS_PANE") || name.contains("GLASS_PANE")) return true;
        if (name.endsWith("_WALL")) return true;

        if (block.getBlockData() instanceof Gate gate) {
            return !gate.isOpen();
        }

        return false;
    }

    @EventHandler(ignoreCancelled = true)
    public void onPearlHit(ProjectileHitEvent event) {
        if (!(event.getEntity() instanceof EnderPearl pearl)) return;
        if (!(pearl.getShooter() instanceof Player player)) return;

        Block hit = event.getHitBlock();
        if (!shouldCancelPearl(player, pearl, hit)) return;

        player.getInventory().addItem(new ItemStack(Material.ENDER_PEARL));
        Tasks.execute(getManager(), () -> player.setCooldown(Material.ENDER_PEARL, 0));
        pearl.remove();
    }

    private boolean shouldCancelPearl(Player player, EnderPearl pearl, Block block) {
        if (block == null) return false;

        Location eye    = player.getEyeLocation();
        Location center = block.getLocation().add(0.5, 0.5, 0.5);

        if (center.distanceSquared(eye) > 3.0D) return false;

        Vector pearlDir = pearl.getVelocity().normalize();
        Vector toBlock  = center.toVector().subtract(eye.toVector()).normalize();

        return pearlDir.dot(toBlock) > 0.85;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if (event.getCause() != PlayerTeleportEvent.TeleportCause.ENDER_PEARL) return;

        Player player = event.getPlayer();
        Block block   = event.getTo().getBlock();

        if (isInvalidPearlBlock(block)) {
            event.setCancelled(true);
            player.getInventory().addItem(new ItemStack(Material.ENDER_PEARL));
            Tasks.execute(getManager(), () -> player.setCooldown(Material.ENDER_PEARL, 0));
            sendMessage(player, getInstance().getLangManager().of(Lang.EXPLOIT_INVALID_PEARL));
            return;
        }

        Location to = event.getTo();
        double x = to.getBlockX() + 0.5D;
        double z = to.getBlockZ() + 0.5D;
        double y = getSafeY(player, event);

        event.setTo(new Location(to.getWorld(), x, y, z, to.getYaw(), to.getPitch()));
    }

    private double getSafeY(Player player, PlayerTeleportEvent event) {
        Location to   = event.getTo();
        Location from = event.getFrom();

        if (player.hasPotionEffect(PotionEffectType.LEVITATION) && to.getY() >= from.getY()) {
            return to.getBlockY() - 1.0D;
        }
        return to.getBlockY();
    }
}
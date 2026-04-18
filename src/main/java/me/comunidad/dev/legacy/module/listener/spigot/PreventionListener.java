package me.comunidad.dev.legacy.module.listener.spigot;

import me.comunidad.dev.legacy.framework.Module;
import me.comunidad.dev.legacy.module.combat.ProfileManager;
import me.comunidad.dev.legacy.module.lang.Lang;
import me.comunidad.dev.legacy.module.listener.ListenerManager;
import me.comunidad.dev.legacy.utils.PearlUtil;
import me.comunidad.dev.legacy.utils.Tasks;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Gate;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.block.data.type.TrapDoor;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;

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

        if (PearlUtil.isInvalidLaunchBlock(clicked)) {
            event.setCancelled(true);
        }
    }

    private ProfileManager profile() {
        return getInstance().getProfileManager();
    }

    @EventHandler(ignoreCancelled = true)
    public void onPearlHit(ProjectileHitEvent event) {
        if (!(event.getEntity() instanceof EnderPearl pearl)) return;
        if (!(pearl.getShooter() instanceof Player player)) return;
        if (event.getHitBlock() == null) return;

        Block hit = event.getHitBlock();
        Location eye = player.getEyeLocation();

        if (hit.getLocation().add(0.5, 0.5, 0.5).distanceSquared(eye) > 400) return;

        if (shouldRefund(hit, player, pearl)) {
            refund(player, pearl);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPearlTeleport(PlayerTeleportEvent event) {
        if (event.getCause() != PlayerTeleportEvent.TeleportCause.ENDER_PEARL) return;

        Player player = event.getPlayer();
        Location to = event.getTo().clone();
        Location from = event.getFrom();

        Block destBlock = to.getBlock();

        if (isBlockedDestination(destBlock)) {
            event.setCancelled(true);
            refundNoEntity(player);
            sendMessage(player, getInstance().getLangManager().of(Lang.EXPLOIT_INVALID_PEARL));
            return;
        }

        // Resolver la posición de llegada
        Location resolved = resolveDestination(to, from, player);
        if (resolved == null) {
            event.setCancelled(true);
            refundNoEntity(player);
            sendMessage(player, getInstance().getLangManager().of(Lang.PEARL_REFUNDED));
            return;
        }

        resolved.setYaw(to.getYaw());
        resolved.setPitch(to.getPitch());
        event.setTo(resolved);

        double damage = profile().pearlDamage;
        if (damage > 0) {
            Tasks.executeLater(getManager(), 1L, () -> {
                if (player.isOnline()) player.damage(damage);
            });
        }

        Tasks.execute(getManager(), () -> player.setCooldown(Material.ENDER_PEARL, profile().pearlCooldownTicks));
    }

    private Location resolveDestination(Location to, Location from, Player player) {
        Block block = to.getBlock();
        String dir = PearlUtil.direction(from);
        Material mat = block.getType();

        Location result = new Location(to.getWorld(), to.getBlockX() + 0.5, to.getBlockY(), to.getBlockZ() + 0.5);

        if (mat.name().contains("FENCE_GATE")) {
            if (block.getBlockData() instanceof Gate gate && !gate.isOpen()) {
                return null; // gate cerrada = refund
            }
            result.setX(to.getBlockX() + 0.5);
            result.setZ(to.getBlockZ() + 0.5);
            return result;
        }

        if (mat.name().contains("TRAPDOOR")) {
            if (block.getBlockData() instanceof TrapDoor td && td.isOpen()) {
                return result;
            }
            return null;
        }

        if (isSolid(block)) {
            return tryWallPearl(block, result, dir, player); // sólido sin salida válida = refund
        }

        if (PearlUtil.isStairs(mat)) {
            if (!stairAllowsDirection(block, dir)) return null;
            return result;
        }

        PearlUtil.isSlab(mat);

        return result;
    }

    private Location tryWallPearl(Block solid, Location base, String dir, Player player) {
        // Obtener bloques adyacentes según la dirección
        Block[] candidates = PearlUtil.getDirectionalBlocks(solid, dir);

        for (Block candidate : candidates) {
            if (candidate == null) continue;

            // El bloque siguiente en la dirección debe ser transparente/pasable
            if (isTransparent(candidate)) {
                Location result = candidate.getLocation().add(0.5, 0, 0.5);
                result.setY(candidate.getY());
                return result;
            }

            // Fence gate abierta también es válida
            if (candidate.getType().name().contains("FENCE_GATE")) {
                if (candidate.getBlockData() instanceof Gate gate && gate.isOpen()) {
                    Location result = candidate.getLocation().add(0.5, 0, 0.5);
                    result.setY(candidate.getY());
                    return result;
                }
            }
        }

        // Diagonal: intentar bloques diagonales si la dirección es diagonal
        if (dir.length() == 2) {
            return tryDiagonalPearl(solid, base, dir);
        }

        return null;
    }

    private Location tryDiagonalPearl(Block solid, Location base, String dir) {
        Block[] sides = PearlUtil.getDiagonalSideBlocks(solid, dir);

        for (Block side : sides) {
            if (side != null && isTransparent(side)) {
                Location result = side.getLocation().add(0.5, 0, 0.5);
                result.setY(side.getY());
                return result;
            }
        }
        return null;
    }

    private boolean shouldRefund(Block hit, Player player, EnderPearl pearl) {
        Material mat = hit.getType();
        String name  = mat.name();

        // Strings, fences, glass panes, walls — siempre refund
        if (mat == Material.STRING) return true;
        if (name.endsWith("_FENCE")) return true;
        if (name.contains("GLASS_PANE")) return true;
        if (name.endsWith("_WALL")) return true;

        // Fence gate cerrada
        if (name.contains("FENCE_GATE")) {
            if (hit.getBlockData() instanceof Gate gate) return !gate.isOpen();
        }

        // Trapdoor cerrada muy cerca del jugador
        if (name.contains("TRAPDOOR")) {
            if (hit.getBlockData() instanceof TrapDoor td && !td.isOpen()) {
                double dist = hit.getLocation().add(0.5, 0.5, 0.5)
                        .distanceSquared(player.getEyeLocation());
                return dist <= 9.0; // solo refundar si está cerca
            }
        }

        return false;
    }

    private boolean isBlockedDestination(Block block) {
        Material mat = block.getType();
        String name  = mat.name();
        if (mat == Material.STRING) return true;
        if (name.endsWith("_FENCE")) return true;
        if (name.contains("GLASS_PANE")) return true;
        if (name.endsWith("_WALL")) return true;
        if (name.contains("FENCE_GATE")) {
            if (block.getBlockData() instanceof Gate gate) return !gate.isOpen();
        }
        return false;
    }

    private boolean isSolid(Block block) {
        Material mat = block.getType();
        return mat.isSolid() && !isTransparent(block);
    }

    private boolean isTransparent(Block block) {
        Material mat = block.getType();
        String   name = mat.name();
        return mat.isAir() || mat == Material.WATER || mat == Material.LAVA
                || name.contains("FENCE")
                || name.contains("SLAB") && !name.contains("DOUBLE")
                || name.contains("STAIRS")
                || name.contains("TRAPDOOR")
                || name.contains("DOOR")  && !name.contains("TRAP")
                || name.contains("SIGN")
                || name.contains("TORCH")
                || name.contains("BUTTON")
                || name.contains("CARPET")
                || name.contains("PLATE")
                || name.contains("BANNER")
                || name.contains("CHEST")
                || name.contains("GLASS") && !name.contains("PANE")
                || mat == Material.COBWEB
                || mat == Material.SNOW
                || mat == Material.VINE
                || mat == Material.LADDER
                || mat == Material.IRON_BARS;
    }

    /**
     * Las stairs solo permiten el paso en la dirección hacia la que miran.
     * Port simplificado de isStairBad() de Azurite.
     */
    private boolean stairAllowsDirection(Block block, String dir) {
        if (!(block.getBlockData() instanceof Stairs stairs)) return true;
        BlockFace facing = stairs.getFacing();
        // Bloquear si el jugador va perpendicular a la escalera
        return switch (dir) {
            case "E", "W" -> facing != BlockFace.EAST && facing != BlockFace.WEST;
            case "N", "S" -> facing != BlockFace.NORTH && facing != BlockFace.SOUTH;
            default -> true;
        };
    }

    private void refund(Player player, EnderPearl pearl) {
        player.getInventory().addItem(new ItemStack(Material.ENDER_PEARL));
        Tasks.execute(getManager(), () -> player.setCooldown(Material.ENDER_PEARL, 0));
        pearl.remove();
    }

    private void refundNoEntity(Player player) {
        player.getInventory().addItem(new ItemStack(Material.ENDER_PEARL));
        Tasks.execute(getManager(), () -> player.setCooldown(Material.ENDER_PEARL, 0));
    }
}
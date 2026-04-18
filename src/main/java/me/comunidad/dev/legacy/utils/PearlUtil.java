package me.comunidad.dev.legacy.utils;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Gate;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.stream.Stream;

/*
 * Copyright (c) 2026. @Comunidad, made since 10/4/2026
 * Use or redistribution of this source file is only permitted
 * if explicit permission is given by the author.
 */
public class PearlUtil {

    private static final List<String> DIRECTIONS = List.of("W", "NW", "N", "NE", "E", "SE", "S", "SW");

    public static String direction(Location location) {
        double d = (location.getYaw() - 90.0f) % 360.0;
        if (d < 0.0) d += 360.0;
        return DIRECTIONS.get((int) ((d + 22.5) / 45.0) % 8);
    }

    public static Block[] getDirectionalBlocks(Block block, String dir) {
        return switch (dir) {
            case "N"  -> new Block[]{ block.getRelative(BlockFace.NORTH) };
            case "S"  -> new Block[]{ block.getRelative(BlockFace.SOUTH) };
            case "E"  -> new Block[]{ block.getRelative(BlockFace.EAST)  };
            case "W"  -> new Block[]{ block.getRelative(BlockFace.WEST)  };
            case "NE" -> new Block[]{ block.getRelative(BlockFace.NORTH), block.getRelative(BlockFace.EAST) };
            case "NW" -> new Block[]{ block.getRelative(BlockFace.NORTH), block.getRelative(BlockFace.WEST) };
            case "SE" -> new Block[]{ block.getRelative(BlockFace.SOUTH), block.getRelative(BlockFace.EAST) };
            case "SW" -> new Block[]{ block.getRelative(BlockFace.SOUTH), block.getRelative(BlockFace.WEST) };
            default   -> new Block[0];
        };
    }

    public static Block[] getDiagonalSideBlocks(Block block, String dir) {
        return switch (dir) {
            case "NE" -> new Block[]{ block.getRelative(BlockFace.NORTH), block.getRelative(BlockFace.EAST) };
            case "NW" -> new Block[]{ block.getRelative(BlockFace.NORTH), block.getRelative(BlockFace.WEST) };
            case "SE" -> new Block[]{ block.getRelative(BlockFace.SOUTH), block.getRelative(BlockFace.EAST) };
            case "SW" -> new Block[]{ block.getRelative(BlockFace.SOUTH), block.getRelative(BlockFace.WEST) };
            default   -> new Block[0];
        };
    }

    public static BlockFace getBehind(String d) {
        return switch (d) {
            case "W"  -> BlockFace.EAST;
            case "E"  -> BlockFace.WEST;
            case "S"  -> BlockFace.NORTH;
            case "N"  -> BlockFace.SOUTH;
            case "NW" -> BlockFace.SOUTH_EAST;
            case "NE" -> BlockFace.SOUTH_WEST;
            case "SW" -> BlockFace.NORTH_EAST;
            case "SE" -> BlockFace.NORTH_WEST;
            default   -> null;
        };
    }

    public static boolean distance(Block block, Location location, double d) {
        return block.getLocation().add(0.5, 0.0, 0.5).distance(location) <= d;
    }

    public static boolean isInvalidLaunchBlock(Block block) {
        Material type = block.getType();
        String   name = type.name();

        if (type == Material.STRING) return true;
        if (name.endsWith("_FENCE")) return true;
        if (name.contains("GLASS_PANE")) return true;
        if (name.endsWith("_WALL")) return true;
        if (name.contains("CHEST")) return true;

        if (block.getBlockData() instanceof Gate gate) {
            return !gate.isOpen();
        }

        return false;
    }

    public static boolean shouldRefundOnHit(Block block, Player player, EnderPearl pearl) {
        if (block == null) return false;

        // Bloques siempre inválidos
        if (isInvalidLaunchBlock(block)) return true;

        // Check de distancia y ángulo para bloques normales
        Location eye = player.getEyeLocation();
        Location center = block.getLocation().add(0.5, 0.5, 0.5);

        if (center.distanceSquared(eye) > 9.0) return false; // radio de 3 bloques

        Vector pearlDir = pearl.getVelocity().normalize();
        Vector toBlock = center.toVector().subtract(eye.toVector()).normalize();

        return pearlDir.dot(toBlock) > 0.85;
    }

    public static boolean isOpaqueBlock(Block block) {
        return !isTransparentBlock(block);
    }

    public static boolean isTransparentBlock(Block block) {
        Material type = block.getType();
        String   name = type.name();
        return type.isTransparent() || block.isLiquid() || name.contains("FENCE") || isSlab(type) || isStairs(type) || List.of(
                Material.COBWEB,
                Material.MOVING_PISTON,
                Material.ANVIL,
                Material.DRAGON_EGG,
                Material.COBBLESTONE_WALL,
                Material.END_PORTAL_FRAME,
                Material.ENCHANTING_TABLE
        ).contains(type) || Stream.of("PLATE", "SIGN", "DAYLIGHT", "BANNER", "CAKE", "DOOR", "CHEST").anyMatch(name::contains);
    }

    public static boolean isStairs(Material material) {
        return material.name().contains("STAIRS");
    }

    public static boolean isSlab(Material material) {
        String name = material.name();
        return name.contains("SLAB") && !name.contains("DOUBLE");
    }

    public static boolean isOpenFenceGate(Block block) {
        return block.getBlockData() instanceof Gate g && g.isOpen();
    }

    public static boolean isRiskyBlock(Block block) {
        Material type = block.getType();
        String   name = type.name();
        return !type.isTransparent() && !block.isLiquid() && type != Material.COBWEB && type != Material.MOVING_PISTON && Stream.of("FENCE", "PLATE", "WALL", "SIGN", "BANNER").noneMatch(name::contains);
    }

    public static boolean isCritBlock(Block block) {
        Material type = block.getType();
        String   name = type.name();
        return !type.isTransparent() && !block.isLiquid() && Stream.of("PLATE", "BANNER", "SIGN").noneMatch(name::contains);
    }
}
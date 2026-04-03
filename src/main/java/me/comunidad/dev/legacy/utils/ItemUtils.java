package me.comunidad.dev.legacy.utils;

import lombok.SneakyThrows;
import me.comunidad.dev.legacy.framework.Manager;
import me.comunidad.dev.legacy.framework.extra.Configs;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

public class ItemUtils {

    private static final Map<String, Material> MATERIALS = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

    private final Configs configs;

    public ItemUtils(Configs configs) {
        this.configs = configs;
        this.load();
    }

    public void load() {
        for (Material material : Material.values()) {
            String path = ("BLOCKS_NEW.") + material.name();

            if (!configs.getItemsConfig().contains(path)) {
                configs.getItemsConfig().set(path, material.name());
                MATERIALS.put(material.name(), material);
                continue;
            }

            String get = configs.getItemsConfig().getString(path);
            String[] names = (get.contains(";") ? get.split(";") : new String[]{get});

            for (String name : names) {
                MATERIALS.put(name, material);
            }
        }

        configs.getItemsConfig().save();
        configs.getItemsConfig().reloadCache(); // clear the cache, no use now.
    }

    public static String getItemName(ItemStack item) {
        if (item == null) {
            return "Hand";
        }

        if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
            return item.getItemMeta().getDisplayName();
        }

        return Utils.capitalize(item.getType().name().toLowerCase().replace('_', ' '));
    }

    public static String getEntityName(Entity entity) {
        return Utils.capitalize(entity.getType().name().toLowerCase().replace('_', ' '));
    }

    public static void giveItem(Player player, ItemStack itemStack, Location drop) {
        if (itemStack.getType() == Material.AIR) return;

        if (player.getInventory().firstEmpty() == -1) {
            for (ItemStack stack : player.getInventory().getContents()) {
                if (!itemStack.isSimilar(stack)) continue;
                if (stack.getAmount() >= stack.getMaxStackSize()) continue;

                int added = stack.getAmount() + itemStack.getAmount();

                if (added <= stack.getMaxStackSize()) {
                    stack.setAmount(added);
                    return;

                } else {
                    stack.setAmount(stack.getMaxStackSize());
                    itemStack.setAmount(added - stack.getMaxStackSize());
                }
            }

            player.getWorld().dropItemNaturally(drop, itemStack);

        } else {
            player.getInventory().addItem(itemStack);
        }
    }

    @SneakyThrows
    public static ItemStack tryGetPotion(Manager manager, Material material, int id) {
        ItemStack itemStack = new ItemStack(material);
        manager.setData(itemStack, id);

        return itemStack;
    }

    public static void giveItem(Player player, ItemStack itemStack) {
        if (itemStack == null || itemStack.getType() == Material.AIR) return;

        if (isArmor(itemStack)) {
            int slot = getArmorSlot(itemStack.getType());
            if (slot != -1) {
                ItemStack currentArmor = player.getInventory().getItem(slot);
                if (currentArmor == null || currentArmor.getType() == Material.AIR) {
                    player.getInventory().setItem(slot, itemStack);
                    player.updateInventory();
                    return;
                }
            }
        }

        if (player.getInventory().firstEmpty() == -1) {
            for (ItemStack stack : player.getInventory().getContents()) {
                if (!itemStack.isSimilar(stack)) continue;
                if (stack.getAmount() >= stack.getMaxStackSize()) continue;

                int added = stack.getAmount() + itemStack.getAmount();

                if (added <= stack.getMaxStackSize()) {
                    stack.setAmount(added);
                    return;

                } else {
                    stack.setAmount(stack.getMaxStackSize());
                    itemStack.setAmount(added - stack.getMaxStackSize());
                }
            }

            player.getWorld().dropItemNaturally(player.getLocation(), itemStack);

        } else {
            player.getInventory().addItem(itemStack);
            player.updateInventory();
        }
    }

    public static boolean isArmor(ItemStack itemStack) {
        Material type = itemStack.getType();
        return type.name().endsWith("_HELMET") || type.name().endsWith("_CHESTPLATE")
                || type.name().endsWith("_LEGGINGS") || type.name().endsWith("_BOOTS");
    }

    public static int getArmorSlot(Material material) {
        if (material.name().endsWith("_HELMET")) return 39;
        if (material.name().endsWith("_CHESTPLATE")) return 38;
        if (material.name().endsWith("_LEGGINGS")) return 37;
        if (material.name().endsWith("_BOOTS")) return 36;
        return -1;
    }

    public static void setData(ItemStack item, int data) {
        if (item.getItemMeta() == null) return;

        Damageable damageable = (Damageable) item.getItemMeta();
        damageable.setDamage(data);
        item.setItemMeta((ItemMeta) damageable);

    }

    public static ItemStack getMatItem(String string) {
        if (string.contains(":")) {
            String[] split = string.split(":");
            ItemStack item = new ItemStack(getMat(split[0]));
            setData(item, Integer.parseInt(split[1]));
            return item;
        }

        if (string.toLowerCase().startsWith("head_")) {
            String ownerName = string.substring(string.indexOf("_") + 1);
            ItemStack item = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) item.getItemMeta();

            if (meta != null) {
                OfflinePlayer owner = Bukkit.getOfflinePlayer(ownerName);
                meta.setOwningPlayer(owner);
                item.setItemMeta(meta);
            }

            return item;
        }


        return new ItemStack(getMat(string));
    }

    public static String itemToBase64(ItemStack item) {
        if (item == null) {
            return "";
        }

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream)) {

            dataOutput.writeObject(item);
            dataOutput.close();
            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (IOException e) {
            throw new IllegalStateException("Unable to save item.", e);
        }
    }

    public static ItemStack itemFromBase64(String data) {
        if (data == null || data.isEmpty()) {
            return null;
        }

        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
             BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream)) {

            ItemStack item = (ItemStack) dataInput.readObject();
            dataInput.close();
            return item;
        } catch (IOException | ClassNotFoundException e) {
            throw new IllegalStateException("Unable to load item.", e);
        }
    }

    public static String arrayToBase64(ItemStack[] items) throws IllegalStateException {
        if(items.length == 0) {
            return "";
        }

        try(ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream)) {

            dataOutput.writeInt(items.length);

            for(ItemStack item : items) {
                dataOutput.writeObject(item);
            }
            dataOutput.close();
            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (IOException e) {
            throw new IllegalStateException("Unable to save items.", e);
        }
    }

    public static ItemStack[] arrayFromBase64(String data) {
        if(data == null || data.isEmpty()) {
            return new ItemStack[0];
        }

        try(ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream)) {

            ItemStack[] items = new ItemStack[dataInput.readInt()];

            for(int i = 0; i < items.length; i++) {
                items[i] = (ItemStack) dataInput.readObject();
            }
            dataInput.close();
            return items;
        } catch (IOException | ClassNotFoundException e) {
            throw new IllegalStateException("Unable to load items.", e);
        }
    }

    public static Material getMat(String string) {
        Material material = MATERIALS.get(string);

        if (material == null) {
            Bukkit.getLogger().warning("[Legacy] Invalid Material: " + string + "! Returning REDSTONE_BLOCK as default!");
            return Material.REDSTONE_BLOCK;
        }

        return material;
    }

    @SneakyThrows
    public static ItemStack createPotion(String potionType, boolean extended, boolean upgraded, boolean splash, @Nullable String path) {
        Material material = splash ? Material.SPLASH_POTION : Material.POTION;
        ItemStack item = new ItemStack(material);

        PotionMeta meta = (PotionMeta) item.getItemMeta();
        if (meta == null) return item;

        PotionType baseType = parsePotionType(potionType);

        boolean canExtend = baseType.isExtendable();
        boolean canUpgrade = baseType.isUpgradeable();

        if (extended && !canExtend && path != null) {
            Bukkit.getLogger().warning(
                    "[Legacy] PotionType " + baseType + " no soporta EXTENDED (" + path + ")"
            );
        }

        if (upgraded && !canUpgrade && path != null) {
            Bukkit.getLogger().warning(
                    "[Legacy] PotionType " + baseType + " no soporta UPGRADED (" + path + ")"
            );
        }

        meta.setBasePotionData(new PotionData(
                baseType,
                canExtend && extended,
                canUpgrade && upgraded
        ));

        item.setItemMeta(meta);
        return item;
    }


    private static PotionType parsePotionType(String potionType) {
        if (potionType == null || potionType.isEmpty()) {
            return PotionType.WATER;
        }

        String normalized = potionType.toUpperCase().trim();

        return switch (normalized) {
            case "SPEED", "SWIFTNESS" -> PotionType.SWIFTNESS;
            case "SLOWNESS", "SLOW" -> PotionType.SLOWNESS;
            case "STRENGTH", "INCREASE_DAMAGE" -> PotionType.STRENGTH;
            case "HEAL", "HEALING", "INSTANT_HEALTH" -> PotionType.HEALING;
            case "HARM", "HARMING", "INSTANT_DAMAGE" -> PotionType.HARMING;
            case "JUMP", "JUMP_BOOST", "LEAPING" -> PotionType.LEAPING;
            case "REGEN", "REGENERATION" -> PotionType.REGENERATION;
            case "POISON" -> PotionType.POISON;
            case "WEAKNESS" -> PotionType.WEAKNESS;
            case "FIRE_RESISTANCE" -> PotionType.FIRE_RESISTANCE;
            case "INVISIBILITY" -> PotionType.INVISIBILITY;
            case "NIGHT_VISION" -> PotionType.NIGHT_VISION;
            case "WATER_BREATHING" -> PotionType.WATER_BREATHING;
            default -> PotionType.WATER;
        };
    }

}
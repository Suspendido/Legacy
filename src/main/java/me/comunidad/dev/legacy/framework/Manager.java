package me.comunidad.dev.legacy.framework;

import lombok.Getter;
import me.comunidad.dev.legacy.Core;
import me.comunidad.dev.legacy.framework.extra.Configs;
import me.comunidad.dev.legacy.utils.CC;
import me.comunidad.dev.legacy.utils.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

import java.lang.reflect.Method;
import java.util.List;

@Getter
public abstract class Manager extends Configs {

    private static Method SET_DATA;
    private final Core instance;

    public Manager(Core instance) {
        this.instance = instance;
        this.instance.getManagers().add(this);
    }

    public void registerListener(Listener listener) {
        instance.getServer().getPluginManager().registerEvents(listener, instance);
    }

    public void setData(ItemStack item, int data) {
        if (item.getItemMeta() == null) return;

        Damageable damageable = (Damageable) item.getItemMeta();
        damageable.setDamage(data);
        item.setItemMeta(damageable);
    }

    public void playSound(Player player, String sound, boolean world) {
        if (world) {
            playSound(player.getLocation(), sound);
        } else {
            Tasks.execute(this, () -> player.playSound(player.getLocation(), getSound(sound), 1.0F, 1.0F));
        }
    }

    public void playSound(Player player, String sound, boolean world, float pitch) {
        if (world) {
            playSound(player.getLocation(), sound);

        } else player.playSound(player.getLocation(), getSound(sound), 1.0F, pitch);
    }

    public void sendMessage(CommandSender player, String s) {
        player.sendMessage(CC.t(s));
    }

    public void sendMessage(CommandSender player, List<String> s) {
       for (String string : s) {
           player.sendMessage(CC.t(string));
       }
    }

    public void broadcast(String... s) {
        for (String string : s) {
            Bukkit.broadcastMessage(CC.t(string));
        }
    }

    public void playSound(Location location, String sound) {
        Tasks.execute(this, () -> location.getWorld().playSound(location, getSound(sound), 1.0F, 1.0F));
    }

    public Sound getSound(String sound) {
        Sound toCreate = null;

        if (sound.equalsIgnoreCase("LEVEL_UP")) {
            toCreate = Sound.valueOf("ENTITY_PLAYER_LEVELUP");
        }

        if (sound.equalsIgnoreCase("ORB_PICKUP")) {
            toCreate = Sound.valueOf("ENTITY_EXPERIENCE_ORB_PICKUP");
        }

        if (sound.equalsIgnoreCase("ITEM_BREAK")) {
            toCreate = Sound.valueOf("ENTITY_ITEM_BREAK");
        }

        if (sound.equalsIgnoreCase("NOTE_BASS_DRUM")) {
            toCreate = Sound.valueOf("BLOCK_NOTE_BLOCK_BASEDRUM");
        }

        if (sound.equalsIgnoreCase("CLICK")) {
            toCreate = Sound.valueOf("UI_BUTTON_CLICK");
        }

        if (toCreate == null) {
            try {
                toCreate = Sound.valueOf(sound);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Sound " + sound + " is wrong!");
            }
        }

        return toCreate;
    }

    public int getData(ItemStack item) {
        if (item.getItemMeta() == null) return 0;

        Damageable damageable = (Damageable) item.getItemMeta();
        return damageable.getDamage();
    }

    public boolean isGapple(ItemStack item) {
        if (item == null) return false;
        Material type = item.getType();

        return type == Material.ENCHANTED_GOLDEN_APPLE;
    }

    public void setItemInHand(Player player, ItemStack item) {
        player.getInventory().setItemInMainHand(item);
        player.updateInventory();
    }

    /**
     * Sets an item in the player's offhand
     */
    public void setItemInOffHand(Player player, ItemStack item) {
        player.getInventory().setItemInOffHand(item);
        player.updateInventory();
    }

    public ItemStack getItemInHand(Player player) {
        ItemStack hand = player.getInventory().getItemInMainHand();

        if (hand.getType() == Material.AIR) {
            return null;
        }

        return hand;
    }

    public ItemStack getItemInOffHand(Player player) {
        ItemStack offHand = player.getInventory().getItemInOffHand();

        if (offHand.getType() == Material.AIR) {
            return null;
        }

        return offHand;
    }

    public ItemStack getItemFromEitherHand(Player player) {
        ItemStack mainHand = getItemInHand(player);
        if (mainHand != null) {
            return mainHand;
        }

        return getItemInOffHand(player);
    }

    public boolean hasItemInHands(Player player) {
        return getItemInHand(player) != null || getItemInOffHand(player) != null;
    }

    public void takeItemInHand(Player player, int amount) {
        ItemStack hand = getItemInHand(player);

        if (hand == null) return;

        if (hand.getAmount() <= 1) {
            setItemInHand(player, new ItemStack(Material.AIR));

        } else {
            hand.setAmount(hand.getAmount() - amount);
        }

        player.updateInventory();
    }

    public void enable() {
    }

    public void disable() {
    }

    public void reload() {
    }
}
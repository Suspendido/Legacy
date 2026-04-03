package me.comunidad.dev.legacy.utils;

import lombok.SneakyThrows;
import me.comunidad.dev.legacy.framework.Manager;
import me.comunidad.dev.legacy.utils.extra.Triple;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

public class PotionUtils {

    private static Triple<UUID, PotionEffectType, PotionEffect> restores;

    public PotionUtils(Triple<UUID, PotionEffectType, PotionEffect> restores) {
        PotionUtils.restores = new Triple<>();
    }

    @SneakyThrows
    public static ItemStack tryGetPotion(Manager manager, Material material, int id) {
        ItemStack itemStack = new ItemStack(material);
        manager.setData(itemStack, id);

        return itemStack;
    }

    public static void addEffect(Player player, PotionEffect effect) {
        if (effect == null) return;

        // if they don't have a current effect to restore just add it normally
        if (!player.hasPotionEffect(effect.getType())) {
            player.addPotionEffect(effect);
            return;
        }

        for (PotionEffect activeEffect : player.getActivePotionEffects()) {
            // Just some checks to make sure we aren't overriding better effects
            if (!activeEffect.getType().equals(effect.getType())) continue;
            if (activeEffect.getAmplifier() > effect.getAmplifier()) break; // Use breaks now since its only 1 effect

            // Don't override if same level but has higher duration.
            if (activeEffect.getAmplifier() == effect.getAmplifier() &&
                    activeEffect.getDuration() > effect.getDuration()) break;

            // Make sure the active effect is longer than the effect
            // otherwise we will be restoring an effect that had already expired.
            if (activeEffect.getDuration() > effect.getDuration()) {
                restores.put(player.getUniqueId(), activeEffect.getType(), activeEffect);
                player.removePotionEffect(activeEffect.getType()); // Remove it so 1.16 spigot doesn't restore it.
            }

            player.addPotionEffect(effect, true); // override old one
            break;
        }
    }

}

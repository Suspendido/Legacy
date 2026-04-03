package me.comunidad.dev.legacy.module.combat.menu.editor;

import me.comunidad.dev.legacy.framework.menu.Menu;
import me.comunidad.dev.legacy.framework.menu.MenuManager;
import me.comunidad.dev.legacy.framework.menu.button.Button;
import me.comunidad.dev.legacy.module.lang.Lang;
import me.comunidad.dev.legacy.module.lang.LangManager;
import me.comunidad.dev.legacy.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;

/*
 * Copyright (c) 2026. @Comunidad, made since 2/4/2026
 * Use or redistribution of this source file is only permitted
 * if explicit permission is given by the author.
 */
public class ProfileEditorMenu extends Menu {

    private final String profileId;
    private final String path;

    public ProfileEditorMenu(MenuManager manager, Player player, String profileId) {
        super(manager, player, manager.getInstance().getLangManager().of(Lang.PROFILE_EDITOR_TITLE, profileId), 54, false);
        this.profileId = profileId;
        this.path = "profiles." + profileId;

        ItemStack item = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE)
                .setName(" ")
                .toItemStack();
        ItemMeta meta = item.getItemMeta();
        meta.setHideTooltip(true);
        item.setItemMeta(meta);

        setFillEnabled(true);
        setFiller(item);
    }

    private LangManager lang() {
        return getInstance().getLangManager();
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        buttons.put(12, new Button() {
            @Override
            public ItemStack getItemStack() {
                double h = getProfilesConfig().getDouble(path + ".knockback.horizontal");
                double v = getProfilesConfig().getDouble(path + ".knockback.vertical");
                return new ItemBuilder(Material.BLAZE_ROD)
                        .setName(lang().of(Lang.PROFILE_KB_NAME))
                        .setLore(lang().loreOf(Lang.PROFILE_KB_LORE, h, v))
                        .toItemStack();
            }
            @Override
            public void onClick(InventoryClickEvent e) {
                e.setCancelled(true);
                playNeutral(player);
                new KnockbackEditorMenu(getManager(), player, profileId).open();
            }
        });

        buttons.put(14, new Button() {
            @Override
            public ItemStack getItemStack() {
                int ticks = getProfilesConfig().getInt(path + ".combat.no-damage-ticks.player");
                return new ItemBuilder(Material.IRON_SWORD)
                        .setName(lang().of(Lang.PROFILE_COMBAT_NAME))
                        .setLore(lang().loreOf(Lang.PROFILE_COMBAT_LORE, ticks))
                        .toItemStack();
            }
            @Override
            public void onClick(InventoryClickEvent e) {
                e.setCancelled(true);
                playNeutral(player);
                new CombatSettingsMenu(getManager(), player, profileId).open();
            }
        });

        buttons.put(16, new Button() {
            @Override
            public ItemStack getItemStack() {
                return new ItemBuilder(Material.DIAMOND_SWORD)
                        .setName(lang().of(Lang.PROFILE_DAMAGE_NAME))
                        .setLore(lang().loreOf(Lang.PROFILE_DAMAGE_LORE))
                        .toItemStack();
            }
            @Override
            public void onClick(InventoryClickEvent e) {
                e.setCancelled(true);
                playNeutral(player);
                new DamageToolMenu(getManager(), player, profileId).open();
            }
        });

        buttons.put(30, new Button() {
            @Override
            public ItemStack getItemStack() {
                double speed = getProfilesConfig().getDouble(path + ".projectiles.potion.speed-multiplier");
                double gravity = getProfilesConfig().getDouble(path + ".projectiles.arrow.gravity");
                return new ItemBuilder(Material.ARROW)
                        .setName(lang().of(Lang.PROFILE_PROJECTILE_NAME))
                        .setLore(lang().loreOf(Lang.PROFILE_PROJECTILE_LORE, speed, gravity))
                        .toItemStack();
            }
            @Override
            public void onClick(InventoryClickEvent e) {
                e.setCancelled(true);
                playNeutral(player);
                new ProjectileEditorMenu(getManager(), player, profileId).open();
            }
        });

        buttons.put(32, new Button() {
            @Override
            public ItemStack getItemStack() {
                double dmg = getProfilesConfig().getDouble(path + ".pearls.damage");
                int cd = getProfilesConfig().getInt(path + ".pearls.cooldown-ticks");
                return new ItemBuilder(Material.ENDER_PEARL)
                        .setName(lang().of(Lang.PROFILE_PEARL_NAME))
                        .setLore(lang().loreOf(Lang.PROFILE_PEARL_LORE, dmg, cd))
                        .toItemStack();
            }
            @Override
            public void onClick(InventoryClickEvent e) {
                e.setCancelled(true);
                playNeutral(player);
                new PearlEditorMenu(getManager(), player, profileId).open();
            }
        });

        buttons.put(34, new Button() {
            @Override
            public ItemStack getItemStack() {
                double speed = getProfilesConfig().getDouble(path + ".projectiles.potion.speed-multiplier");
                double boost = getProfilesConfig().getDouble(path + ".potions.self-intensity-boost");
                return new ItemBuilder(Material.SPLASH_POTION)
                        .setName(lang().of(Lang.PROFILE_POTION_NAME))
                        .setLore(lang().loreOf(Lang.PROFILE_POTION_LORE, speed, boost))
                        .toItemStack();
            }
            @Override
            public void onClick(InventoryClickEvent e) {
                e.setCancelled(true);
                playNeutral(player);
                new PotionEditorMenu(getManager(), player, profileId).open();
            }
        });

        buttons.put(50, backButton(() -> new ManageCombatMenu(getManager(), player).open()));

        return buttons;
    }

    private Button backButton(Runnable action) {
        return new Button() {
            @Override
            public ItemStack getItemStack() {
                return new ItemBuilder(Material.ARROW)
                        .setName(lang().of(Lang.BACK_BUTTON_NAME))
                        .setLore(lang().loreOf(Lang.BACK_BUTTON_LORE))
                        .toItemStack();
            }
            @Override
            public void onClick(InventoryClickEvent e) {
                e.setCancelled(true);
                playNeutral(player);
                action.run();
            }
        };
    }
}
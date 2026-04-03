package me.comunidad.dev.legacy.module.combat.menu;

import me.comunidad.dev.legacy.framework.menu.Menu;
import me.comunidad.dev.legacy.framework.menu.MenuManager;
import me.comunidad.dev.legacy.framework.menu.button.Button;
import me.comunidad.dev.legacy.module.combat.menu.editor.*;
import me.comunidad.dev.legacy.module.combat.menu.lang.LanguagesMenu;
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
public class MainMenu extends Menu {

    public MainMenu(MenuManager manager, Player player) {
        super(manager, player,
                manager.getInstance().getLangManager().of(Lang.MAIN_MENU_TITLE),
                27,
                false
        );
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
            @Override public ItemStack getItemStack() {
                return new ItemBuilder(Material.NETHER_STAR)
                        .setName(lang().of(Lang.MAIN_PROFILES_NAME))
                        .setLore(lang().loreOf(Lang.MAIN_PROFILES_LORE))
                        .toItemStack();
            }
            @Override
            public void onClick(InventoryClickEvent e) {
                e.setCancelled(true);
                playNeutral(player);
                new ManageCombatMenu(getManager(), player).open();
            }
        });

        buttons.put(14, new Button() {
            @Override
            public ItemStack getItemStack() {
                return new ItemBuilder(Material.DIAMOND_SWORD)
                        .setName(lang().of(Lang.MAIN_DAMAGE_NAME))
                        .setLore(lang().loreOf(Lang.MAIN_DAMAGE_LORE))
                        .toItemStack();
            }
            @Override
            public void onClick(InventoryClickEvent e) {
                e.setCancelled(true);
                playNeutral(player);
                new DamageToolMenu(getManager(), player).open();
            }
        });

        buttons.put(16, new Button() {
            @Override
            public ItemStack getItemStack() {
                return new ItemBuilder(Material.PAPER)
                        .setName(lang().of(Lang.MAIN_LANGUAGES_NAME))
                        .setLore(lang().loreOf(Lang.MAIN_LANGUAGES_LORE))
                        .toItemStack();
            }

            @Override
            public void onClick(InventoryClickEvent e) {
                e.setCancelled(true);
                playNeutral(player);
                new LanguagesMenu(getManager(), player).open();
            }
        });

        return buttons;
    }
}
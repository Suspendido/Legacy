package me.comunidad.dev.legacy.module.combat.menu.lang;

import me.comunidad.dev.legacy.framework.menu.Menu;
import me.comunidad.dev.legacy.framework.menu.MenuManager;
import me.comunidad.dev.legacy.framework.menu.button.Button;
import me.comunidad.dev.legacy.module.combat.menu.MainMenu;
import me.comunidad.dev.legacy.module.lang.Lang;
import me.comunidad.dev.legacy.module.lang.LangManager;
import me.comunidad.dev.legacy.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * Copyright (c) 2026. @Comunidad, made since 2/4/2026
 * Use or redistribution of this source file is only permitted
 * if explicit permission is given by the author.
 */
public class LanguagesMenu extends Menu {

    // { langId, displayName, icon }
    private static final Object[][] LANGUAGES = {
            {"es_lang", "§6Español", Material.ORANGE_TERRACOTTA, "&aTraducción Oficial"},
            {"en_lang", "§bEnglish", Material.LIGHT_BLUE_TERRACOTTA, "&aOfficial Translation"},
            {"pt_lang", "§aPortuguês", Material.GREEN_TERRACOTTA, "&aTradução oficial"},
            {"fr_lang", "§9Français", Material.BLUE_TERRACOTTA, "&c&o0% Translated"},
            {"de_lang", "§7Deutsch", Material.GRAY_TERRACOTTA, "&c&o0% Translated"},
            {"it_lang", "§cItaliano", Material.RED_TERRACOTTA, "&c&o0% Translated"},
            {"zh_lang", "§dChinese (中文)", Material.PINK_TERRACOTTA, "&c&o0% Translated"},
            {"ru_lang", "§4Русский", Material.BROWN_TERRACOTTA, "&c&o0% Translated"},
    };

    private static final int[] SLOTS = {11, 13, 15, 17, 29, 31, 33, 35};

    public LanguagesMenu(MenuManager manager, Player player) {
        super(manager, player, manager.getInstance().getLangManager().of(Lang.LANG_MENU_TITLE), 54, false);
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

        String activeLang = lang().getActiveId();

        for (int i = 0; i < LANGUAGES.length; i++) {
            final String id = (String) LANGUAGES[i][0];
            final String name = (String) LANGUAGES[i][1];
            final Material mat = (Material) LANGUAGES[i][2];
            final String desc = (String) LANGUAGES[i][3];
            final boolean active = id.equals(activeLang);
            final int slot = SLOTS[i];

            buttons.put(slot, new Button() {
                @Override
                public ItemStack getItemStack() {
                    List<String> lore = new ArrayList<>();
                    lore.add(lang().of(Lang.LANG_LORE_ID, id));
                    lore.add(lang().of(Lang.LANG_LORE_STATUS, active ? lang().of(Lang.LANG_STATUS_ACTIVE) : lang().of(Lang.LANG_STATUS_INACTIVE)));
                    lore.add("");
                    lore.add(desc);
                    lore.add("");
                    lore.add(active ? lang().of(Lang.LANG_LORE_SELECTED) : lang().of(Lang.LANG_LORE_CLICK));
                    return new ItemBuilder(mat).setName(name).setLore(lore).toItemStack();
                }

                @Override
                public void onClick(InventoryClickEvent e) {
                    e.setCancelled(true);
                    if (active) {
                        playFail(player);
                        sendMessage(player, lang().of(Lang.LANG_ALREADY_ACTIVE, name));
                        return;
                    }

                    lang().reload(id);
                    sendMessage(player, lang().of(Lang.LANG_CHANGED, name));
                    playSuccess(player);
                    update();
                }
            });
        }

        buttons.put(50, new Button() {
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
                new MainMenu(getManager(), player).open();
            }
        });

        return buttons;
    }
}
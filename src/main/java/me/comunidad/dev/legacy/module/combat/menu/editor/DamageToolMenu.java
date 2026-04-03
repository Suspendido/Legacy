package me.comunidad.dev.legacy.module.combat.menu.editor;

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
public class DamageToolMenu extends Menu {

    private final String profileId;
    private final String base;

    private static final Object[][] TOOLS = {
            {"Wood Sword", "swords.wood", Material.WOODEN_SWORD, 4.0},
            {"Gold Sword", "swords.gold", Material.GOLDEN_SWORD, 4.0},
            {"Stone Sword", "swords.stone", Material.STONE_SWORD, 5.0},
            {"Iron Sword", "swords.iron", Material.IRON_SWORD, 6.0},
            {"Diamond Sword", "swords.diamond", Material.DIAMOND_SWORD, 7.0},
            {"Netherite Sword", "swords.netherite", Material.NETHERITE_SWORD, 7.0},
            {"Wood Axe", "axes.wood", Material.WOODEN_AXE, 3.0},
            {"Stone Axe", "axes.stone", Material.STONE_AXE, 4.0},
            {"Iron Axe", "axes.iron", Material.IRON_AXE, 5.0},
            {"Diamond Axe", "axes.diamond", Material.DIAMOND_AXE, 6.0},
            {"Pickaxe", "pickaxe", Material.IRON_PICKAXE, 2.0},
            {"Shovel", "shovel", Material.IRON_SHOVEL, 1.0},
            {"Hoe", "hoe", Material.IRON_HOE, 1.0},
            {"Trident", "trident", Material.TRIDENT, 8.0},
    };

    private static final int[] SLOTS = {
            11, 12, 13, 14, 15, 16, 17,
            20, 21, 22, 23, 24, 25, 26
    };

    public DamageToolMenu(MenuManager manager, Player player) {
        this(manager, player, null);
    }

    public DamageToolMenu(MenuManager manager, Player player, String profileId) {
        super(manager, player,
                profileId == null
                        ? manager.getInstance().getLangManager().of(Lang.DAMAGE_MENU_TITLE_GLOBAL)
                        : manager.getInstance().getLangManager().of(Lang.DAMAGE_MENU_TITLE_PROFILE, profileId),
                5*9, false
        );
        this.profileId = profileId;
        this.base = profileId == null ? "global.tool-damage." : "profiles." + profileId + ".tool-damage.";
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

        for (int i = 0; i < TOOLS.length; i++) {
            final String sub = (String)TOOLS[i][1];
            final String label = (String)TOOLS[i][0];
            final Material icon = (Material)TOOLS[i][2];
            final double def = (double)TOOLS[i][3];
            final String path = base + sub;
            final int slot = SLOTS[i];

            buttons.put(slot, new Button() {
                @Override
                public ItemStack getItemStack() {
                    double val = getProfilesConfig().contains(path) ? getProfilesConfig().getDouble(path) : def;
                    List<String> lore = new ArrayList<>();
                    lore.add(lang().of(Lang.DAMAGE_CURRENT, val));
                    lore.add("");
                    lore.add(lang().of(Lang.KB_HINT_LEFT, 0.5));
                    lore.add(lang().of(Lang.KB_HINT_RIGHT, 0.5));
                    lore.add(lang().of(Lang.KB_HINT_SHIFT_LEFT, 1.0));
                    lore.add(lang().of(Lang.KB_HINT_SHIFT_RIGHT, 1.0));
                    return new ItemBuilder(icon).setName("&6" + label).setLore(lore).toItemStack();
                }

                @Override
                public void onClick(InventoryClickEvent e) {
                    e.setCancelled(true);
                    double delta = switch (e.getClick()) {
                        case LEFT ->  0.5;
                        case RIGHT -> -0.5;
                        case SHIFT_LEFT ->  1.0;
                        case SHIFT_RIGHT -> -1.0;
                        default -> 0;
                    };
                    if (delta == 0) return;

                    double current = getProfilesConfig().contains(path) ? getProfilesConfig().getDouble(path) : def;
                    double next = Math.max(0, round(current + delta));
                    getProfilesConfig().set(path, next);
                    getProfilesConfig().reloadCache();
                    getProfilesConfig().save();
                    getInstance().getProfileManager().reload();
                    update();
                    sendMessage(player, lang().of(Lang.DAMAGE_UPDATED, label, next));
                    playNeutral(player);
                }
            });
        }

        buttons.put(41, backButton(() -> {
            if (profileId == null) new MainMenu(getManager(), player).open();
            else new ProfileEditorMenu(getManager(), player, profileId).open();
        }));

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

    private double round(double v) {
        return Math.round(v * 10.0) / 10.0;
    }
}
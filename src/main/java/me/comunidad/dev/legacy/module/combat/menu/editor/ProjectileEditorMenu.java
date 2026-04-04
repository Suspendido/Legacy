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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * Copyright (c) 2026. @Comunidad, made since 2/4/2026
 * Use or redistribution of this source file is only permitted
 * if explicit permission is given by the author.
 */
public class ProjectileEditorMenu extends Menu {

    private final String profileId;
    private final String base;

    private static final Object[][] FIELDS = {
            {"Potion Speed Multiplier", "potion.speed-multiplier", 0.05, 0.0, 5.0, Material.SPLASH_POTION},
            {"Potion Y Offset", "potion.y-offset", 0.01, 0.0, 1.0, Material.GLASS_BOTTLE},
            {"Potion Player Vel X", "potion.player-vel-x", 0.05, 0.0, 2.0, Material.CYAN_DYE},
            {"Potion Player Vel Z", "potion.player-vel-z", 0.05, 0.0, 2.0, Material.CYAN_DYE},
            {"Arrow Speed", "arrow.speed", 0.1, 0.1, 10.0, Material.ARROW},
            {"Arrow Gravity", "arrow.gravity", 0.005, 0.0, 1.0, Material.FEATHER},
            {"Snowball Speed", "snowball.speed", 0.1, 0.1, 10.0, Material.SNOWBALL},
            {"Snowball Gravity", "snowball.gravity", 0.005, 0.0, 1.0, Material.SNOWBALL},
            {"Trident Speed", "trident.speed", 0.1, 0.1, 10.0, Material.TRIDENT},
    };

    private static final int[] SLOTS = {11, 12, 13, 14, 15, 16, 17, 29, 30};

    public ProjectileEditorMenu(MenuManager manager, Player player, String profileId) {
        super(manager, player, manager.getInstance().getLangManager().of(Lang.PROJ_EDITOR_TITLE, profileId), 54, false);
        this.profileId = profileId;
        this.base = "profiles." + profileId + ".projectiles.";

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

        for (int i = 0; i < FIELDS.length; i++) {
            final String label = (String)FIELDS[i][0];
            final String sub = (String)FIELDS[i][1];
            final double step = ((Number)FIELDS[i][2]).doubleValue();
            final double min = ((Number)FIELDS[i][3]).doubleValue();
            final double max = ((Number)FIELDS[i][4]).doubleValue();
            final Material mat = (Material)FIELDS[i][5];
            final String path = base + sub;
            final int slot = SLOTS[i];

            buttons.put(slot, new Button() {
                @Override
                public ItemStack getItemStack() {
                    double val = getProfilesConfig().getDouble(path);
                    List<String> lore = new ArrayList<>();
                    lore.add(lang().of(Lang.VALUE_CURRENT, round(val)));
                    lore.add(lang().of(Lang.VALUE_RANGE, min, max));
                    lore.add("");
                    lore.add(lang().of(Lang.KB_HINT_LEFT, step));
                    lore.add(lang().of(Lang.KB_HINT_RIGHT, step));
                    lore.add(lang().of(Lang.KB_HINT_SHIFT_LEFT, step * 5));
                    lore.add(lang().of(Lang.KB_HINT_SHIFT_RIGHT, step * 5));
                    return new ItemBuilder(mat).setName("&d" + label).setLore(lore).toItemStack();
                }

                @Override
                public void onClick(InventoryClickEvent e) {
                    e.setCancelled(true);
                    double delta = switch (e.getClick()) {
                        case LEFT -> step;
                        case RIGHT -> -step;
                        case SHIFT_LEFT -> step * 5;
                        case SHIFT_RIGHT -> -step * 5;
                        default -> 0;
                    };
                    if (delta == 0) return;

                    double next = Math.min(max, Math.max(min, round(getProfilesConfig().getDouble(path) + delta)));
                    getProfilesConfig().set(path, next);
                    getProfilesConfig().reloadCache();
                    getProfilesConfig().save();
                    getInstance().getProfileManager().reload();
                    update();
                    sendMessage(player, lang().of(Lang.VALUE_UPDATED, label, next));
                    playNeutral(player);
                }
            });
        }

        buttons.put(50, backButton(() -> new ProfileEditorMenu(getManager(), player, profileId).open()));
        return buttons;
    }

    private Button backButton(Runnable a) {
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
                a.run();
            }
        };
    }

    private double round(double v) {
        return Math.round(v * 1000.0) / 1000.0;
    }
}
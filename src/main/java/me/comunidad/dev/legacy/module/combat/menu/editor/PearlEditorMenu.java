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
public class PearlEditorMenu extends Menu {

    private final String profileId;
    private final String base;

    private static final Object[][] FIELDS = {
            {"Damage on Land", "damage", 0.5, 0.0, 20.0, Material.ENDER_PEARL, false, false},
            {"Speed on Launch", "speed", 0.005, 0.0, 1, Material.SUGAR, false, false},
            {"Pearl Gravity", "gravity", 0.005, 0.0, 1, Material.FEATHER, false, false},
            {"Cooldown (ticks)", "cooldown-ticks", 1, 0, 2000, Material.CLOCK, true,  false},
    };

    private static final int[] SLOTS = {11, 13, 15, 17};

    public PearlEditorMenu(MenuManager manager, Player player, String profileId) {
        super(manager, player, manager.getInstance().getLangManager().of(Lang.PEARL_EDITOR_TITLE, profileId), 3*9, false);
        this.profileId = profileId;
        this.base = "profiles." + profileId + ".pearls.";

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
            final boolean isInt = (boolean)FIELDS[i][6];
            final boolean toggle = (boolean)FIELDS[i][7];
            final String path = base + sub;
            final int slot = SLOTS[i];

            buttons.put(slot, new Button() {
                @Override
                public ItemStack getItemStack() {
                    double val = getProfilesConfig().getDouble(path);
                    List<String> lore = new ArrayList<>();
                    if (toggle) {
                        lore.add(lang().of(Lang.VALUE_CURRENT) + (val == 1 ? lang().of(Lang.PEARL_TOGGLE_ON) : lang().of(Lang.PEARL_TOGGLE_OFF)));
                        lore.add("");
                        lore.add(lang().of(Lang.LANG_LORE_CLICK)); // reuse "click to activate"
                    } else {
                        lore.add(lang().of(Lang.VALUE_CURRENT, fmt(val, isInt)));
                        lore.add(lang().of(Lang.VALUE_RANGE, fmt(min, isInt), fmt(max, isInt)));
                        lore.add("");
                        lore.add(lang().of(Lang.KB_HINT_LEFT, 1));
                        lore.add(lang().of(Lang.KB_HINT_RIGHT, 1));
                        lore.add(lang().of(Lang.KB_HINT_SHIFT_LEFT, fmt(step, isInt)));
                        lore.add(lang().of(Lang.KB_HINT_SHIFT_RIGHT, fmt(step, isInt)));
                    }
                    return new ItemBuilder(mat).setName("&5" + label).setLore(lore).toItemStack();
                }

                @Override
                public void onClick(InventoryClickEvent e) {
                    e.setCancelled(true);
                    if (toggle) {
                        double cur = getProfilesConfig().getDouble(path);
                        getProfilesConfig().set(path, cur == 1 ? 0 : 1);
                    } else {
                        double delta = switch (e.getClick()) {
                            case LEFT ->  step;
                            case RIGHT -> -step;
                            case SHIFT_LEFT ->  step * 5;
                            case SHIFT_RIGHT -> -step * 5;
                            default -> 0;
                        };
                        if (delta == 0) return;
                        double next = Math.min(max, Math.max(min, round(getProfilesConfig().getDouble(path) + delta)));
                        if (isInt) getProfilesConfig().set(path, (int) next);
                        else getProfilesConfig().set(path, next);
                    }
                    getProfilesConfig().reloadCache();
                    getProfilesConfig().save();
                    getInstance().getProfileManager().reload();
                    update();
                    sendMessage(player, lang().of(Lang.PEARL_UPDATED, label));
                }
            });
        }

        buttons.put(23, backButton(() -> new ProfileEditorMenu(getManager(), player, profileId).open()));
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

    private String fmt(double v, boolean asInt) {
        return asInt ? String.valueOf((int) v) : String.valueOf(round(v));
    }
    private double round(double v) {
        return Math.round(v * 100.0) / 100.0;
    }
}
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
public class KnockbackEditorMenu extends Menu {

    private final String profileId;
    private final String base;

    private static final Object[][] FIELDS = {
            {"Horizontal", "horizontal", 0.005, 0.0, 5.0, Material.BLAZE_ROD},
            {"Vertical", "vertical", 0.005, 0.0, 5.0, Material.BLAZE_POWDER},
            {"Vertical Limit", "vertical-limit", 0.005, 0.0, 5.0, Material.MAGMA_CREAM},
            {"Extra Vertical", "extra-vertical", 0.005, 0.0, 5.0, Material.FIRE_CHARGE},
            {"Extra Horizontal", "extra-horizontal", 0.005, 0.0, 5.0, Material.GLOWSTONE_DUST},
            {"Sprint Modifier", "sprint-modifier", 0.05,  0.0, 5.0, Material.FEATHER},
            {"Sprint Reset Modifier", "sprint-reset-mod", 0.05,  0.0, 5.0, Material.RABBIT_FOOT},
    };

    private static final int[] SLOTS = {11, 13, 15, 17, 30, 32, 34};

    public KnockbackEditorMenu(MenuManager manager, Player player, String profileId) {
        super(manager, player, manager.getInstance().getLangManager().of(Lang.KB_EDITOR_TITLE, profileId), 54, false);
        this.profileId = profileId;
        this.base = "profiles." + profileId + ".knockback.";

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
            final double step = (double)FIELDS[i][2];
            final double min = (double)FIELDS[i][3];
            final double max = (double)FIELDS[i][4];
            final Material mat = (Material)FIELDS[i][5];
            final String path = base + sub;
            final int slot = SLOTS[i];

            buttons.put(slot, new Button() {
                @Override
                public ItemStack getItemStack() {
                    double val = getProfilesConfig().getDouble(path);
                    List<String> lore = new ArrayList<>();
                    lore.add(lang().of(Lang.KB_CURRENT_VALUE, round(val)));
                    lore.add(lang().of(Lang.KB_RANGE, min, max));
                    lore.add("");
                    lore.add(lang().of(Lang.KB_HINT_LEFT, step));
                    lore.add(lang().of(Lang.KB_HINT_RIGHT, step));
                    lore.add(lang().of(Lang.KB_HINT_SHIFT_LEFT, step * 5));
                    lore.add(lang().of(Lang.KB_HINT_SHIFT_RIGHT, step * 5));
                    return new ItemBuilder(mat)
                            .setName("&e" + label)
                            .setLore(lore)
                            .toItemStack();
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
                    sendMessage(player, lang().of(Lang.KB_UPDATED, label, next));
                    playNeutral(player);
                }
            });
        }

        buttons.put(50, backButton(() -> new ProfileEditorMenu(getManager(), player, profileId).open()));
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
                playNeutral(player);
                e.setCancelled(true);
                action.run();
            }
        };
    }

    private double round(double v) {
        return Math.round(v * 1000.0) / 1000.0;
    }
}
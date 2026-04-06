package me.comunidad.dev.legacy.module.combat.menu.editor.knockback;

import me.comunidad.dev.legacy.framework.menu.Menu;
import me.comunidad.dev.legacy.framework.menu.MenuManager;
import me.comunidad.dev.legacy.framework.menu.button.Button;
import me.comunidad.dev.legacy.module.combat.menu.editor.ProfileEditorMenu;
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
 * Copyright (c) 2026. @Comunidad, made since 4/4/2026
 * Use or redistribution of this source file is only permitted
 * if explicit permission is given by the author.
 */
public class KbMainEditorMenu extends Menu {

    private final String profileId;

    public KbMainEditorMenu(MenuManager manager, Player player, String profileId) {
        super(manager, player, manager.getInstance().getLangManager().of(Lang.KB_MAIN_EDITOR_TITLE, profileId), 27, false);
        this.profileId = profileId;

        ItemStack filler = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setName(" ").toItemStack();
        ItemMeta meta = filler.getItemMeta();
        meta.setHideTooltip(true);
        filler.setItemMeta(meta);
        setFillEnabled(true);
        setFiller(filler);
    }

    private LangManager lang() { return getInstance().getLangManager(); }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        // ── Slot 11: Hit Knockback ─────────────────────────────────────────
        buttons.put(11, new Button() {
            @Override
            public ItemStack getItemStack() {
                return new ItemBuilder(Material.IRON_SWORD)
                        .setName(lang().of(Lang.KB_HIT_EDITOR_NAME))
                        .setLore(lang().loreOf(Lang.KB_HIT_EDITOR_LORE))
                        .toItemStack();
            }

            @Override
            public void onClick(InventoryClickEvent e) {
                e.setCancelled(true);
                playNeutral(player);
                new KnockbackEditorMenu(getManager(), player, profileId).open();
            }
        });

        // ── Slot 13: Projectile Knockback ──────────────────────────────────
        buttons.put(13, new Button() {
            @Override
            public ItemStack getItemStack() {
                return new ItemBuilder(Material.SNOWBALL)
                        .setName(lang().of(Lang.KB_PROJECTILE_EDITOR_NAME))
                        .setLore(lang().loreOf(Lang.KB_PROJECTILE_EDITOR_LORE))
                        .toItemStack();
            }

            @Override
            public void onClick(InventoryClickEvent e) {
                e.setCancelled(true);
                playNeutral(player);
                new ProjectileKnockbackEditorMenu(getManager(), player, profileId).open();
            }
        });

        // ── Slot 15: Rod Knockback ─────────────────────────────────────────
        buttons.put(15, new Button() {
            @Override
            public ItemStack getItemStack() {
                return new ItemBuilder(Material.FISHING_ROD)
                        .setName(lang().of(Lang.KB_ROD_EDITOR_NAME))
                        .setLore(lang().loreOf(Lang.KB_ROD_EDITOR_LORE))
                        .toItemStack();
            }

            @Override
            public void onClick(InventoryClickEvent e) {
                e.setCancelled(true);
                playNeutral(player);
                new RodKnockbackEditorMenu(getManager(), player, profileId).open();
            }
        });

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
}
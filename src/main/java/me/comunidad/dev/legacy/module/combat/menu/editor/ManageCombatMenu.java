package me.comunidad.dev.legacy.module.combat.menu.editor;

import me.comunidad.dev.legacy.framework.menu.MenuManager;
import me.comunidad.dev.legacy.framework.menu.button.Button;
import me.comunidad.dev.legacy.framework.menu.paginated.PaginatedMenu;
import me.comunidad.dev.legacy.module.combat.menu.MainMenu;
import me.comunidad.dev.legacy.module.lang.Lang;
import me.comunidad.dev.legacy.module.lang.LangManager;
import me.comunidad.dev.legacy.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/*
 * Copyright (c) 2026. @Comunidad, made since 2/4/2026
 * Use or redistribution of this source file is only permitted
 * if explicit permission is given by the author.
 */
public class ManageCombatMenu extends PaginatedMenu {

    public ManageCombatMenu(MenuManager manager, Player player) {
        super(manager, player,
                manager.getInstance().getLangManager().of(Lang.MANAGE_MENU_TITLE),
                3*9, true
        );
        this.defaultButtons.put(5, backButton(() -> new MainMenu(getManager(), player).open()));
    }

    private LangManager lang() {
        return getInstance().getLangManager();
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        Set<String> profileKeys = getProfilesConfig().getConfigurationSection("profiles") != null ? getProfilesConfig().getConfigurationSection("profiles").getKeys(false) : Set.of();
        String activeId = getInstance().getProfileManager() != null ? getInstance().getProfileManager().getActiveId() : null;

        int slot = 1;
        for (String profileId : profileKeys) {
            final String id = profileId;
            final boolean isActive = id.equals(activeId);

            String displayName = getProfilesConfig().getString("profiles." + id + ".display-name");
            if (displayName == null || displayName.isBlank()) displayName = "&f" + id;

            List<String> lore = new ArrayList<>();
            lore.add(lang().of(Lang.MANAGE_PROFILE_LORE_ID, id));
            lore.add(lang().of(Lang.MANAGE_PROFILE_LORE_STATUS,
                    isActive ? lang().of(Lang.MANAGE_PROFILE_STATUS_ACTIVE) : lang().of(Lang.MANAGE_PROFILE_STATUS_INACTIVE)));
            lore.add("");
            lore.add(lang().of(Lang.MANAGE_PROFILE_LORE_EDIT));
            if (!isActive) lore.add(lang().of(Lang.MANAGE_PROFILE_LORE_ACTIVATE));

            final String finalName = displayName;
            final List<String> finalLore = lore;

            buttons.put(slot++, new Button() {
                @Override
                public ItemStack getItemStack() {
                    return new ItemBuilder(isActive ? Material.LIME_DYE : Material.GRAY_DYE)
                            .setName(finalName)
                            .setLore(finalLore)
                            .toItemStack();
                }

                @Override
                public void onClick(InventoryClickEvent e) {
                    e.setCancelled(true);
                    if (e.getClick() == ClickType.RIGHT && !isActive) {
                        getInstance().getProfileManager().activate(id);
                        sendMessage(player, lang().of(Lang.MANAGE_ACTIVATED, id));
                        new ManageCombatMenu(getManager(), player).open();
                        playNeutral(player);
                        return;
                    }
                    new ProfileEditorMenu(getManager(), player, id).open();
                    playNeutral(player);
                }
            });
        }

        if (profileKeys.isEmpty()) {
            buttons.put(1, new Button() {
                @Override
                public ItemStack getItemStack() {
                    return new ItemBuilder(Material.BARRIER)
                            .setName(lang().of(Lang.NO_PROFILES_FOUND_NAME))
                            .setLore(lang().loreOf(Lang.NO_PROFILES_FOUND_LORE))
                            .toItemStack();
                }

                @Override
                public void onClick(InventoryClickEvent e) {
                    e.setCancelled(true);
                }
            });
        }

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
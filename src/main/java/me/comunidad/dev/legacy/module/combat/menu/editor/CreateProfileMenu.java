package me.comunidad.dev.legacy.module.combat.menu.editor;

import me.comunidad.dev.legacy.framework.menu.Menu;
import me.comunidad.dev.legacy.framework.menu.MenuManager;
import me.comunidad.dev.legacy.framework.menu.button.Button;
import me.comunidad.dev.legacy.module.lang.Lang;
import me.comunidad.dev.legacy.module.lang.LangManager;
import me.comunidad.dev.legacy.utils.ItemBuilder;
import me.comunidad.dev.legacy.utils.Tasks;
import me.comunidad.dev.legacy.utils.prompt.Prompt;
import me.comunidad.dev.legacy.utils.prompt.impl.StringPrompt;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;

public class CreateProfileMenu extends Menu {

    public CreateProfileMenu(MenuManager manager, Player player) {
        super(manager, player, manager.getInstance().getLangManager().of(Lang.CREATE_PROFILE_TITLE), 27, false);
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

        buttons.put(13, new Button() {
            @Override
            public ItemStack getItemStack() {
                return new ItemBuilder(Material.GOLDEN_SWORD)
                        .setName(lang().of(Lang.CREATE_PROFILE_PRESET_LEGACY_NAME))
                        .setLore(lang().loreOf(Lang.CREATE_PROFILE_PRESET_LEGACY_LORE))
                        .toItemStack();
            }
            @Override
            public void onClick(InventoryClickEvent e) {
                e.setCancelled(true);
                player.closeInventory();
                playNeutral(player);
                promptName(player, "1.8");
            }
        });

        buttons.put(15, new Button() {
            @Override
            public ItemStack getItemStack() {
                return new ItemBuilder(Material.DIAMOND_SWORD)
                        .setName(lang().of(Lang.CREATE_PROFILE_PRESET_MODERN_NAME))
                        .setLore(lang().loreOf(Lang.CREATE_PROFILE_PRESET_MODERN_LORE))
                        .toItemStack();
            }
            @Override
            public void onClick(InventoryClickEvent e) {
                e.setCancelled(true);
                player.closeInventory();
                playNeutral(player);
                promptName(player, "vanilla");
            }
        });

        buttons.put(23, new Button() {
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
                new ManageCombatMenu(getManager(), player).open();
            }
        });

        return buttons;
    }

    private void promptName(Player player, String preset) {
        new StringPrompt(getInstance()) {

            @Override
            public void handleBegin(Player player) {
                sendMessage(player, lang().of(Lang.CREATE_PROFILE_PROMPT));
                player.closeInventory();
            }

            @Override
            public void handleCancel(Player player) {
                sendMessage(player, lang().of(Lang.CREATE_PROFILE_CANCELLED));
                playFail(player);
                Tasks.execute(getManager(), () -> new ManageCombatMenu(getManager(), player).open());
            }

            @Override
            public Prompt acceptInput(Player player, String value) {
                String name = value.trim();

                if (name.isBlank() || name.equalsIgnoreCase("cancel")) {
                    handleCancel(player);
                    return null;
                }

                String id = name.toLowerCase().replaceAll("[^a-z0-9_-]", "_");

                if (getProfilesConfig().contains("profiles." + id)) {
                    sendMessage(player, lang().of(Lang.CREATE_PROFILE_EXISTS, id));
                    playFail(player);
                    Tasks.execute(getManager(), () -> new ManageCombatMenu(getManager(), player).open());
                    return null;
                }

                writePreset(id, name, preset);
                sendMessage(player, lang().of(Lang.CREATE_PROFILE_CREATED, name));
                playSuccess(player);
                Tasks.execute(getManager(), () -> new ManageCombatMenu(getManager(), player).open());
                return null;
            }
        }.startPrompt(player);
    }

    private void writePreset(String id, String displayName, String preset) {
        String p = "profiles." + id;

        getProfilesConfig().set(p + ".display-name", "&f" + displayName);
        getProfilesConfig().set(p + ".active", false);

        if (preset.equals("1.8")) {
            legacy(p);
        } else {
            modern(p);
        }

        getProfilesConfig().reloadCache();
        getProfilesConfig().save();
    }

    // Legacy Config
    private void legacy(String p) {
        // Knockback
        set(p, "knockback.horizontal", 0.35);
        set(p, "knockback.vertical", 0.35);
        set(p, "knockback.vertical-limit", 0.4);
        set(p, "knockback.extra-vertical", 0.085);
        set(p, "knockback.extra-horizontal", 0.425);
        set(p, "knockback.sprint-modifier", 0.5);
        set(p, "knockback.sprint-reset-mod", 1.0);
        set(p, "knockback.horizontal-limit", 0.45);
        set(p, "knockback.friction", 2.0);
        set(p, "knockback.ground_check", false);
        set(p, "knockback.dynamic-limit", false);
        set(p, "knockback.limit-horizontal", false);
        set(p, "knockback.one-point-seven", true);
        set(p, "knockback.enabled", true);

        // Knockback projectile / rod
        set(p, "knockback-projectile.horizontal", 0.35);
        set(p, "knockback-projectile.vertical", 0.35);
        set(p, "knockback-projectile.vertical-limit", 0.4);
        set(p, "knockback-rod.horizontal", 0.35);
        set(p, "knockback-rod.vertical", 0.35);
        set(p, "knockback-rod.vertical-limit", 0.4);

        // Combat
        set(p, "combat.no-damage-ticks.player", 19);
        set(p, "combat.no-damage-ticks.mob", 10);
        set(p, "combat.attack-speed", 1024.0);
        set(p, "combat.block-hit", true);

        // Tool damage
        set(p, "tool-damage.swords.wood", 4.0);
        set(p, "tool-damage.swords.gold", 4.0);
        set(p, "tool-damage.swords.stone", 5.0);
        set(p, "tool-damage.swords.iron", 6.0);
        set(p, "tool-damage.swords.diamond", 7.0);
        set(p, "tool-damage.swords.netherite", 7.0);
        set(p, "tool-damage.axes.wood", 3.0);
        set(p, "tool-damage.axes.stone", 4.0);
        set(p, "tool-damage.axes.iron", 5.0);
        set(p, "tool-damage.axes.diamond", 6.0);
        set(p, "tool-damage.pickaxe", 2.0);
        set(p, "tool-damage.shovel", 1.0);
        set(p, "tool-damage.hoe", 1.0);
        set(p, "tool-damage.trident", 8.0);

        // Projectiles
        set(p, "projectiles.potion.speed-multiplier", 0.5);
        set(p, "projectiles.potion.y-offset", 0.1);
        set(p, "projectiles.potion.player-vel-x", 1.0);
        set(p, "projectiles.potion.player-vel-z", 1.0);
        set(p, "projectiles.arrow.speed", 1.0);
        set(p, "projectiles.arrow.gravity", 0.05);
        set(p, "projectiles.snowball.speed", 1.0);
        set(p, "projectiles.snowball.gravity", 0.03);
        set(p, "projectiles.trident.speed", 1.0);
        set(p, "projectiles.trident.gravity", 0.05);

        // Pearls
        set(p, "pearls.damage", 5.0);
        set(p, "pearls.cooldown-ticks", 20);
        set(p, "pearls.speed", 1.5);
        set(p, "pearls.gravity", 0.03);

        // Potions
        set(p, "potions.self-intensity-boost", 0.3);
        set(p, "potions.min-self-intensity", 0.6);
        set(p, "potions.max-intensity-cap", 1.0);
    }

    // Modern Config
    private void modern(String p) {
        // Knockback
        set(p, "knockback.horizontal", 0.35);
        set(p, "knockback.vertical", 0.35);
        set(p, "knockback.vertical-limit", 0.4);
        set(p, "knockback.extra-vertical", 0.085);
        set(p, "knockback.extra-horizontal", 0.425);
        set(p, "knockback.sprint-modifier", 0.5);
        set(p, "knockback.sprint-reset-mod", 1.0);
        set(p, "knockback.horizontal-limit", 0.45);
        set(p, "knockback.friction", 2.0);
        set(p, "knockback.ground_check", false);
        set(p, "knockback.dynamic-limit", false);
        set(p, "knockback.limit-horizontal", false);
        set(p, "knockback.one-point-seven", false);
        set(p, "knockback.enabled", false);

        // Knockback projectile / rod
        set(p, "knockback-projectile.horizontal", 0.35);
        set(p, "knockback-projectile.vertical", 0.35);
        set(p, "knockback-projectile.vertical-limit", 0.4);
        set(p, "knockback-rod.horizontal", 0.35);
        set(p, "knockback-rod.vertical", 0.35);
        set(p, "knockback-rod.vertical-limit", 0.4);

        // Combat
        set(p, "combat.no-damage-ticks.player", 20);
        set(p, "combat.no-damage-ticks.mob", 20);
        set(p, "combat.attack-speed", 4.0);
        set(p, "combat.block-hit", false);

        // Tool damage
        set(p, "tool-damage.swords.wood", 4.0);
        set(p, "tool-damage.swords.gold", 4.0);
        set(p, "tool-damage.swords.stone", 5.0);
        set(p, "tool-damage.swords.iron", 6.0);
        set(p, "tool-damage.swords.diamond", 7.0);
        set(p, "tool-damage.swords.netherite", 8.0);
        set(p, "tool-damage.axes.wood", 7.0);
        set(p, "tool-damage.axes.stone", 9.0);
        set(p, "tool-damage.axes.iron", 9.0);
        set(p, "tool-damage.axes.diamond", 9.0);
        set(p, "tool-damage.pickaxe", 2.0);
        set(p, "tool-damage.shovel", 2.5);
        set(p, "tool-damage.hoe", 1.0);
        set(p, "tool-damage.trident", 9.0);

        // Projectiles
        set(p, "projectiles.potion.speed-multiplier", 0.5);
        set(p, "projectiles.potion.y-offset", 0.1);
        set(p, "projectiles.potion.player-vel-x", 1.0);
        set(p, "projectiles.potion.player-vel-z", 1.0);
        set(p, "projectiles.arrow.speed", 1.0);
        set(p, "projectiles.arrow.gravity", 0.05);
        set(p, "projectiles.snowball.speed", 1.0);
        set(p, "projectiles.snowball.gravity", 0.03);
        set(p, "projectiles.trident.speed", 1.0);
        set(p, "projectiles.trident.gravity", 0.05);

        // Pearls
        set(p, "pearls.damage", 5.0);
        set(p, "pearls.cooldown-ticks", 20);
        set(p, "pearls.speed", 1.0);
        set(p, "pearls.gravity", 0.03);

        // Potions
        set(p, "potions.self-intensity-boost", 0.0);
        set(p, "potions.min-self-intensity", 0.6);
        set(p, "potions.max-intensity-cap", 1.0);
    }

    private void set(String base, String sub, Object value) {
        getProfilesConfig().set(base + "." + sub, value);
    }
}
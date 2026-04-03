package me.comunidad.dev.legacy.module.lang;

/*
 * Copyright (c) 2026. @Comunidad, made since 2/4/2026
 * Use or redistribution of this source file is only permitted
 * if explicit permission is given by the author.
 */

/**
 * Central registry of every translatable string used across menus and listeners.
 * <p>
 * Each constant maps to a YAML path inside the active language file.
 * The second constructor argument is the English fallback used when the key
 * is absent from the file — so the plugin never sends a raw path to players.
 * <p>
 */
public enum Lang {

    // Generic
    BACK_BUTTON_NAME("generic.back-button.name", "&7&lBack"),
    BACK_BUTTON_LORE("generic.back-button.lore", "&7Return to previous menu"),
    NO_PROFILES_FOUND_NAME("generic.no-profiles-found.name", "&cNo profiles found"),
    NO_PROFILES_FOUND_LORE("generic.no-profiles-found.lore", "&7Add profiles in &fprofiles.yml"),
    VALUE_EDITOR_TITLE("menus.value-editor.title", "&8» &lEdit: &f{0}"),
    VALUE_CURRENT("menus.value-editor.current", "&7Current: &a{0}"),
    VALUE_RANGE("menus.value-editor.range","&7Min: &f{0}  Max: &f{1}"),
    VALUE_STEP("menus.value-editor.step", "&7Step: &f{0}"),
    VALUE_UPDATED("menus.value-editor.updated", "&aUpdated &f{0} &ato &f{1}"),
    VALUE_TOGGLE("menus.value-editor.toggle", "&eClick &7to toggle"),

    // Main menu
    MAIN_MENU_TITLE("menus.main.title", "&8» &lCombat Editor"),
    MAIN_PROFILES_NAME("menus.main.profiles.name", "&b&lCombat Profiles"),
    MAIN_PROFILES_LORE("menus.main.profiles.lore", "&7Manage knockback, damage,\n&7cooldowns and more per profile.\n\n&eClick to open"),
    MAIN_DAMAGE_NAME("menus.main.damage.name", "&c&lTool Damage"),
    MAIN_DAMAGE_LORE("menus.main.damage.lore", "&7Configure damage values\n&7for each weapon type.\n\n&eClick to open"),
    MAIN_LANGUAGES_NAME("menus.main.languages.name", "&a&lLanguages"),
    MAIN_LANGUAGES_LORE("menus.main.languages.lore", "&7Choose the active language\n&7for server messages.\n\n&eClick to open"),

    // Manage combat menu
    MANAGE_MENU_TITLE("menus.manage.title", "&8» &lCombat Profiles"),
    MANAGE_PROFILE_STATUS_ACTIVE("menus.manage.status.active", "&aActive ✔"),
    MANAGE_PROFILE_STATUS_INACTIVE("menus.manage.status.inactive", "&7Inactive"),
    MANAGE_PROFILE_LORE_ID("menus.manage.profile.lore.id", "&7ID: &f{0}"),
    MANAGE_PROFILE_LORE_STATUS("menus.manage.profile.lore.status", "&7Status: {0}"),
    MANAGE_PROFILE_LORE_EDIT("menus.manage.profile.lore.edit", "&eLeft-click &7to edit"),
    MANAGE_PROFILE_LORE_ACTIVATE("menus.manage.profile.lore.activate", "&eRight-click &7to activate"),
    MANAGE_ACTIVATED("menus.manage.activated", "&aActivated profile &f{0}"),

    // Profile editor
    PROFILE_EDITOR_TITLE("menus.profile-editor.title", "&8» &lProfile: &b{0}"),
    PROFILE_KB_NAME("menus.profile-editor.knockback.name", "&e&lKnockback"),
    PROFILE_KB_LORE("menus.profile-editor.knockback.lore", "&7Horizontal: &f{0}\n&7Vertical: &f{1}\n\n&eClick to edit"),
    PROFILE_COMBAT_NAME("menus.profile-editor.combat.name", "&c&lCombat Settings"),
    PROFILE_COMBAT_LORE("menus.profile-editor.combat.lore", "&7No-Damage Ticks: &f{0}\n\n&eClick to edit"),
    PROFILE_DAMAGE_NAME("menus.profile-editor.damage.name", "&6&lTool Damage"),
    PROFILE_DAMAGE_LORE("menus.profile-editor.damage.lore", "&7Configure per-weapon damage\n&7values for this profile.\n\n&eClick to edit"),
    PROFILE_PROJECTILE_NAME("menus.profile-editor.projectiles.name", "&d&lProjectile Physics"),
    PROFILE_PROJECTILE_LORE("menus.profile-editor.projectiles.lore", "&7Speed Multiplier: &f{0}\n&7Gravity: &f{1}\n\n&eClick to edit"),
    PROFILE_PEARL_NAME("menus.profile-editor.pearl.name", "&5&lEnder Pearl"),
    PROFILE_PEARL_LORE("menus.profile-editor.pearl.lore", "&7Damage on land: &f{0}\n&7Cooldown (ticks): &f{1}\n\n&eClick to edit"),
    PROFILE_POTION_NAME("menus.profile-editor.potion.name", "&b&lPotion Settings"),
    PROFILE_POTION_LORE("menus.profile-editor.potion.lore", "&7Speed Multiplier: &f{0}\n&7Self Intensity Boost: &f{1}\n\n&eClick to edit"),

    // Knockback editor
    KB_EDITOR_TITLE("menus.kb-editor.title", "&8» &lKnockback: &e{0}"),
    KB_HINT_LEFT("menus.kb-editor.hint.left", "&fLeft-click&7: +{0}"),
    KB_HINT_RIGHT("menus.kb-editor.hint.right", "&fRight-click&7: -{0}"),
    KB_HINT_SHIFT_LEFT("menus.kb-editor.hint.shift-left", "&fShift+Left&7: +{0}"),
    KB_HINT_SHIFT_RIGHT("menus.kb-editor.hint.shift-right", "&fShift+Right&7: -{0}"),

    // Combat settings editor
    COMBAT_EDITOR_TITLE("menus.combat-editor.title", "&8» &lCombat: &c{0}"),

    // Damage tool menu
    DAMAGE_MENU_TITLE_GLOBAL("menus.damage.title.global", "&8» &lTool Damage (Global)"),
    DAMAGE_MENU_TITLE_PROFILE("menus.damage.title.profile", "&8» &lTool Damage: &6{0}"),
    DAMAGE_CURRENT("menus.damage.current", "&7Damage: &a{0}"),

    // Projectile editor
    PROJ_EDITOR_TITLE("menus.proj-editor.title", "&8» &lProjectiles: &d{0}"),

    // Pearl editor
    PEARL_EDITOR_TITLE("menus.pearl-editor.title", "&8» &lPearl Settings: &5{0}"),
    PEARL_UPDATED("menus.pearl-editor.updated", "&aUpdated &5{0}"),
    PEARL_TOGGLE_ON("menus.pearl-editor.toggle.on", "&aEnabled"),
    PEARL_TOGGLE_OFF("menus.pearl-editor.toggle.off", "&cDisabled"),

    // Potion editor
    POTION_EDITOR_TITLE("menus.potion-editor.title", "&8» &lPotions: &b{0}"),

    // Languages menu
    LANG_MENU_TITLE("menus.languages.title", "&8» &lLanguage Selection"),
    LANG_STATUS_ACTIVE("menus.languages.status.active", "&aActive ✔"),
    LANG_STATUS_INACTIVE("menus.languages.status.inactive", "&7Inactive"),
    LANG_LORE_ID("menus.languages.lore.id", "&7ID: &f{0}"),
    LANG_LORE_STATUS("menus.languages.lore.status", "&7Status: {0}"),
    LANG_LORE_SELECTED("menus.languages.lore.selected", "&7Already selected"),
    LANG_LORE_CLICK("menus.languages.lore.click", "&eClick to activate"),
    LANG_CHANGED("menus.languages.changed", "&aLanguage changed to &f{0}"),

    // Exploit listener
    EXPLOIT_INVALID_PEARL("messages.exploit.invalid-pearl", "&cInvalid Pearl Location");

    public final String path;
    public final String fallback;

    Lang(String path, String fallback) {
        this.path = path;
        this.fallback = fallback;
    }
}
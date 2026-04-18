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
    MANAGE_PROFILE_LORE_DELETE("menus.manage.profile.lore.delete", "&eCtrl+Q &7to delete"),
    MANAGE_ACTIVATED("menus.manage.activated", "&aActivated profile &f{0}"),
    MANAGE_DELETED("menus.manage.deleted", "&cProfile &f{0} &cdeleted successfully."),

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
    KB_MAIN_EDITOR_TITLE("menus.kb-main-editor.title", "&8» &lKnockback: &e{0}"),
    KB_HIT_EDITOR_NAME("menus.kb-main-editor.hit.name", "&e&lHit Knockback"),
    KB_HIT_EDITOR_LORE("menus.kb-main-editor.hit.lore", "&7Configure horizontal, vertical,\n&7friction and sprint values.\n\n&eClick to edit"),
    KB_PROJECTILE_EDITOR_NAME("menus.kb-main-editor.projectile.name", "&d&lProjectile Knockback"),
    KB_PROJECTILE_EDITOR_LORE("menus.kb-main-editor.projectile.lore", "&7Configure knockback applied\n&7by snowballs and eggs.\n\n&eClick to edit"),
    KB_ROD_EDITOR_NAME("menus.kb-main-editor.rod.name", "&6&lRod Knockback"),
    KB_ROD_EDITOR_LORE("menus.kb-main-editor.rod.lore", "&7Configure knockback applied\n&7by the fishing rod.\n\n&eClick to edit"),
    KB_PROJECTILE_EDITOR_TITLE("menus.kb-projectile-editor.title", "&8» &lProjectile KB: &d{0}"),
    KB_ROD_EDITOR_TITLE("menus.kb-rod-editor.title", "&8» &lRod KB: &6{0}"),

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
    LANG_ALREADY_ACTIVE("menus.languages.already-active", "&cLanguage already active"),

    // Exploit listener
    EXPLOIT_INVALID_PEARL("messages.exploit.invalid-pearl", "&cInvalid Pearl Location"),
    PEARL_REFUNDED("messages.exploit.pearl-refunded", "&c(!) Your pearl was refunded"),

    // ManageCombatMenu
    MANAGE_CREATE_PROFILE_NAME ("menus.manage.create.name", "&a&l+ Create Profile"),
    MANAGE_CREATE_PROFILE_LORE ("menus.manage.create.lore", "&7Create a new profile\n&7from a preset.\n\n&eClick to open"),

    // CreateProfileMenu
    CREATE_PROFILE_TITLE("menus.create-profile.title", "&8» &lCreate Profile"),
    CREATE_PROFILE_PRESET_LEGACY_NAME("menus.create-profile.preset-18.name", "&6&l1.8 Preset"),
    CREATE_PROFILE_PRESET_LEGACY_LORE("menus.create-profile.preset-18.lore", "&7No cooldown, sword blocking,\n&71.8 KB, legacy pearl physics.\n\n&eClick to select"),
    CREATE_PROFILE_PRESET_MODERN_NAME("menus.create-profile.preset-vanilla.name", "&b&lVanilla 1.20"),
    CREATE_PROFILE_PRESET_MODERN_LORE("menus.create-profile.preset-vanilla.lore", "&7Standard 1.20 values.\n&7Attack cooldown, no blocking,\n&7vanilla pearl physics.\n\n&eClick to select"),
    CREATE_PROFILE_PROMPT("menus.create-profile.prompt", "&eType the profile name in chat.\n&7Type &fcancel &7to abort."),
    CREATE_PROFILE_CANCELLED("menus.create-profile.cancelled", "&cProfile creation cancelled."),
    CREATE_PROFILE_EXISTS("menus.create-profile.exists", "&cA profile with id &f{0} &calready exists."),
    CREATE_PROFILE_CREATED("menus.create-profile.created", "&aProfile &f{0} &acreated successfully."),

    // Knockback command
    KB_COMMAND_USAGE("commands.knockback.usage", "&6&lKnockback Profile Commands:\n&e/knockback create <name> &7- Create a new profile\n&e/knockback delete <name> &7- Delete a profile\n&e/knockback list &7- List all profiles\n&e/knockback set <name> &7- Set active profile\n&e/knockback view [name] &7- View profile values\n&e/knockback edit <name> <value> <newValue> &7- Edit profile value"),
    KB_CREATE_USAGE("commands.knockback.create.usage", "&eUsage: /knockback create <profileName>"),
    KB_CREATE_INVALID_NAME("commands.knockback.create.invalid-name", "&cProfile name cannot contain dots or spaces!"),
    KB_CREATE_EXISTS("commands.knockback.create.exists", "&cA profile with that name already exists!"),
    KB_CREATE_SUCCESS("commands.knockback.create.success", "&aProfile '{0}' created successfully!"),

    KB_DELETE_USAGE("commands.knockback.delete.usage", "&eUsage: /knockback delete <profileName>"),
    KB_DELETE_NOT_FOUND("commands.knockback.delete.not-found", "&cProfile '{0}' does not exist!"),
    KB_DELETE_ACTIVE("commands.knockback.delete.active", "&cCannot delete the active profile! Switch to another profile first."),
    KB_DELETE_LAST("commands.knockback.delete.last", "&cCannot delete the last profile! Create another one first."),
    KB_DELETE_SUCCESS("commands.knockback.delete.success", "&aProfile '{0}' deleted successfully!"),

    KB_LIST_USAGE("commands.knockback.list.usage", "&eUsage: /knockback list"),
    KB_LIST_NONE("commands.knockback.list.none", "&cNo profiles found! Use '/knockback create <name>' to create one."),
    KB_LIST_TITLE("commands.knockback.list.title", "&6&lKnockback Profiles:"),
    KB_LIST_ACTIVE("commands.knockback.list.active", "&a[ACTIVE] "),
    KB_LIST_INACTIVE("commands.knockback.list.inactive", "&7"),
    KB_LIST_TOTAL("commands.knockback.list.total", "&eTotal profiles: {0}"),

    KB_SET_USAGE("commands.knockback.set.usage", "&eUsage: /knockback set <profileName>"),
    KB_SET_NOT_FOUND("commands.knockback.set.not-found", "&cProfile '{0}' does not exist!"),
    KB_SET_ALREADY_ACTIVE("commands.knockback.set.already-active", "&eProfile '{0}' is already active!"),
    KB_SET_SUCCESS("commands.knockback.set.success", "&aProfile '{0}' is now active!"),

    KB_VIEW_USAGE("commands.knockback.view.usage", "&eUsage: /knockback view [profileName]"),
    KB_VIEW_NO_ACTIVE("commands.knockback.view.no-active", "&cNo active profile found!"),
    KB_VIEW_NOT_FOUND("commands.knockback.view.not-found", "&cProfile '{0}' does not exist!"),
    KB_VIEW_TITLE("commands.knockback.view.title", "&6&lProfile: {0} {1}"),
    KB_VIEW_SEPARATOR("commands.knockback.view.separator", "&7&m-----------------------------"),
    KB_VIEW_KB_VALUES("commands.knockback.view.kb-values", "&eKnockback Values:"),
    KB_VIEW_PROJECTILE_KB("commands.knockback.view.projectile-kb", "&eProjectile Knockback:"),
    KB_VIEW_ROD_KB("commands.knockback.view.rod-kb", "&eRod Knockback:"),
    KB_VIEW_HORIZONTAL("commands.knockback.view.horizontal", "&7  Horizontal: &f{0}"),
    KB_VIEW_VERTICAL("commands.knockback.view.vertical", "&7  Vertical: &f{0}"),
    KB_VIEW_VERTICAL_LIMIT("commands.knockback.view.vertical-limit", "&7  Vertical Limit: &f{0}"),
    KB_VIEW_EXTRA_VERTICAL("commands.knockback.view.extra-vertical", "&7  Extra Vertical: &f{0}"),
    KB_VIEW_EXTRA_HORIZONTAL("commands.knockback.view.extra-horizontal", "&7  Extra Horizontal: &f{0}"),
    KB_VIEW_SPRINT_MODIFIER("commands.knockback.view.sprint-modifier", "&7  Sprint Modifier: &f{0}"),
    KB_VIEW_SPRINT_RESET_MOD("commands.knockback.view.sprint-reset-mod", "&7  Sprint Reset Modifier: &f{0}"),
    KB_VIEW_HORIZONTAL_LIMIT("commands.knockback.view.horizontal-limit", "&7  Horizontal Limit: &f{0}"),
    KB_VIEW_FRICTION("commands.knockback.view.friction", "&7  Friction: &f{0}"),
    KB_VIEW_ENABLED("commands.knockback.view.enabled", "&7  Enabled: &f{0}"),
    KB_VIEW_GROUND_CHECK("commands.knockback.view.ground-check", "&7  Ground Check: &f{0}"),
    KB_VIEW_DYNAMIC_LIMIT("commands.knockback.view.dynamic-limit", "&7  Dynamic Limit: &f{0}"),
    KB_VIEW_LIMIT_HORIZONTAL("commands.knockback.view.limit-horizontal", "&7  Limit Horizontal: &f{0}"),
    KB_VIEW_ONE_POINT_SEVEN("commands.knockback.view.one-point-seven", "&7  One Point Seven: &f{0}"),

    KB_EDIT_USAGE("commands.knockback.edit.usage", "&eUsage: /knockback edit <profileName> <value> <newValue>"),
    KB_EDIT_NOT_FOUND("commands.knockback.edit.not-found", "&cProfile '{0}' does not exist!"),
    KB_EDIT_VALUE_NOT_FOUND("commands.knockback.edit.value-not-found", "&cValue '{0}' does not exist!"),
    KB_EDIT_AVAILABLE_VALUES("commands.knockback.edit.available-values", "&eAvailable values: {0}"),
    KB_EDIT_UNSUPPORTED_TYPE("commands.knockback.edit.unsupported-type", "&cUnsupported value type for '{0}'"),
    KB_EDIT_INVALID_FORMAT("commands.knockback.edit.invalid-format", "&cInvalid value format! Expected: {0}"),
    KB_EDIT_SUCCESS("commands.knockback.edit.success", "&aSet {0} to {1} for profile '{2}'"),
    KB_EDIT_RELOADED("commands.knockback.edit.reloaded", "&eProfile reloaded since it's currently active!");

    public final String path;
    public final String fallback;

    Lang(String path, String fallback) {
        this.path = path;
        this.fallback = fallback;
    }
}
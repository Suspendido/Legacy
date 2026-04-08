package me.comunidad.dev.legacy.module.combat;

import lombok.Getter;
import lombok.Setter;
import me.comunidad.dev.legacy.Core;
import me.comunidad.dev.legacy.framework.Manager;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

/*
 * Copyright (c) 2026. @Comunidad, made since 2/4/2026
 * Use or redistribution of this source file is only permitted
 * if explicit permission is given by the author.
 */
@Getter @Setter
public class ProfileManager extends Manager {

    private String activeId;

    // Knockback (Hit)
    public double kbHorizontal;
    public double kbVertical;
    public double kbVerticalLimit;
    public double kbExtraVertical;
    public double kbExtraHorizontal;
    public double kbSprintModifier;
    public double kbSprintResetModifier;
    public double kbHorizontalLimit;
    public double kbFriction;
    public boolean kbEnabled;
    public boolean requiredGroundCheck;
    public boolean kbDynamicLimit;
    public boolean kbLimitHorizontal;
    public boolean kbOnePointSeven;

    // Knockback (Projectile)
    public double projKbHorizontal;
    public double projKbVertical;
    public double projKbVerticalLimit;

    // Knockback (Rod)
    public double rodKbHorizontal;
    public double rodKbVertical;
    public double rodKbVerticalLimit;

    // Combat
    public int playerNoDamageTicks;
    public int mobNoDamageTicks;
    public double attackSpeed;
    public boolean blockhit;

    // Tool damage
    public double dmgSwordWood;
    public double dmgSwordGold;
    public double dmgSwordStone;
    public double dmgSwordIron;
    public double dmgSwordDiamond;
    public double dmgSwordNetherite;
    public double dmgAxeWood;
    public double dmgAxeStone;
    public double dmgAxeIron;
    public double dmgAxeDiamond;
    public double dmgPickaxe;
    public double dmgShovel;
    public double dmgHoe;
    public double dmgTrident;

    // Potions
    public double potionSpeedMultiplier;
    public double potionYOffset;
    public double potionPlayerVelX;
    public double potionPlayerVelZ;

    // Projectiles
    public double arrowSpeed;
    public double arrowGravity;
    public double snowballSpeed;
    public double tridentSpeed;
    public double snowballGravity;
    public double tridentGravity;

    // Pearls
    public double pearlDamage;
    public int pearlCooldownTicks;
    public double pearlGravity;
    public double pearlSpeed;

    // Potions (splash behaviour)
    public double potionSelfIntensityBoost;
    public double potionMinSelfIntensity;
    public double potionMaxIntensityCap;

    public ProfileManager(Core instance) {
        super(instance);
    }

    /**
     * Finds the first profile marked active=true and loads all its values.
     * Falls back to the first profile found if none is marked active.
     * Call this after any menu saves a change.
     */
    public void reload() {
        ConfigurationSection root = getProfilesConfig().getConfigurationSection("profiles");
        if (root == null) return;

        // Pick active profile
        String fallback = null;
        activeId = null;
        for (String key : root.getKeys(false)) {
            if (fallback == null) fallback = key;
            if (getProfilesConfig().getBoolean("profiles." + key + ".active")) {
                activeId = key;
                break;
            }
        }
        if (activeId == null) activeId = fallback;
        if (activeId == null) return; // no profiles at all

        loadFrom("profiles." + activeId);

        for (Player player : getInstance().getServer().getOnlinePlayers()) {
            AttributeInstance attr = player.getAttribute(Attribute.ATTACK_SPEED);
            if (attr != null) attr.setBaseValue(attackSpeed);
            player.setMaximumNoDamageTicks(playerNoDamageTicks);
        }

        getInstance().getServer().getWorlds().forEach(world -> world.getLivingEntities().forEach(entity -> {
            if (!(entity instanceof Player)) {
                entity.setMaximumNoDamageTicks(mobNoDamageTicks);
            }
        }));

        if (!blockhit) {
            getInstance().getListenerManager().getCombatListener().clearBlockingFromAllPlayers();
        }
    }

    public void enable() {
        ConfigurationSection root = getProfilesConfig().getConfigurationSection("profiles");
        if (root == null) return;

        String fallback = null;
        activeId = null;

        for (String key : root.getKeys(false)) {
            if (fallback == null) fallback = key;
            if (getProfilesConfig().getBoolean("profiles." + key + ".active")) {
                activeId = key;
                break;
            }
        }
        if (activeId == null) activeId = fallback;
        if (activeId == null) return;

        loadFrom("profiles." + activeId);
    }

    /** Switch to a different profile by id and save the active flag. */
    public void activate(String profileId) {
        // Clear previous active flag
        ConfigurationSection root = getProfilesConfig().getConfigurationSection("profiles");
        if (root != null) {
            for (String key : root.getKeys(false)) {
                getProfilesConfig().set("profiles." + key + ".active", false);
            }
        }
        getProfilesConfig().set("profiles." + profileId + ".active", true);
        getProfilesConfig().reloadCache();
        getProfilesConfig().save();
        reload();
    }

    public void delete(String profileId) {
        ConfigurationSection root = getProfilesConfig().getConfigurationSection("profiles");
        if (root != null) {
            root.set(profileId, null);
            getProfilesConfig().save();
        }
        reload();
    }

    private void loadFrom(String path) {
        // Knockback (Hits)
        kbHorizontal = doubles(path + ".knockback.horizontal", 0.35);
        kbVertical = doubles(path + ".knockback.vertical", 0.35);
        kbVerticalLimit = doubles(path + ".knockback.vertical-limit", 0.40);
        kbExtraVertical = doubles(path + ".knockback.extra-vertical", 0.085);
        kbExtraHorizontal = doubles(path + ".knockback.extra-horizontal", 0.425);
        kbSprintModifier = doubles(path + ".knockback.sprint-modifier", 0.5);
        kbSprintResetModifier = doubles(path + ".knockback.sprint-reset-mod", 1.0);
        kbHorizontalLimit = doubles(path + ".knockback.horizontal-limit", 0.45);
        kbFriction = doubles(path + ".knockback.friction", 2.0);
        kbEnabled = booleans(path + ".knockback.enabled", true);
        requiredGroundCheck = booleans(path + ".knockback.ground_check", true);
        kbDynamicLimit = booleans(path + ".knockback.dynamic-limit", false);
        kbLimitHorizontal = booleans(path + ".knockback.limit-horizontal", false);
        kbOnePointSeven = booleans(path + ".knockback.one-point-seven", false);

        // Knockback (Projectile)
        projKbHorizontal = doubles(path + ".knockback-projectile.horizontal", 0.35);
        projKbVertical = doubles(path + ".knockback-projectile.vertical", 0.35);
        projKbVerticalLimit = doubles(path + ".knockback-projectile.vertical-limit", 0.40);

        // Knockback (Rod)
        rodKbHorizontal = doubles(path + ".knockback-rod.horizontal", 0.35);
        rodKbVertical = doubles(path + ".knockback-rod.vertical", 0.35);
        rodKbVerticalLimit = doubles(path + ".knockback-rod.vertical-limit", 0.4);

        // Combat
        playerNoDamageTicks = ints(path + ".combat.no-damage-ticks.player", 19);
        mobNoDamageTicks = ints(path + ".combat.no-damage-ticks.mob", 10);
        attackSpeed = doubles(path + ".combat.attack-speed", 1024.0);
        blockhit = booleans(path + ".combat.block-hit", true);

        // Tool damage
        dmgSwordWood = doubles(path + ".tool-damage.swords.wood", 4.0);
        dmgSwordGold = doubles(path + ".tool-damage.swords.gold", 4.0);
        dmgSwordStone = doubles(path + ".tool-damage.swords.stone", 5.0);
        dmgSwordIron = doubles(path + ".tool-damage.swords.iron", 6.0);
        dmgSwordDiamond = doubles(path + ".tool-damage.swords.diamond", 7.0);
        dmgSwordNetherite = doubles(path + ".tool-damage.swords.netherite", 7.0);
        dmgAxeWood = doubles(path + ".tool-damage.axes.wood", 3.0);
        dmgAxeStone = doubles(path + ".tool-damage.axes.stone",4.0);
        dmgAxeIron = doubles(path + ".tool-damage.axes.iron", 5.0);
        dmgAxeDiamond = doubles(path + ".tool-damage.axes.diamond", 6.0);
        dmgPickaxe = doubles(path + ".tool-damage.pickaxe", 2.0);
        dmgShovel = doubles(path + ".tool-damage.shovel", 1.0);
        dmgHoe = doubles(path + ".tool-damage.hoe", 1.0);
        dmgTrident = doubles(path + ".tool-damage.trident", 8.0);

        // Potions
        potionSpeedMultiplier = doubles(path + ".projectiles.potion.speed-multiplier", 0.5);
        potionYOffset = doubles(path + ".projectiles.potion.y-offset", 0.1);
        potionPlayerVelX = doubles(path + ".projectiles.potion.player-vel-x", 1.0);
        potionPlayerVelZ = doubles(path + ".projectiles.potion.player-vel-z", 1.0);

        // Proyectiles
        arrowSpeed = doubles(path + ".projectiles.arrow.speed", 1.0);
        arrowGravity = doubles(path + ".projectiles.arrow.gravity", 0.05);
        snowballSpeed = doubles(path + ".projectiles.snowball.speed", 1.0);
        tridentSpeed = doubles(path + ".projectiles.trident.speed", 1.0);
        snowballGravity = doubles(path + ".projectiles.snowball.gravity", 0.03);
        tridentGravity = doubles(path + ".projectiles.trident.gravity", 0.05);

        // Pearls
        pearlDamage = doubles(path + ".pearls.damage", 5.0);
        pearlCooldownTicks = ints(path + ".pearls.cooldown-ticks", 20);
        pearlGravity = doubles(path + ".pearls.gravity", 0.03);
        pearlSpeed = doubles(path + ".pearls.speed", 1.5);

        // Potion splash behaviour
        potionSelfIntensityBoost = doubles(path + ".potions.self-intensity-boost", 0.3);
        potionMinSelfIntensity = doubles(path + ".potions.min-self-intensity", 0.6);
        potionMaxIntensityCap = doubles(path + ".potions.max-intensity-cap", 1.0);
    }

    private double doubles(String path, double def) {
        return getProfilesConfig().contains(path) ? getProfilesConfig().getDouble(path) : def;
    }

    private boolean booleans(String path, boolean def) {
        return getProfilesConfig().getBoolean(path) ? getProfilesConfig().getBoolean(path) : def;
    }

    private int ints(String path, int def) {
        return getProfilesConfig().contains(path) ? getProfilesConfig().getInt(path) : def;
    }
}
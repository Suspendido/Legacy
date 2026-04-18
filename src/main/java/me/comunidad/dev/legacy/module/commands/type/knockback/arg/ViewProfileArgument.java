package me.comunidad.dev.legacy.module.commands.type.knockback.arg;

import me.comunidad.dev.legacy.module.commands.CommandManager;
import me.comunidad.dev.legacy.framework.commands.Argument;
import me.comunidad.dev.legacy.module.lang.Lang;
import me.comunidad.dev.legacy.module.lang.LangManager;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ViewProfileArgument extends Argument {

    public ViewProfileArgument(CommandManager manager) {
        super(manager, List.of("view", "info", "show"));
        this.usage = lang().of(Lang.KB_VIEW_USAGE);
    }

    @Override
    public String usage() {
        return lang().of(Lang.KB_VIEW_USAGE);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        String profileName;
        
        if (args.length == 0) {
            // Show active profile if no name specified
            profileName = getInstance().getProfileManager().getActiveId();
            if (profileName == null) {
                sendMessage(sender, lang().of(Lang.KB_VIEW_NO_ACTIVE));
                return;
            }
        } else {
            profileName = args[0];
        }
        
        ConfigurationSection root = getInstance().getProfileManager().getProfilesConfig().getConfigurationSection("profiles");
        if (root == null || !root.contains(profileName)) {
            sendMessage(sender, lang().of(Lang.KB_VIEW_NOT_FOUND, profileName));
            return;
        }

        boolean isActive = profileName.equals(getInstance().getProfileManager().getActiveId());
        String path = "profiles." + profileName;
        
        sendMessage(sender, lang().of(Lang.KB_VIEW_TITLE, profileName, isActive ? " &a[ACTIVE]" : ""));
        sendMessage(sender, lang().of(Lang.KB_VIEW_SEPARATOR));
        sendMessage(sender, "");
        
        // Knockback values
        sendMessage(sender, lang().of(Lang.KB_VIEW_KB_VALUES));
        sendMessage(sender, lang().of(Lang.KB_VIEW_HORIZONTAL, getInstance().getProfileManager().getProfilesConfig().getDouble(path + ".knockback.horizontal")));
        sendMessage(sender, lang().of(Lang.KB_VIEW_VERTICAL, getInstance().getProfileManager().getProfilesConfig().getDouble(path + ".knockback.vertical")));
        sendMessage(sender, lang().of(Lang.KB_VIEW_VERTICAL_LIMIT, getInstance().getProfileManager().getProfilesConfig().getDouble(path + ".knockback.vertical-limit")));
        sendMessage(sender, lang().of(Lang.KB_VIEW_EXTRA_VERTICAL, getInstance().getProfileManager().getProfilesConfig().getDouble(path + ".knockback.extra-vertical")));
        sendMessage(sender, lang().of(Lang.KB_VIEW_EXTRA_HORIZONTAL, getInstance().getProfileManager().getProfilesConfig().getDouble(path + ".knockback.extra-horizontal")));
        sendMessage(sender, lang().of(Lang.KB_VIEW_SPRINT_MODIFIER, getInstance().getProfileManager().getProfilesConfig().getDouble(path + ".knockback.sprint-modifier")));
        sendMessage(sender, lang().of(Lang.KB_VIEW_SPRINT_RESET_MOD, getInstance().getProfileManager().getProfilesConfig().getDouble(path + ".knockback.sprint-reset-mod")));
        sendMessage(sender, lang().of(Lang.KB_VIEW_HORIZONTAL_LIMIT, getInstance().getProfileManager().getProfilesConfig().getDouble(path + ".knockback.horizontal-limit")));
        sendMessage(sender, lang().of(Lang.KB_VIEW_FRICTION, getInstance().getProfileManager().getProfilesConfig().getDouble(path + ".knockback.friction")));
        sendMessage(sender, lang().of(Lang.KB_VIEW_ENABLED, getInstance().getProfileManager().getProfilesConfig().getBoolean(path + ".knockback.enabled")));
        sendMessage(sender, lang().of(Lang.KB_VIEW_GROUND_CHECK, getInstance().getProfileManager().getProfilesConfig().getBoolean(path + ".knockback.ground_check")));
        sendMessage(sender, lang().of(Lang.KB_VIEW_DYNAMIC_LIMIT, getInstance().getProfileManager().getProfilesConfig().getBoolean(path + ".knockback.dynamic-limit")));
        sendMessage(sender, lang().of(Lang.KB_VIEW_LIMIT_HORIZONTAL, getInstance().getProfileManager().getProfilesConfig().getBoolean(path + ".knockback.limit-horizontal")));
        sendMessage(sender, lang().of(Lang.KB_VIEW_ONE_POINT_SEVEN, getInstance().getProfileManager().getProfilesConfig().getBoolean(path + ".knockback.one-point-seven")));
        sendMessage(sender, "");
        
        // Projectile values
        sendMessage(sender, lang().of(Lang.KB_VIEW_PROJECTILE_KB));
        sendMessage(sender, lang().of(Lang.KB_VIEW_HORIZONTAL, getInstance().getProfileManager().getProfilesConfig().getDouble(path + ".knockback-projectile.horizontal")));
        sendMessage(sender, lang().of(Lang.KB_VIEW_VERTICAL, getInstance().getProfileManager().getProfilesConfig().getDouble(path + ".knockback-projectile.vertical")));
        sendMessage(sender, lang().of(Lang.KB_VIEW_VERTICAL_LIMIT, getInstance().getProfileManager().getProfilesConfig().getDouble(path + ".knockback-projectile.vertical-limit")));
        sendMessage(sender, "");
        
        // Rod values
        sendMessage(sender, lang().of(Lang.KB_VIEW_ROD_KB));
        sendMessage(sender, lang().of(Lang.KB_VIEW_HORIZONTAL, getInstance().getProfileManager().getProfilesConfig().getDouble(path + ".knockback-rod.horizontal")));
        sendMessage(sender, lang().of(Lang.KB_VIEW_VERTICAL, getInstance().getProfileManager().getProfilesConfig().getDouble(path + ".knockback-rod.vertical")));
        sendMessage(sender, lang().of(Lang.KB_VIEW_VERTICAL_LIMIT, getInstance().getProfileManager().getProfilesConfig().getDouble(path + ".knockback-rod.vertical-limit")));
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            List<String> profiles = new ArrayList<>();
            ConfigurationSection root = getInstance().getProfileManager().getProfilesConfig().getConfigurationSection("profiles");
            if (root != null) {
                for (String profile : root.getKeys(false)) {
                    if (profile.toLowerCase().startsWith(args[0].toLowerCase())) {
                        profiles.add(profile);
                    }
                }
            }
            return profiles;
        }
        return Collections.emptyList();
    }

    private LangManager lang() {
        return getInstance().getLangManager();
    }
}

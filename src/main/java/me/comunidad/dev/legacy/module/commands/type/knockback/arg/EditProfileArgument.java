package me.comunidad.dev.legacy.module.commands.type.knockback.arg;

import me.comunidad.dev.legacy.module.commands.CommandManager;
import me.comunidad.dev.legacy.framework.commands.Argument;
import me.comunidad.dev.legacy.module.lang.Lang;
import me.comunidad.dev.legacy.module.lang.LangManager;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class EditProfileArgument extends Argument {

    private static final List<String> KB_VALUES = Arrays.asList(
        "knockback.horizontal",
        "knockback.vertical", 
        "knockback.vertical-limit",
        "knockback.extra-vertical",
        "knockback.extra-horizontal",
        "knockback.sprint-modifier",
        "knockback.sprint-reset-mod",
        "knockback.horizontal-limit",
        "knockback.friction",
        "knockback.enabled",
        "knockback.ground_check",
        "knockback.dynamic-limit",
        "knockback.limit-horizontal",
        "knockback.one-point-seven",
        "knockback-projectile.horizontal",
        "knockback-projectile.vertical",
        "knockback-projectile.vertical-limit",
        "knockback-rod.horizontal",
        "knockback-rod.vertical",
        "knockback-rod.vertical-limit"
    );

    public EditProfileArgument(CommandManager manager) {
        super(manager, List.of("edit", "set", "modify"));
        this.usage = lang().of(Lang.KB_EDIT_USAGE);
    }

    @Override
    public String usage() {
        return lang().of(Lang.KB_EDIT_USAGE);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sendUsage(sender);
            return;
        }

        String profileName = args[0];
        String valuePath = args[1];
        String newValue = args[2];
        
        ConfigurationSection root = getInstance().getProfileManager().getProfilesConfig().getConfigurationSection("profiles");
        if (root == null || !root.contains(profileName)) {
            sendMessage(sender, lang().of(Lang.KB_EDIT_NOT_FOUND, profileName));
            return;
        }

        // Check if the value path exists
        String fullPath = "profiles." + profileName + "." + valuePath;
        if (!getInstance().getProfileManager().getProfilesConfig().contains(fullPath)) {
            sendMessage(sender, lang().of(Lang.KB_EDIT_VALUE_NOT_FOUND, valuePath));
            sendMessage(sender, lang().of(Lang.KB_EDIT_AVAILABLE_VALUES, String.join(", ", KB_VALUES)));
            return;
        }

        // Get current value type
        Object currentValue = getInstance().getProfileManager().getProfilesConfig().get(fullPath);
        
        try {
            if (currentValue instanceof Boolean) {
                boolean boolValue = Boolean.parseBoolean(newValue);
                getInstance().getProfileManager().getProfilesConfig().set(fullPath, boolValue);
                sendMessage(sender, lang().of(Lang.KB_EDIT_SUCCESS, valuePath, boolValue, profileName));
            } else if (currentValue instanceof Integer) {
                int intValue = Integer.parseInt(newValue);
                getInstance().getProfileManager().getProfilesConfig().set(fullPath, intValue);
                sendMessage(sender, lang().of(Lang.KB_EDIT_SUCCESS, valuePath, intValue, profileName));
            } else if (currentValue instanceof Double) {
                double doubleValue = Double.parseDouble(newValue);
                getInstance().getProfileManager().getProfilesConfig().set(fullPath, doubleValue);
                sendMessage(sender, lang().of(Lang.KB_EDIT_SUCCESS, valuePath, doubleValue, profileName));
            } else {
                sendMessage(sender, lang().of(Lang.KB_EDIT_UNSUPPORTED_TYPE, valuePath));
                return;
            }
            
            getInstance().getProfileManager().getProfilesConfig().save();
            
            // Reload if editing active profile
            if (profileName.equals(getInstance().getProfileManager().getActiveId())) {
                getInstance().getProfileManager().reload();
                sendMessage(sender, lang().of(Lang.KB_EDIT_RELOADED));
            }
            
        } catch (NumberFormatException e) {
            sendMessage(sender, lang().of(Lang.KB_EDIT_INVALID_FORMAT, currentValue instanceof Boolean ? "true/false" : "number"));
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            // Tab complete profile names
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
        } else if (args.length == 2) {
            // Tab complete value paths
            List<String> values = new ArrayList<>();
            for (String value : KB_VALUES) {
                if (value.toLowerCase().startsWith(args[1].toLowerCase())) {
                    values.add(value);
                }
            }
            return values;
        }
        return Collections.emptyList();
    }

    private LangManager lang() {
        return getInstance().getLangManager();
    }
}

package me.comunidad.dev.legacy.module.commands.type.knockback.arg;

import me.comunidad.dev.legacy.module.commands.CommandManager;
import me.comunidad.dev.legacy.framework.commands.Argument;
import me.comunidad.dev.legacy.module.lang.Lang;
import me.comunidad.dev.legacy.module.lang.LangManager;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Collections;
import java.util.List;

public class CreateProfileArgument extends Argument {

    public CreateProfileArgument(CommandManager manager) {
        super(manager, List.of("create", "new"));
        this.usage = lang().of(Lang.KB_CREATE_USAGE);
    }

    @Override
    public String usage() {
        return lang().of(Lang.KB_CREATE_USAGE);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sendUsage(sender);
            return;
        }

        String profileName = args[0];
        
        // Validate profile name
        if (profileName.contains(".") || profileName.contains(" ")) {
            sendMessage(sender, lang().of(Lang.KB_CREATE_INVALID_NAME));
            return;
        }

        ConfigurationSection root = getInstance().getProfileManager().getProfilesConfig().getConfigurationSection("profiles");
        if (root != null && root.contains(profileName)) {
            sendMessage(sender, lang().of(Lang.KB_CREATE_EXISTS));
            return;
        }

        // Create new profile with default values
        String path = "profiles." + profileName;
        getInstance().getProfileManager().getProfilesConfig().set(path + ".name", profileName);
        getInstance().getProfileManager().getProfilesConfig().set(path + ".active", false);
        
        // Set default knockback values
        getInstance().getProfileManager().getProfilesConfig().set(path + ".knockback.horizontal", 0.35);
        getInstance().getProfileManager().getProfilesConfig().set(path + ".knockback.vertical", 0.35);
        getInstance().getProfileManager().getProfilesConfig().set(path + ".knockback.vertical-limit", 0.40);
        getInstance().getProfileManager().getProfilesConfig().set(path + ".knockback.extra-vertical", 0.085);
        getInstance().getProfileManager().getProfilesConfig().set(path + ".knockback.extra-horizontal", 0.425);
        getInstance().getProfileManager().getProfilesConfig().set(path + ".knockback.sprint-modifier", 0.5);
        getInstance().getProfileManager().getProfilesConfig().set(path + ".knockback.sprint-reset-mod", 1.0);
        getInstance().getProfileManager().getProfilesConfig().set(path + ".knockback.horizontal-limit", 0.45);
        getInstance().getProfileManager().getProfilesConfig().set(path + ".knockback.friction", 2.0);
        getInstance().getProfileManager().getProfilesConfig().set(path + ".knockback.enabled", true);
        getInstance().getProfileManager().getProfilesConfig().set(path + ".knockback.ground_check", true);
        getInstance().getProfileManager().getProfilesConfig().set(path + ".knockback.dynamic-limit", false);
        getInstance().getProfileManager().getProfilesConfig().set(path + ".knockback.limit-horizontal", false);
        getInstance().getProfileManager().getProfilesConfig().set(path + ".knockback.one-point-seven", false);
        
        // Set default projectile values
        getInstance().getProfileManager().getProfilesConfig().set(path + ".knockback-projectile.horizontal", 0.35);
        getInstance().getProfileManager().getProfilesConfig().set(path + ".knockback-projectile.vertical", 0.35);
        getInstance().getProfileManager().getProfilesConfig().set(path + ".knockback-projectile.vertical-limit", 0.40);
        
        // Set default rod values
        getInstance().getProfileManager().getProfilesConfig().set(path + ".knockback-rod.horizontal", 0.35);
        getInstance().getProfileManager().getProfilesConfig().set(path + ".knockback-rod.vertical", 0.35);
        getInstance().getProfileManager().getProfilesConfig().set(path + ".knockback-rod.vertical-limit", 0.4);
        
        getInstance().getProfileManager().getProfilesConfig().save();
        
        sendMessage(sender, lang().of(Lang.KB_CREATE_SUCCESS, profileName));
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }

    private LangManager lang() {
        return getInstance().getLangManager();
    }
}

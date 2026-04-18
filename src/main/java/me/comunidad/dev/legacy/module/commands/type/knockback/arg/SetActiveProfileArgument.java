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

public class SetActiveProfileArgument extends Argument {

    public SetActiveProfileArgument(CommandManager manager) {
        super(manager, List.of("set", "activate", "switch"));
        this.usage = lang().of(Lang.KB_SET_USAGE);
    }

    @Override
    public String usage() {
        return lang().of(Lang.KB_SET_USAGE);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sendUsage(sender);
            return;
        }

        String profileName = args[0];
        
        ConfigurationSection root = getInstance().getProfileManager().getProfilesConfig().getConfigurationSection("profiles");
        if (root == null || !root.contains(profileName)) {
            sendMessage(sender, lang().of(Lang.KB_SET_NOT_FOUND, profileName));
            return;
        }

        String currentActive = getInstance().getProfileManager().getActiveId();
        if (profileName.equals(currentActive)) {
            sendMessage(sender, lang().of(Lang.KB_SET_ALREADY_ACTIVE, profileName));
            return;
        }

        getInstance().getProfileManager().activate(profileName);
        sendMessage(sender, lang().of(Lang.KB_SET_SUCCESS, profileName));
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

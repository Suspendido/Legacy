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

public class DeleteProfileArgument extends Argument {

    public DeleteProfileArgument(CommandManager manager) {
        super(manager, List.of("delete", "remove", "del"));
        this.usage = lang().of(Lang.KB_DELETE_USAGE);
    }

    @Override
    public String usage() {
        return lang().of(Lang.KB_DELETE_USAGE);
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
            sendMessage(sender, lang().of(Lang.KB_DELETE_NOT_FOUND, profileName));
            return;
        }

        // Check if it's the active profile
        String activeProfile = getInstance().getProfileManager().getActiveId();
        if (profileName.equals(activeProfile)) {
            sendMessage(sender, lang().of(Lang.KB_DELETE_ACTIVE));
            return;
        }

        // Check if it's the only profile
        if (root.getKeys(false).size() <= 1) {
            sendMessage(sender, lang().of(Lang.KB_DELETE_LAST));
            return;
        }

        getInstance().getProfileManager().delete(profileName);
        sendMessage(sender, lang().of(Lang.KB_DELETE_SUCCESS, profileName));
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            List<String> profiles = new ArrayList<>();
            ConfigurationSection root = getInstance().getProfileManager().getProfilesConfig().getConfigurationSection("profiles");
            if (root != null) {
                String activeProfile = getInstance().getProfileManager().getActiveId();
                for (String profile : root.getKeys(false)) {
                    // Don't suggest active profile for deletion
                    if (!profile.equals(activeProfile) && profile.toLowerCase().startsWith(args[0].toLowerCase())) {
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

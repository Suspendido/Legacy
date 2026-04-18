package me.comunidad.dev.legacy.module.commands.type.knockback.arg;

import me.comunidad.dev.legacy.module.commands.CommandManager;
import me.comunidad.dev.legacy.framework.commands.Argument;
import me.comunidad.dev.legacy.module.lang.Lang;
import me.comunidad.dev.legacy.module.lang.LangManager;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Collections;
import java.util.List;

public class ListProfilesArgument extends Argument {

    public ListProfilesArgument(CommandManager manager) {
        super(manager, List.of("list", "ls"));
        this.usage = lang().of(Lang.KB_LIST_USAGE);
    }

    @Override
    public String usage() {
        return lang().of(Lang.KB_LIST_USAGE);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        ConfigurationSection root = getInstance().getProfileManager().getProfilesConfig().getConfigurationSection("profiles");
        if (root == null || root.getKeys(false).isEmpty()) {
            sendMessage(sender, lang().of(Lang.KB_LIST_NONE));
            return;
        }

        String activeProfile = getInstance().getProfileManager().getActiveId();
        
        sendMessage(sender, lang().of(Lang.KB_LIST_TITLE));
        sendMessage(sender, "");
        
        for (String profileName : root.getKeys(false)) {
            boolean isActive = profileName.equals(activeProfile);
            String prefix = isActive ? lang().of(Lang.KB_LIST_ACTIVE) : lang().of(Lang.KB_LIST_INACTIVE);
            sendMessage(sender, prefix + profileName);
        }
        
        sendMessage(sender, "");
        sendMessage(sender, lang().of(Lang.KB_LIST_TOTAL, root.getKeys(false).size()));
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }

    private LangManager lang() {
        return getInstance().getLangManager();
    }
}

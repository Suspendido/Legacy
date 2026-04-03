package me.comunidad.dev.legacy.module.commands.type;

import me.comunidad.dev.legacy.framework.Config;
import me.comunidad.dev.legacy.framework.commands.Command;
import me.comunidad.dev.legacy.module.combat.menu.MainMenu;
import me.comunidad.dev.legacy.module.commands.CommandManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

/*
 * Copyright (c) 2026. @Comunidad, made since 2/4/2026
 * Use or redistribution of this source file is only permitted
 * if explicit permission is given by the author.
 */
public class LegacyEditorCommand extends Command {

    public LegacyEditorCommand(CommandManager manager) {
        super(manager, "menu");
        this.setPermissible("legacy.menu");
    }

    @Override
    public List<String> aliases() {
        return List.of();
    }

    @Override
    public List<String> usage() {
        return List.of();
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sendMessage(sender, Config.PLAYER_ONLY);
            return;
        }

        new MainMenu(getInstance().getMenuManager(), player).open();
    }
}

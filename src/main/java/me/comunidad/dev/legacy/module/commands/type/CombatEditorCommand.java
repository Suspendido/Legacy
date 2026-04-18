package me.comunidad.dev.legacy.module.commands.type;

import me.comunidad.dev.legacy.framework.Config;
import me.comunidad.dev.legacy.framework.commands.Command;
import me.comunidad.dev.legacy.module.combat.menu.MainMenu;
import me.comunidad.dev.legacy.module.commands.CommandManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

/*
 * Copyright (c) 2026. @Comunidad, made since 2/4/2026
 * Use or redistribution of this source file is only permitted
 * if explicit permission is given by the author.
 */
public class CombatEditorCommand extends Command {

    public CombatEditorCommand(CommandManager manager) {
        super(manager, "combat");
        this.setPermissible("legacy.menu");
    }

    @Override
    public List<String> aliases() {
        return List.of();
    }

    @Override
    public List<String> usage() {
        return List.of("&cUsage: &c/combat menu - Opens the ingame editor");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        switch (args[0].toLowerCase()) {
            case "menu" -> {
                if (!(sender instanceof Player player)) {
                    sendMessage(sender, Config.PLAYER_ONLY);
                    return;
                }
                new MainMenu(getInstance().getMenuManager(), player).open();
            }

            default -> sendUsage(sender);
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 0) return Collections.singletonList("menu");
        return Collections.emptyList();
    }
}

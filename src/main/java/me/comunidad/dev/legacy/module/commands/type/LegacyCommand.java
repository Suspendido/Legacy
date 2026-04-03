package me.comunidad.dev.legacy.module.commands.type;

import me.comunidad.dev.legacy.framework.Config;
import me.comunidad.dev.legacy.framework.Manager;
import me.comunidad.dev.legacy.module.commands.CommandManager;
import me.comunidad.dev.legacy.framework.commands.Command;
import me.comunidad.dev.legacy.utils.CC;
import me.comunidad.dev.legacy.utils.configs.ConfigYML;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.Arrays;
import java.util.List;

public class LegacyCommand extends Command {

    public LegacyCommand(CommandManager manager) {
        super(manager, "legacy");
        this.setPermissible("legacy.reload");
    }

    @Override
    public List<String> aliases() {
        return List.of();
    }

    @Override
    public List<String> usage() {
        return List.of(
                CC.LINE,
                "&6Legacy Combat",
                "",
                "&e/legacy reload &7— &fRecarga la configuración.",
                "&e/legacy version &7— &fMuestra la versión del plugin.",
                CC.LINE
        );
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sendUsage(sender);
            return;
        }

        switch (args[0].toLowerCase()) {
            case "reload" -> {
                sendMessage(sender, "&7Recargando configuración...");

                long start = System.currentTimeMillis();

                for (ConfigYML config : getInstance().getConfigs()) {
                    config.reloadCache(); // then re-cache all the objects in the cache.
                    config.reload(); // reload
                }

                for (Manager manage : getInstance().getManagers()) {
                    manage.reload();
                }

                Config.load(getInstance().getConfigsObject(), true);

                long elapsed = System.currentTimeMillis() - start;
                sendMessage(sender, "&aConfiguración recargada y sincronizada en &f" + elapsed + "ms&a.");
            }

            case "version" -> sendMessage(sender, "&b&lMineEffects &8➠ &fCurrent Version is &a" + getInstance().getDescription().getVersion() + "&f.");
            default -> sendUsage(sender);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onProcess(PlayerCommandPreprocessEvent e) {
        Player sender = e.getPlayer();
        String message = e.getMessage().toLowerCase();
        if (sender.hasPermission("mineffects.reload")) return;

        // /mineffects
        if (message.equals("/mineffects") || message.equals("/mineffects:" + name) || message.equals("/" + name)) {
            for (String line : Arrays.asList(
                    CC.LINE,
                    "&fThis server is running &dMineEffect&f. Made by &6C0munidad",
                    "&fOriginally made for &6&lMineLC&f.",
                    CC.LINE
            )) {
                sendMessage(sender, line);
            }
            return;
        }

        // Aliases
        for (String alias : aliases()) {
            if (message.equals("/mineffects:" + alias) || message.equals("/" + alias)) {
                for (String line : Arrays.asList(
                        CC.LINE,
                        "&fThis server is running &dMineEffect&f. Made by &6C0munidad",
                        "&fOriginally made for &6&lMineLC&f.",
                        CC.LINE
                )) {
                    sendMessage(sender, line);
                }
                return;
            }
        }
    }
}
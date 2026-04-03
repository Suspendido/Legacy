package me.comunidad.dev.legacy.utils.prompt.impl;

import me.comunidad.dev.legacy.Core;
import me.comunidad.dev.legacy.utils.prompt.Prompt;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public abstract class StringPrompt extends Prompt<String> {

    public StringPrompt(Core main) {
        super(main);
    }

    @Override
    public void handleFailed(Player player, String input) {
        player.sendMessage(ChatColor.RED + "The input '" + ChatColor.WHITE + input + ChatColor.RED + "' is invalid.");
    }

    @Override
    public String handleInput(Player player, String input) {
        return (input == null || input.isEmpty()) ? null : input;
    }
}

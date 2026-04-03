package me.comunidad.dev.legacy.utils.prompt.impl;

import me.comunidad.dev.legacy.Core;
import me.comunidad.dev.legacy.utils.prompt.Prompt;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public abstract class IntegerPrompt extends Prompt<Integer> {

    public IntegerPrompt(Core main) {
        super(main);
    }

    @Override
    public void handleFailed(Player player, String input) {
        player.sendMessage(ChatColor.RED + "Invalid integer value: " + ChatColor.WHITE + input);
    }

    @Override
    public Integer handleInput(Player player, String input) {
        return toInt(input);
    }

    public int toInt(String input){
        try{
            return Integer.parseInt(input);
        }catch (Exception e){
            return 0;
        }
    }
}

package me.comunidad.dev.legacy.utils.prompt;

import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;

@RequiredArgsConstructor
public class PromptListener implements Listener {

    private final PromptManager promptManager;

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        if (promptManager.getPromptByPlayer(event.getPlayer()) != null) {
            promptManager.handleChat(event, promptManager.getPromptByPlayer(event.getPlayer()).getPrompt());
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        if (promptManager.getPromptByPlayer(event.getPlayer()) != null) {
            promptManager.getPromptMap().remove(event.getPlayer().getUniqueId());
        }
    }
}

package me.comunidad.dev.legacy.utils.prompt;

import lombok.RequiredArgsConstructor;
import me.comunidad.dev.legacy.utils.CC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.UUID;

@RequiredArgsConstructor
public class PromptRunnable implements Runnable {

    private final PromptManager promptManager;

    @Override
    public void run() {
        Iterator<Entry<UUID, PromptData>> iterator = promptManager.getPromptMap().entrySet().iterator();
        while (iterator.hasNext()) {
            Entry<UUID, PromptData> entry = iterator.next();
            PromptData promptData = entry.getValue();
            long startMillis = promptData.getStartMillis();

            if ((System.currentTimeMillis() - startMillis) > promptData.getTimeout()) {
                Player player = Bukkit.getPlayer(entry.getKey());
                if (player != null) {
                    player.sendMessage(CC.t("&cYou have exceded the conversation time for response, you were removed from them."));
                }

                iterator.remove();
            }
        }
    }
}

package me.comunidad.dev.legacy.framework;

import lombok.Getter;
import me.comunidad.dev.legacy.Core;
import me.comunidad.dev.legacy.framework.extra.Configs;
import me.comunidad.dev.legacy.utils.CC;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.lang.reflect.Method;

@Getter
public abstract class Module<T extends Manager> extends Configs implements Listener {

    private final Core instance;
    private final T manager;

    public Module(T manager) {
        this.instance = manager.getInstance();
        this.manager = manager;
        this.checkListener();
    }

    private void checkListener() {
        for (Method method : getClass().getMethods()) {
            if (method.isAnnotationPresent(EventHandler.class)) {
                manager.registerListener(this);
                break; // Break the loop, we already know it's a listener now.
            }
        }
    }
    public void sendMessage(CommandSender player, String... s) {
        for (String s_ : s) {
            player.sendMessage(CC.t(s_));
        }
    }
    public void broadcast(String... s) {
        for (String string : s) {
            String finalString = CC.t(string);
            Bukkit.getOnlinePlayers().forEach(player -> {
                sendMessage(player, finalString);
            });
        }
    }
}
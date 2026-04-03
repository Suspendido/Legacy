package me.comunidad.dev.legacy.framework.menu;

import lombok.Getter;
import me.comunidad.dev.legacy.Core;
import me.comunidad.dev.legacy.framework.Manager;
import me.comunidad.dev.legacy.framework.menu.listener.MenuListener;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public class MenuManager extends Manager {

    private final Map<UUID, Menu> menus;

    public MenuManager(Core instance) {
        super(instance);
        this.menus = new HashMap<>();
        new MenuListener(this);
    }
}
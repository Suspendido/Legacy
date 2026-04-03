package me.comunidad.dev.legacy.module.placeholder;

import me.comunidad.dev.legacy.Core;
import me.comunidad.dev.legacy.framework.Manager;
import me.comunidad.dev.legacy.module.placeholder.type.NonePlaceholderHook;
import me.comunidad.dev.legacy.module.placeholder.type.PlaceholderAPIHook;
import me.comunidad.dev.legacy.utils.Utils;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Copyright (c) 2025. Keano
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public class PlaceholderHook extends Manager implements Placeholder {

    private Placeholder placeholder;

    public PlaceholderHook(Core instance) {
        super(instance);
        this.load();
    }

    private void load() {
        if (Utils.verifyPlugin("PlaceholderAPI", getInstance())) {
            placeholder = new PlaceholderAPIHook(this);

        } else {
            placeholder = new NonePlaceholderHook(this);
        }
    }

    @Override
    public String replace(Player player, String string) {
        return placeholder.replace(player, string);
    }

    @Override
    public List<String> replace(Player player, List<String> list) {
        return placeholder.replace(player, list);
    }
}
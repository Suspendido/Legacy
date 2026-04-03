package me.comunidad.dev.legacy.module.placeholder.type;

import me.comunidad.dev.legacy.framework.Module;
import me.comunidad.dev.legacy.module.placeholder.Placeholder;
import me.comunidad.dev.legacy.module.placeholder.PlaceholderHook;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Copyright (c) 2025. Keano
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public class NonePlaceholderHook extends Module<PlaceholderHook> implements Placeholder {

    public NonePlaceholderHook(PlaceholderHook manager) {
        super(manager);
    }

    @Override
    public String replace(Player player, String string) {
        return string;
    }

    @Override
    public List<String> replace(Player player, List<String> list) {
        return list;
    }
}
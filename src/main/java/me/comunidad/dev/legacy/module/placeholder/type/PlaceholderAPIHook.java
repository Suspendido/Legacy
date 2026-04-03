package me.comunidad.dev.legacy.module.placeholder.type;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.comunidad.dev.legacy.framework.Module;
import me.comunidad.dev.legacy.module.placeholder.Placeholder;
import me.comunidad.dev.legacy.module.placeholder.PlaceholderHook;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

/**
 * Copyright (c) 2025. Keano
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public class PlaceholderAPIHook extends Module<PlaceholderHook> implements Placeholder {

    public PlaceholderAPIHook(PlaceholderHook manager) {
        super(manager);
        this.load();
    }

    private void load() {
        new PlaceholderExpansion() {
            @Override
            public @NotNull String getIdentifier() {
                return "mineffects";
            }

            @Override
            public @NotNull String getAuthor() {
                return "comunidad";
            }

            @Override
            public @NotNull String getVersion() {
                return "1.0";
            }

            @Override
            public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
                if (player == null) {
                    return null;
                }


                return null;
            }
        }.register();
    }

    @Override
    public String replace(Player player, String string) {
        if (player == null || string == null) return "";
        return PlaceholderAPI.setPlaceholders(player, string);
    }

    @Override
    public List<String> replace(Player player, List<String> list) {
        if (player == null || list == null) return Collections.emptyList();
        return PlaceholderAPI.setPlaceholders(player, list);
    }
}
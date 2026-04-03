package me.comunidad.dev.legacy.module.listener.events;

import lombok.Generated;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

/*
 * Copyright (c) 2026. @Comunidad, made since 2/4/2026
 * Use or redistribution of this source file is only permitted
 * if explicit permission is given by the author.
 */
public final class PlayerSprintResetEvent extends PlayerEvent {
    public static final HandlerList handlerList = new HandlerList();

    public PlayerSprintResetEvent(Player who) {
        super(who);
    }

    @NotNull
    public HandlerList getHandlers() {
        return handlerList;
    }

    @Generated
    public static HandlerList getHandlerList() {
        return handlerList;
    }
}

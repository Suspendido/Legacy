package me.comunidad.dev.legacy.utils.extra;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadFactory;

public class NameThreadFactory implements ThreadFactory {

    private final String name;

    public NameThreadFactory(String name) {
        this.name = name;
    }

    @Override
    public Thread newThread(@NotNull Runnable r) {
        return new Thread(r, name);
    }
}
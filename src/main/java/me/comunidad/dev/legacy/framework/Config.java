package me.comunidad.dev.legacy.framework;

import me.comunidad.dev.legacy.framework.extra.Configs;

public class Config {

    public static String PLAYER_NOT_FOUND;
    public static String PLAYER_ONLY;
    public static String INSUFFICIENT_PERM;
    public static String NOT_VALID_NUMBER;
    public static String SERVER_NOT_LOADED;
    public static String COULD_NOT_LOAD_DATA;

    public static void load(Configs configs, boolean reload) {
        // Global
        PLAYER_NOT_FOUND = configs.getLanguageConfig().getString("GLOBAL_COMMANDS.PLAYER_NOT_FOUND");
        PLAYER_ONLY = configs.getLanguageConfig().getString("GLOBAL_COMMANDS.PLAYER_ONLY");
        INSUFFICIENT_PERM = configs.getLanguageConfig().getString("GLOBAL_COMMANDS.INSUFFICIENT_PERMISSION");
        NOT_VALID_NUMBER = configs.getLanguageConfig().getString("GLOBAL_COMMANDS.NOT_VALID_NUMBER");

        SERVER_NOT_LOADED = configs.getLanguageConfig().getString("USER_LISTENER.SERVER_NOT_LOADED");
        COULD_NOT_LOAD_DATA = configs.getLanguageConfig().getString("USER_LISTENER.COULD_NOT_LOAD_DATA");
    }
}
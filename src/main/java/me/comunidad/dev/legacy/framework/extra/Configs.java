package me.comunidad.dev.legacy.framework.extra;

import me.comunidad.dev.legacy.Core;
import me.comunidad.dev.legacy.framework.Config;
import me.comunidad.dev.legacy.utils.Formatter;
import me.comunidad.dev.legacy.utils.ItemUtils;
import me.comunidad.dev.legacy.utils.configs.ConfigYML;

import java.io.File;

public class Configs {

    private static ConfigYML CONFIG;
    private static ConfigYML LANGUAGE_CONFIG;
    private static ConfigYML ITEMS_CONFIG;
    private static ConfigYML MISC_CONFIG;
    private static ConfigYML PROFILES_CONFIG;

    public void load(Core instance) {
        CONFIG = new ConfigYML(instance, "config");
        LANGUAGE_CONFIG = new ConfigYML(instance, "language");
        PROFILES_CONFIG = new ConfigYML(instance, "profiles");
        MISC_CONFIG = new ConfigYML(instance, "data" + File.separator + "misc");
        ITEMS_CONFIG = new ConfigYML(instance, "data" + File.separator + "items");


        new ItemUtils(this);
        Config.load(this, false);
        Formatter.loadFormats(instance, this);
    }

    public ConfigYML getConfig() {
        return CONFIG;
    }
    public ConfigYML getLanguageConfig() {
        return LANGUAGE_CONFIG;
    }
    public ConfigYML getItemsConfig() {
        return ITEMS_CONFIG;
    }
    public ConfigYML getMiscConfig() {
        return MISC_CONFIG;
    }
    public ConfigYML getProfilesConfig() {
        return PROFILES_CONFIG;
    }

}
package me.comunidad.dev.legacy.utils;

import org.bukkit.Bukkit;

public class Logger {

    public static final String LINE_CONSOLE = CC.t("&7&m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

    public static void state(String state, int managers) {
        print("");
        print("       &5&lLegacyCombat &fv" + Bukkit.getPluginManager().getPlugin("Legacy").getDescription().getVersion());
        print("");
        print("       &fState&7: &5" + state);
        print("     &7Bringing back the Old Combat!");
        print("  &7&oMineLC Exclusive @ Made by C0munidad");
        print("");
    }

    public static void print(String... message) {
        for (String s : message) {
            Bukkit.getServer().getConsoleSender().sendMessage(CC.t(s));
        }
    }

    private static String convert(String string) {
        return string.equalsIgnoreCase("enabled") ? "Loaded" : "Saved";
    }
}
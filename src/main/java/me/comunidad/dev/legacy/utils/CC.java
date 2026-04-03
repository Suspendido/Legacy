package me.comunidad.dev.legacy.utils;

import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;

import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class CC {

    private static final Function<String, String> REPLACER;
    private static final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");
    private static final char COLOR_CHAR = ChatColor.COLOR_CHAR;

    static {
        REPLACER = s -> {
            Matcher matcher = HEX_PATTERN.matcher(s);
            StringBuilder buffer = new StringBuilder(s.length() + 4 * 8);
            while (matcher.find()) {
                String group = matcher.group(1);
                matcher.appendReplacement(buffer, COLOR_CHAR + "x"
                        + COLOR_CHAR + group.charAt(0) + COLOR_CHAR + group.charAt(1)
                        + COLOR_CHAR + group.charAt(2) + COLOR_CHAR + group.charAt(3)
                        + COLOR_CHAR + group.charAt(4) + COLOR_CHAR + group.charAt(5)
                );
            }
            return ChatColor.translateAlternateColorCodes('&', matcher.appendTail(buffer).toString());
        };
    }

    public static final String LINE = t("&7&m-------------------------");

    public static String t(String t) {
        return REPLACER.apply(t);
    }

    public static Component tComponent(String t) {
        return Serializer.LEGACY.deserialize(t);
    }

    public static List<String> t(List<String> t) {
        return t.stream().map(REPLACER).collect(Collectors.toList());
    }

    public static List<Component> tComponent(List<String> t) {
        return t.stream().map(CC::tComponent).collect(Collectors.toList());
    }
}
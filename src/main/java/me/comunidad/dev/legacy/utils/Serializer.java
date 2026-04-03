package me.comunidad.dev.legacy.utils;

import me.comunidad.dev.legacy.utils.extra.Pair;
import me.comunidad.dev.legacy.utils.fanciful.FancyMessage;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;
import java.util.stream.Collectors;

public class Serializer {

    static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.legacyAmpersand();

    public static List<FancyMessage> loadFancyMessages(List<String> toSend) {
        List<FancyMessage> list = new ArrayList<>();

        for (String string : toSend) {
            if (!string.contains("<f>")) {
                list.add(new FancyMessage(string));
                continue;
            }

            FancyMessage current = new FancyMessage();

            while (string.contains("<f>")) {
                Pair<String, FancyMessage> pair = getFancy(current, string);
                string = pair.getFirst();
                current = pair.getSecond();
            }

            list.add(current);
        }

        return list;
    }

    private static Pair<String, FancyMessage> getFancy(FancyMessage fancyMessage, String string) {
        int first = string.indexOf("<f>");
        String firstPart = "";
        String lastPart = "";

        if (first > 0) {
            firstPart = string.substring(0, first);
        }

        int last = string.indexOf("<f>", first + 1);

        if (last == -1) {
            throw new IllegalArgumentException("Fancy message bracket started but not finished.");
        }

        if (last + 3 < string.length()) {
            lastPart = string.substring(last + 3);
        }

        string = lastPart;
        fancyMessage.text(firstPart);

        fancyMessage.then().text(lastPart);
        return new Pair<>(string, fancyMessage);
    }


    public static Location deserializeLoc(String string, boolean includeCamera) {
        if (string.equals("null")) {
            return null;
        }

        String[] split = string.split(", ");
        if (includeCamera)
            return new Location(Bukkit.getWorld(split[0]),
                    parseDouble(split[1]), parseDouble(split[2]), parseDouble(split[3]),
                    parseFloat(split[4]), parseFloat(split[5]));
        else
            return new Location(Bukkit.getWorld(split[0]),
                    parseDouble(split[1]), parseDouble(split[2]), parseDouble(split[3]));
    }

    public static Location deserializeLoc(String string) {
        if (string.equals("null")) {
            return null;
        }

        String[] split = string.split(", ");
        return new Location(Bukkit.getWorld(split[0]), parseDouble(split[1]), parseDouble(split[2]), parseDouble(split[3]), parseFloat(split[4]), parseFloat(split[5]));
    }


    public static List<Location> fetchLocations(Object object) {
        List<String> list = Utils.createList(object, String.class);
        return list.stream().map(Serializer::fetchLocation).collect(Collectors.toList());
    }

    public static List<String> serializeLocations(List<Location> locations) {
        return locations.stream().map(Serializer::serializeLoc).collect(Collectors.toList());
    }

    public static List<Material> fetchMaterials(Object object) {
        List<String> list = Utils.createList(object, String.class);
        return list.stream().map(ItemUtils::getMat).collect(Collectors.toList());
    }

    public static List<String> serializeUUIDs(Collection<UUID> list) {
        return list.stream().map(UUID::toString).collect(Collectors.toList());
    }

    public static Set<UUID> fetchUUIDs(Object object) {
        List<String> list = Utils.createList(object, String.class);
        return list.stream().map(UUID::fromString).collect(Collectors.toSet());
    }

    public static String serializeLoc(Location location, boolean includeCamera) {
        if (location == null) {
            return "null";
        }

        if (includeCamera)
            return location.getWorld().getName() + ", " + location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ() + ", " + location.getYaw() + ", " + location.getPitch();
        else
            return location.getWorld().getName() + ", " + location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ();
    }

    public static String serializeLoc(Location location) {
        return serializeLoc(location, true);
    }

    public static Location fetchLocation(String string) {
        if (string.equals("null")) return null;
        String[] split = string.split(", ");
        return new Location(Bukkit.getWorld(split[0]),
                parseDouble(split[1]), parseDouble(split[2]), parseDouble(split[3]),
                parseFloat(split[4]), parseFloat(split[5]));
    }

    public static Location fetchLocationAddOffset(String string) {
        if (string.equals("null")) return null;
        String[] split = string.split(", ");
        return new Location(Bukkit.getWorld(split[0]),
                parseDouble(split[1]), parseDouble(split[2]), parseDouble(split[3]),
                parseFloat(split[4]), parseFloat(split[5])).clone().add(0.5, 1, 0.5);
    }

    public static PotionEffect getEffect(String string) {
        if (string.isEmpty()) {
            return null;
        }

        String[] split = string.split(", ");

        try {

            int duration = (split[1].equals("MAX_VALUE") ? (-1) : 20 * parseInt(split[1].replaceAll("s", "")));
            return new PotionEffect(PotionEffectType.getByName(split[0]), duration, parseInt(split[2]) - 1);

        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Effect " + split[0] + " does not exist!");
        }
    }

    public static List<TextComponent> deserializeStrings(List<String> strings) {
        return strings.stream().map(LEGACY::deserialize).toList();
    }

    public static List<TextComponent> replacePlaceholders(List<TextComponent> components, Map<String, ?> replacements) {
        return components.stream()
                .map(c -> replacePlaceholders(c, replacements))
                .toList();
    }

    public static TextComponent replacePlaceholders(TextComponent component, Map<String, ?> replacements) {
        String text = LEGACY.serialize(component);
        for (Map.Entry<String, ?> entry : replacements.entrySet()) {
            if (entry.getValue() instanceof Component) {
                text = text.replace(entry.getKey(), LEGACY.serialize((Component) entry.getValue()));
                continue;
            }

            text = text.replace(entry.getKey(), entry.getValue().toString());
        }

        return LEGACY.deserialize(text);
    }

    private static Integer parseInt(String string) {
        return Integer.parseInt(string);
    }

    private static Double parseDouble(String string) {
        return Double.parseDouble(string);
    }

    private static Float parseFloat(String string) {
        return Float.parseFloat(string);
    }
}
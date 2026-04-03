package me.comunidad.dev.legacy.utils.fanciful;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.gson.stream.JsonWriter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.UnaryOperator;
import java.util.logging.Level;

/**
 * Internal class: Represents a component of a JSON-serializable {@link FancyMessage}.
 */
@Deprecated
@Setter
public class MessagePart implements JsonRepresentedObject, ConfigurationSerializable, Cloneable {

    public ChatColor color = ChatColor.WHITE;
    public ArrayList<ChatColor> styles = new ArrayList<>();
    public String clickActionName = null, clickActionData = null, hoverActionName = null;
    public JsonRepresentedObject hoverActionData = null;
    public TextualComponent text;
    public String insertionData = null;
    public ArrayList<JsonRepresentedObject> translationReplacements = new ArrayList<>();

    public MessagePart(final TextualComponent text) {
        this.text = text;
    }

    public MessagePart() {
        this.text = null;
    }

    public boolean hasText() {
        return text != null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public MessagePart clone() throws CloneNotSupportedException {
        MessagePart obj = (MessagePart) super.clone();
        obj.styles = (ArrayList<ChatColor>) styles.clone();
        if (hoverActionData instanceof JsonString) {
            obj.hoverActionData = new JsonString(((JsonString) hoverActionData).getValue());
        } else if (hoverActionData instanceof FancyMessage) {
            obj.hoverActionData = ((FancyMessage) hoverActionData).clone();
        }
        obj.translationReplacements = (ArrayList<JsonRepresentedObject>) translationReplacements.clone();
        return obj;

    }

    static final BiMap<ChatColor, String> stylesToNames;

    static {
        ImmutableBiMap.Builder<ChatColor, String> builder = ImmutableBiMap.builder();
        for (final ChatColor style : ChatColor.values()) {
            if (!style.isFormat()) {
                continue;
            }

            String styleName = switch (style) {
                case MAGIC -> "obfuscated";
                case UNDERLINE -> "underlined";
                default -> style.name().toLowerCase();
            };

            builder.put(style, styleName);
        }
        stylesToNames = builder.build();
    }

    public void writeJson(JsonWriter json) {
        this.writeJson(json, UnaryOperator.identity());
    }

    public void writeJson(JsonWriter json, UnaryOperator<String> replacer) {
        try {
            json.beginObject();
            text.writeJson(json, replacer);
            json.name("color").value(color.name().toLowerCase());
            for (final ChatColor style : styles) {
                json.name(stylesToNames.get(style)).value(true);
            }
            if (clickActionName != null && clickActionData != null) {
                json.name("clickEvent")
                        .beginObject()
                        .name("action").value(clickActionName)
                        .name("value").value(replacer.apply(clickActionData))
                        .endObject();
            }
            if (hoverActionName != null && hoverActionData != null) {
                json.name("hoverEvent")
                        .beginObject()
                        .name("action").value(hoverActionName)
                        .name("value");
                hoverActionData.writeJson(json, replacer);
                json.endObject();
            }
            if (insertionData != null) {
                json.name("insertion").value(insertionData);
            }
            if (!translationReplacements.isEmpty() && TextualComponent.isTranslatableText(text)) {
                json.name("with").beginArray();
                for (JsonRepresentedObject obj : translationReplacements) {
                    obj.writeJson(json, replacer);
                }
                json.endArray();
            }
            json.endObject();
        } catch (IOException e) {
            Bukkit.getLogger().log(Level.WARNING, "A problem occured during writing of JSON string", e);
        }
    }

    public @NotNull Map<String, Object> serialize() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("text", text);
        map.put("styles", styles);
        map.put("color", color.getChar());
        map.put("hoverActionName", hoverActionName);
        map.put("hoverActionData", hoverActionData);
        map.put("clickActionName", clickActionName);
        map.put("clickActionData", clickActionData);
        map.put("insertion", insertionData);
        map.put("translationReplacements", translationReplacements);
        return map;
    }

    @SuppressWarnings("unchecked")
    public static MessagePart deserialize(Map<String, Object> serialized) {
        MessagePart part = new MessagePart((TextualComponent) serialized.get("text"));
        part.styles = (ArrayList<ChatColor>) serialized.get("styles");
        part.color = ChatColor.getByChar(serialized.get("color").toString());
        part.hoverActionName = (String) serialized.get("hoverActionName");
        part.hoverActionData = (JsonRepresentedObject) serialized.get("hoverActionData");
        part.clickActionName = (String) serialized.get("clickActionName");
        part.clickActionData = (String) serialized.get("clickActionData");
        part.insertionData = (String) serialized.get("insertion");
        part.translationReplacements = (ArrayList<JsonRepresentedObject>) serialized.get("translationReplacements");
        return part;
    }

    static {
        ConfigurationSerialization.registerClass(MessagePart.class);
    }
}
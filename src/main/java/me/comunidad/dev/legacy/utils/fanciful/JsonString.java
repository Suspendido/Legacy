package me.comunidad.dev.legacy.utils.fanciful;

import com.google.gson.stream.JsonWriter;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.UnaryOperator;

/**
 * Represents a JSON string value.
 * Writes by this object will not write name values nor begin/end objects in the JSON stream.
 * All writes merely write the represented string value.
 */
@Deprecated
final class JsonString implements JsonRepresentedObject, ConfigurationSerializable {

    private final String _value;

    public JsonString(CharSequence value) {
        _value = value == null ? null : value.toString();
    }

    public void writeJson(JsonWriter writer, UnaryOperator<String> replacer) throws IOException {
        writer.value(replacer.apply(getValue()));
    }

    public String getValue() {
        return _value;
    }

    public @NotNull Map<String, Object> serialize() {
        HashMap<String, Object> theSingleValue = new HashMap<>();
        theSingleValue.put("stringValue", _value);
        return theSingleValue;
    }

    public static JsonString deserialize(Map<String, Object> map) {
        return new JsonString(map.get("stringValue").toString());
    }

    @Override
    public String toString() {
        return _value;
    }

}

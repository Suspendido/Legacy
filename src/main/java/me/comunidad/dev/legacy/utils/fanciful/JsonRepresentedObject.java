package me.comunidad.dev.legacy.utils.fanciful;

import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.function.UnaryOperator;

/**
 * Represents an object that can be serialized to a JSON writer instance.
 */
@Deprecated
public interface JsonRepresentedObject {

    /**
     * Writes the JSON representation of this object to the specified writer.
     *
     * @param writer The JSON writer which will receive the object.
     * @throws IOException If an error occurs writing to the stream.
     */
    void writeJson(JsonWriter writer, UnaryOperator<String> replacer) throws IOException;

}

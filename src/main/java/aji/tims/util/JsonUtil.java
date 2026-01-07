package aji.tims.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.Map;

public class JsonUtil {
    public static void writeJson(JsonWriter writer, JsonElement element) throws IOException {
        if (element.isJsonArray()) {
            writer.beginArray();
            for (JsonElement jsonElement : element.getAsJsonArray()) {
                writeJson(writer, jsonElement);
            }
            writer.endArray();
        } else if (element.isJsonObject()) {
            writer.beginObject();
            for (Map.Entry<String, JsonElement> entry : element.getAsJsonObject().entrySet()) {
                writer.name(entry.getKey());
                writeJson(writer, entry.getValue());
            }
            writer.endObject();
        } else if (element.isJsonNull()) {
            writer.nullValue();
        } else if (element.isJsonPrimitive()) {
            JsonPrimitive primitive = element.getAsJsonPrimitive();
            if (primitive.isString()) {
                writer.value(primitive.getAsString());
            } else if (primitive.isNumber()) {
                writer.value(primitive.getAsNumber());
            } else if (primitive.isBoolean()) {
                writer.value(primitive.getAsBoolean());
            }
        }
    }
}

package ru.tensor.sbis.network_native.json.adapter;

import androidx.annotation.NonNull;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

import ru.tensor.sbis.network_native.type.FieldDescription;
import ru.tensor.sbis.network_native.type.RecFormat;


/**
 * Конвертер RecFormat в Json и обратно.
 */
public class RecFormatJsonTypeAdapter implements JsonSerializer<RecFormat>, JsonDeserializer<RecFormat> {

    @NonNull
    @Override
    public JsonElement serialize(@NonNull RecFormat src, Type typeOfSrc, JsonSerializationContext context) {
        JsonArray sArray = new JsonArray();
        for (FieldDescription fd : src) {
            sArray.add(fd.getFieldDescription());
        }

        return sArray;
    }

    @NonNull
    @Override
    public RecFormat deserialize(@NonNull JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        RecFormat recFormat = new RecFormat();
        JsonArray jsonArray = json.getAsJsonArray();
        for (JsonElement jElem : jsonArray) {
            JsonObject jObject = jElem.getAsJsonObject();
            JsonElement t = jObject.get("t");
            JsonElement n = jObject.get("n");

            FieldDescription fd;
            if (t.isJsonPrimitive()) {
                fd = new FieldDescription(n.getAsString(), t.getAsString(), jObject);
            } else {
                fd = new FieldDescription(n.getAsString(), t.getAsJsonObject().get("n").getAsString(), jObject);
            }

            recFormat.add(fd);
        }

        return recFormat;
    }
}

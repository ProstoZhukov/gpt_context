package ru.tensor.sbis.network_native.json.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

import ru.tensor.sbis.network_native.type.ObjectId;


/**
 * Конвертер ObjectId в Json объект и обратно.
 */
public class ObjectIdJsonTypeAdapter implements JsonSerializer<ObjectId>, JsonDeserializer<ObjectId> {

    @NonNull
    @Override
    public JsonElement serialize(@NonNull ObjectId src, Type typeOfSrc, JsonSerializationContext context) {
        JsonArray array = new JsonArray();
        array.add(new JsonPrimitive(src.getValue()));
        if (src.getObject() != null) {
            array.add(new JsonPrimitive(src.getObject()));
        }

        return array;
    }

    @Nullable
    @Override
    public ObjectId deserialize(@NonNull JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (!json.isJsonArray()) {
            throw new JsonParseException("Не удалось разобрать JSON объект в ObjectId");
        }
        JsonArray jArray = json.getAsJsonArray();
        JsonElement element = jArray.get(0);
        if (element.equals(JsonNull.INSTANCE)) {
            return null;
        } else {
            long value = jArray.get(0).getAsLong();
            String object = null;
            if (jArray.size() > 1) {
                object = jArray.get(1).getAsString();
            }

            return new ObjectId(value, object);
        }

    }
}

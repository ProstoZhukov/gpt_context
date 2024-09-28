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

import ru.tensor.sbis.network_native.type.RecFormat;
import ru.tensor.sbis.network_native.type.Record;
import ru.tensor.sbis.network_native.type.RecordSet;


/**
 * Конвертер RecordSet в Json и обратно.
 */
public class RecordSetJsonTypeAdapter implements JsonSerializer<RecordSet>, JsonDeserializer<RecordSet> {

    @NonNull
    @Override
    public JsonElement serialize(@NonNull RecordSet src, Type typeOfSrc, @NonNull JsonSerializationContext context) {
        JsonElement s = null;
        JsonArray d = new JsonArray();

        for (Record rec : src) {
            if (s == null) {
                s = context.serialize(rec.getFormat());
            }

            JsonObject recJson = (JsonObject) context.serialize(rec);
            d.add(recJson.get("d"));
        }

        JsonObject recordSetJson = new JsonObject();
        recordSetJson.add("s", s);
        recordSetJson.add("d", d);

        return recordSetJson;
    }

    @NonNull
    @Override
    public RecordSet deserialize(@NonNull JsonElement json, Type typeOfT, @NonNull JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        JsonElement s = jsonObject.get("s");
        JsonElement d = jsonObject.get("d");

        if (s == null) {
            throw new JsonParseException("Невозможно преобразовать JSON в RecordSet: ключ \"s\" отсутствует.");
        }
        if (d == null) {
            throw new JsonParseException("Невозможно преобразовать JSON в RecordSet: ключ \"d\" отсутствует.");
        }
        RecFormat recFormat = context.deserialize(s, RecFormat.class);
        RecordSet recordSet = new RecordSet();

        JsonArray dArray = d.getAsJsonArray();
        RecordJsonTypeAdapter recordAdapter = new RecordJsonTypeAdapter();
        for (JsonElement dArrayElement : dArray) {
            Record record = recordAdapter.deserialize(recFormat, dArrayElement.getAsJsonArray(), context);
            recordSet.add(record);
        }

        return recordSet;
    }
}

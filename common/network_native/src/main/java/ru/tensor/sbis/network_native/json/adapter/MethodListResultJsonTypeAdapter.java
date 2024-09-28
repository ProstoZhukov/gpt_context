package ru.tensor.sbis.network_native.json.adapter;

import androidx.annotation.NonNull;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

import ru.tensor.sbis.network_native.type.MethodListResult;
import ru.tensor.sbis.network_native.type.RecFormat;
import ru.tensor.sbis.network_native.type.Record;
import ru.tensor.sbis.network_native.type.RecordSet;


/**
 * Legacy-код
 * <p>
 * Created by da.rodionov on 22.07.15.
 */
public class MethodListResultJsonTypeAdapter implements JsonDeserializer<MethodListResult> {

    @NonNull
    @Override
    public MethodListResult deserialize(@NonNull JsonElement json, Type typeOfT, @NonNull JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        JsonElement s = jsonObject.get("s");
        JsonElement d = jsonObject.get("d");
        JsonElement n = jsonObject.get("n");

        if (s == null) {
            throw new JsonParseException("Невозможно преобразовать JSON в RecordSet: ключ \"s\" отсутствует.");
        }
        if (d == null) {
            throw new JsonParseException("Невозможно преобразовать JSON в RecordSet: ключ \"d\" отсутствует.");
        }
        if (n == null) {
            throw new JsonParseException("Невозможно преобразовать JSON в RecordSet: ключ \"n\" отсутствует.");
        }
        RecFormat recFormat = context.deserialize(s, RecFormat.class);
        RecordSet recordSet = new RecordSet();

        JsonArray dArray = d.getAsJsonArray();
        RecordJsonTypeAdapter recordAdapter = new RecordJsonTypeAdapter();
        for (JsonElement dArrayElement : dArray) {
            Record record = recordAdapter.deserialize(recFormat, dArrayElement.getAsJsonArray(), context);
            recordSet.add(record);
        }

        boolean hasMore = n.getAsBoolean();

        return new MethodListResult(recordSet, hasMore);
    }

}
package ru.tensor.sbis.network_native.json.adapter;

import androidx.annotation.NonNull;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

import ru.tensor.sbis.network_native.error.SbisError;


/**
 * Конвертер типа SbisError из Json.
 */
public class SbisErrorJsonTypeAdapter implements JsonDeserializer<SbisError> {

    @NonNull
    @Override
    public SbisError deserialize(@NonNull JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jObject = json.getAsJsonObject();
        String message = jObject.get("message").getAsString();
        String details = jObject.get("details").getAsString();

        JsonElement dataObj = jObject.get("data");

        JsonElement data = null;

        if (dataObj != null && !dataObj.isJsonNull()) {
            JsonObject dataObjJsonObj = dataObj.getAsJsonObject();
            String addinfoFieldName = "addinfo";
            if (dataObjJsonObj.has(addinfoFieldName) && !dataObjJsonObj.get(addinfoFieldName).isJsonNull()) {
                data = dataObjJsonObj.get(addinfoFieldName);
            }
        }

        return new SbisError(message, details, 0, data);
    }
}

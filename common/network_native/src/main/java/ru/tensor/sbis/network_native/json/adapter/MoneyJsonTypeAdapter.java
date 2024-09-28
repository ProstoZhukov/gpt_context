package ru.tensor.sbis.network_native.json.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

import ru.tensor.sbis.network_native.type.Money;


/**
 * Конвертер типа Money в Json и обратно.
 * <p/>
 * TODO: не доделан серелизатор
 */
public class MoneyJsonTypeAdapter implements JsonSerializer<Money>, JsonDeserializer<Money> {

    @NonNull
    @Override
    public Money deserialize(@NonNull JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return new Money(json.getAsDouble());
    }

    @Nullable
    @Override
    public JsonElement serialize(Money src, Type typeOfSrc, JsonSerializationContext context) {
        return null;
    }
}

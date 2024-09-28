package ru.tensor.sbis.network_native.json.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Класс для преобразования Date в Json и обратно.
 */
public class DateTimeJsonTypeAdapter implements JsonSerializer<Date>, JsonDeserializer<Date> {

    private final Locale locale = new Locale("ru");

    private final SimpleDateFormat dateFormatFull = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss.SSSZZZ", locale);
    private final SimpleDateFormat dateFormatDateOnly = new SimpleDateFormat("yyyy-MM-dd", locale);
    private final SimpleDateFormat dateFormatNoMs = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss", locale);

    @Nullable
    @Override
    public Date deserialize(@NonNull JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        String receivedFormat = json.getAsJsonPrimitive().getAsString();

        Date dt = tryParseWithFormat(receivedFormat, dateFormatFull);

        if (dt != null) {
            return dt;
        }

        dt = tryParseWithFormat(receivedFormat, dateFormatNoMs);

        if (dt != null) {
            return dt;
        }

        return tryParseWithFormat(receivedFormat, dateFormatDateOnly);
    }

    @Nullable
    private Date tryParseWithFormat(String content, @NonNull SimpleDateFormat formatter) {
        Date dt = null;
        try {
            dt = formatter.parse(content);
        } catch (Exception ignored) {
        }

        return dt;
    }

    @NonNull
    @Override
    public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(dateFormatFull.format(src));
    }
}

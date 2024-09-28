package ru.tensor.sbis.network_native.json.adapter;

import androidx.annotation.NonNull;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.util.Date;

import ru.tensor.sbis.network_native.error.exceptions.CommonSbisException;
import ru.tensor.sbis.network_native.error.exceptions.UnknownFieldTypeException;
import ru.tensor.sbis.network_native.type.FieldDescription;
import ru.tensor.sbis.network_native.type.FieldType;
import ru.tensor.sbis.network_native.type.Money;
import ru.tensor.sbis.network_native.type.ObjectId;
import ru.tensor.sbis.network_native.type.RecFormat;
import ru.tensor.sbis.network_native.type.Record;
import ru.tensor.sbis.network_native.type.RecordSet;


/**
 * Конвертер Record в Json и обратно.
 */
public class RecordJsonTypeAdapter implements JsonSerializer<Record>, JsonDeserializer<Record> {

    @NonNull
    @Override
    public JsonElement serialize(@NonNull Record src, Type typeOfSrc, @NonNull JsonSerializationContext context) {
        JsonArray d = new JsonArray();
        for (Object obj : src) {
            d.add(context.serialize(obj));
        }
        JsonElement s = context.serialize(src.getFormat());

        JsonObject record = new JsonObject();
        record.add("s", s);
        record.add("d", d);

        return record;
    }

    /**
     * Преобразовать json в Record.
     *
     * @param recFormat формат рекорда
     * @param dArray    данные рекорда в виде Json массива
     * @param context   контекст десереализатора
     * @return разобранный Record
     * @throws com.google.gson.JsonParseException В случае неверного формата Json
     */
    @NonNull
    public Record deserialize(@NonNull RecFormat recFormat, @NonNull JsonArray dArray, @NonNull JsonDeserializationContext context) throws JsonParseException {
        Record record = new Record(recFormat);
        int idx = 0;
        for (FieldDescription fd : recFormat) {
            try {
                Object content = getDataFromObject(fd.getFieldTypeString(), dArray.get(idx), context);
                //noinspection ConstantConditions
                record.setValue(fd.getFieldName(), content);
            } catch (CommonSbisException cse) {
                throw new JsonParseException(cse);
            }
            idx++;
        }

        return record;
    }

    @NonNull
    @Override
    public Record deserialize(@NonNull JsonElement json, Type typeOfT, @NonNull JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        JsonElement s = jsonObject.get("s");
        JsonElement d = jsonObject.get("d");

        if (s == null) {
            throw new JsonParseException("Невозможно преобразовать JSON в Record: ключ \"s\" отсутствует.");
        }
        if (d == null) {
            throw new JsonParseException("Невозможно преобразовать JSON в Record: ключ \"d\" отсутствует.");
        }

        RecFormat recFormat = context.deserialize(s, RecFormat.class);

        return deserialize(recFormat, d.getAsJsonArray(), context);
    }

    private Object getDataFromObject(String typeStr, @NonNull JsonElement json, @NonNull JsonDeserializationContext context) throws JsonParseException {
        FieldType fType;
        try {
            fType = FieldType.getTypeByName(typeStr);
        } catch (UnknownFieldTypeException e) {
            throw new JsonParseException("Тип \"" + typeStr + "\" не поддерживается текущей реализацией бизнес логики");
        }

        boolean isJsonNull;

        if (json.isJsonArray()) {
            JsonArray jsonArray = json.getAsJsonArray();
            isJsonNull = !(jsonArray != null && jsonArray.size() > 0) || jsonArray.get(0).equals(JsonNull.INSTANCE);
        } else {
            isJsonNull = json.equals(JsonNull.INSTANCE);
        }

        switch (fType) {
            case IDENTIFIER:
                return context.<ObjectId>deserialize(json, ObjectId.class);
            case INT:
            case LINK:
            case ENUM:
                return isJsonNull ? 0 : json.getAsLong();
            case RECORDSET:
                return context.<RecordSet>deserialize(json, RecordSet.class);
            case RECORD:
                return context.<Record>deserialize(json, Record.class);
            case STRING:
            case TEXT:
            case UUID:
                return isJsonNull ? null : json.getAsString();
            case MONEY:
                return context.<Money>deserialize(json, Money.class);
            case FLOAT:
            case DOUBLE:
                return isJsonNull ? 0 : json.getAsDouble();
            case DATE:
            case TIME:
            case DATETIME:
                return context.<Date>deserialize(json, Date.class);
            case BINARY:
            case FLAGS:
            case BOOLEAN:
                return !isJsonNull ? json.getAsBoolean() : null;
            case HIERARCHY:
            case ARRAY:
                return !isJsonNull ? json.getAsJsonArray() : null;
            default:
                throw new JsonParseException("Тип \"" + typeStr + "\" не поддерживается текущей реализацией бизнес логики");
        }
    }
}

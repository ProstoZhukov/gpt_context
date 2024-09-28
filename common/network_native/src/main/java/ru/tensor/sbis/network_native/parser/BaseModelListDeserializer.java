package ru.tensor.sbis.network_native.parser;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import ru.tensor.sbis.network_native.apiservice.api.Params;
import ru.tensor.sbis.network_native.parser.model.BaseModel;
import ru.tensor.sbis.network_native.parser.model.BaseModelList;


/**
 * Legacy-код
 * <p>
 * Created by ss.buvaylink on 17.11.2015.
 */
public class BaseModelListDeserializer implements JsonDeserializer<BaseModelList> {

    @Nullable
    @Override
    public BaseModelList deserialize(@NonNull JsonElement json, Type typeOfT, @NonNull JsonDeserializationContext context) throws JsonParseException {
        BaseModelList result = null;
        boolean isHasMore = false;
        BaseModel extendedParams = null;
        if (json.isJsonObject()) {
            JsonElement jsonElement = json.getAsJsonObject().get("result");
            if (jsonElement != null && jsonElement.isJsonObject()) {
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                isHasMore = getIsHasMore(jsonObject);
                extendedParams = getExtendedParams(jsonObject, context);
                result = parse(jsonObject, context);
            }
        }
        if (result == null) {
            result = new BaseModelList();
        }
        result.setIsHasMore(isHasMore);
        result.setExtendedParams(extendedParams);
        return result;
    }

    private boolean getIsHasMore(@NonNull JsonObject jsonObject) {
        boolean result = false;
        JsonElement isHasMoreElement = jsonObject.get("n");
        if (isHasMoreElement != null && isHasMoreElement.isJsonPrimitive()) {
            JsonPrimitive isHasMorePrimitive = isHasMoreElement.getAsJsonPrimitive();
            if (isHasMorePrimitive.isBoolean()) {
                result = isHasMorePrimitive.getAsBoolean();
            }
        }
        return result;
    }

    private BaseModel getExtendedParams(JsonObject jsonObject, JsonDeserializationContext context) {
        JsonObject paramsObject = jsonObject.getAsJsonObject("r");
        if (paramsObject != null) {
            BaseModelList paramsList = parse(paramsObject, context);
            if (paramsList != null && !paramsList.isEmpty()) {
                return paramsList.get(0);
            }
        }
        return null;
    }

    @Nullable
    private BaseModelList parse(@NonNull JsonObject json, @NonNull JsonDeserializationContext context) {
        BaseModelList result = null;
        JsonElement sElement = json.get("s");
        List<Field> fields = null;
        if (sElement != null && sElement.isJsonArray()) {
            JsonArray s = json.get("s").getAsJsonArray();
            fields = getFields(s);
        }
        JsonElement dElement = json.get("d");
        if (fields != null && dElement != null && dElement.isJsonArray()) {
            JsonArray d = json.get("d").getAsJsonArray();
            if (d.size() > 0) {
                result = new BaseModelList();
                if (checkIsSingleArrayParser(d, fields)) {
                    for (JsonElement element : d) {
                        if (element.isJsonArray()) {
                            result.add(parseRecord(element.getAsJsonArray(), fields, context));
                        }
                    }
                } else {
                    result.add(parseRecord(d, fields, context));
                }
            }
        }
        return result;
    }

    @NonNull
    private BaseModel parseRecord(@NonNull JsonArray values, @NonNull List<Field> fields, @NonNull JsonDeserializationContext context) {
        BaseModel model = new BaseModel();
        for (int i = 0; i < values.size(); i++) {
            JsonElement value = values.get(i);
            if (value != null && !value.isJsonNull()) {
                Object object = null;
                if (value.isJsonArray()) {
                    if (value.getAsJsonArray().size() > 0) {
                        object = context.deserialize(value, Object[].class);
                    }
                } else if (value.isJsonObject()) {
                    object = parse(value.getAsJsonObject(), context);
                } else {
                    object = context.deserialize(value, Object.class);
                }
                if (object != null) {
                    model.put(fields.get(i).name, object);
                }
            }
        }
        return model;
    }

    @NonNull
    private List<Field> getFields(@NonNull JsonArray array) {
        List<Field> result = new ArrayList<>();
        String name;
        JsonObject jsonObject;
        JsonElement jsonElement;
        for (JsonElement element : array) {
            if (element.isJsonObject()) {
                jsonObject = element.getAsJsonObject();
                jsonElement = jsonObject.get("n");
                if (jsonElement != null && jsonElement.isJsonPrimitive()) {
                    name = jsonElement.getAsJsonPrimitive().getAsString();
                } else {
                    name = null;
                }
                if (name != null) {
                    result.add(new Field(name, jsonObject.get("t")));
                }
            }
        }
        return result;
    }

    private static boolean checkIsSingleArrayParser(@NonNull JsonArray jsonArray, @NonNull List<Field> fields) {
        if (fields.size() > 0) {
            final JsonElement element = jsonArray.get(0);
            final JsonElement type = fields.get(0).type;
            return element != null && element.isJsonArray() && (type == null || !type.equals(Params.ARRAY_STRING));
        }
        return false;
    }

    private static class Field {

        @NonNull
        final String name;
        @Nullable
        final JsonElement type;

        public Field(@NonNull String name, @Nullable JsonElement type) {
            this.name = name;
            this.type = type;
        }
    }
}

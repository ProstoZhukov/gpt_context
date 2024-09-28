package ru.tensor.sbis.network_native.apiservice.api;

import androidx.annotation.NonNull;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Legacy-код
 * <p>
 * Параметры методов сетевых запросов
 * <p>
 * Created by ss.buvaylink on 11.11.2015.
 */
@SuppressWarnings("unused")
public class Params {

    public static final String STRING = "Строка";
    public static final String BOOLEAN = "Логическое";
    public static final String INTEGER = "Число целое";
    public static final String DOUBLE = "Число вещественное";
    public static final String MONEY = "Деньги";
    public static final String RECORD = "Запись";
    public static final String RECORDSET = "Выборка";
    public static final String IDENTIFIER = "Идентификатор";
    public static final String UUID = "UUID";
    public static final String BINARY = "Двоичное";

    public static final JsonObject ARRAY_STRING = new JsonObject();
    public static final JsonObject ARRAY_INTEGER = new JsonObject();
    public static final JsonObject ARRAY_UUID = new JsonObject();

    static {
        ARRAY_STRING.addProperty("n", "Массив");
        ARRAY_STRING.addProperty("t", "Текст");
        ARRAY_INTEGER.addProperty("n", "Массив");
        ARRAY_INTEGER.addProperty("t", "Число целое");
        ARRAY_UUID.addProperty("n", "Массив");
        ARRAY_UUID.addProperty("t", "UUID");
    }

    @NonNull
    @SerializedName("s")
    private final List<Param> mTypes;
    @NonNull
    @SerializedName("d")
    private final List<Object> mValues;

    public Params() {
        mTypes = new ArrayList<>();
        mValues = new ArrayList<>();
    }

    public void addNew(String name, Object type, Object value) {
        addType(name, type);
        addValue(value);
    }

    public void addType(String name, Object type) {
        mTypes.add(new Param(name, type));
    }

    public void addValue(Object value) {
        mValues.add(value);
    }

    @NonNull
    public List<Object> getValues() {
        return mValues;
    }

    @NonNull
    public List<Param> getTypes() {
        return mTypes;
    }

    @SuppressWarnings("FieldCanBeLocal")
    public static class Param {

        @SerializedName("t")
        private final Object mType;
        @SerializedName("n")
        private final String mName;

        public Param(String name, Object type) {
            mName = name;
            mType = type;
        }
    }

    @NonNull
    public static Map<String, Object> methodParams(Params filter, Params navigation) {
        Map<String, Object> methodParams = new HashMap<>();
        methodParams.put("ДопПоля", new Object[]{});
        methodParams.put("Фильтр", filter);
        methodParams.put("Сортировка", null);
        methodParams.put("Навигация", navigation);
        return methodParams;
    }

    @NonNull
    public static Map<String, Object> methodParams(Params filter, Params navigation, Params sorting) {
        Map<String, Object> methodParams = new HashMap<>();
        methodParams.put("ДопПоля", new Object[]{});
        methodParams.put("Фильтр", filter);
        methodParams.put("Сортировка", new SortingParams(sorting));
        methodParams.put("Навигация", navigation);
        return methodParams;
    }

    @NonNull
    public static Map<String, Object> methodParams2(Params filter) {
        Map<String, Object> methodParams = new HashMap<>();
        methodParams.put("Filter", filter);
        return methodParams;
    }

    @NonNull
    public static Params navigation(int page, int pageSize) {
        Params result = new Params();
        result.addNew("Страница", Params.INTEGER, page);
        result.addNew("РазмерСтраницы", Params.INTEGER, pageSize);
        result.addNew("ЕстьЕще", Params.BOOLEAN, true);
        return result;
    }

}

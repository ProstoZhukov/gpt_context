package ru.tensor.sbis.network_native.json;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Date;

import ru.tensor.sbis.network_native.error.SbisError;
import ru.tensor.sbis.network_native.json.adapter.DateTimeJsonTypeAdapter;
import ru.tensor.sbis.network_native.json.adapter.MethodListResultJsonTypeAdapter;
import ru.tensor.sbis.network_native.json.adapter.MoneyJsonTypeAdapter;
import ru.tensor.sbis.network_native.json.adapter.NavigationJsonTypeAdapter;
import ru.tensor.sbis.network_native.json.adapter.ObjectIdJsonTypeAdapter;
import ru.tensor.sbis.network_native.json.adapter.RecFormatJsonTypeAdapter;
import ru.tensor.sbis.network_native.json.adapter.RecordJsonTypeAdapter;
import ru.tensor.sbis.network_native.json.adapter.RecordSetJsonTypeAdapter;
import ru.tensor.sbis.network_native.json.adapter.SbisErrorJsonTypeAdapter;
import ru.tensor.sbis.network_native.type.MethodListResult;
import ru.tensor.sbis.network_native.type.Money;
import ru.tensor.sbis.network_native.type.Navigation;
import ru.tensor.sbis.network_native.type.ObjectId;
import ru.tensor.sbis.network_native.type.RecFormat;
import ru.tensor.sbis.network_native.type.Record;
import ru.tensor.sbis.network_native.type.RecordSet;


/**
 * Помощник для работы с Gson. Выступает в роли синглтона, который проинициализирован нужным нам образом.
 */
public class GsonHelper {

    // Экземпляр объекта с настроенными сериализаторами
    private static final Gson instance = new GsonBuilder()
            .serializeNulls()
            .registerTypeAdapter(Navigation.class, new NavigationJsonTypeAdapter())
            .registerTypeAdapter(RecFormat.class, new RecFormatJsonTypeAdapter())
            .registerTypeAdapter(ObjectId.class, new ObjectIdJsonTypeAdapter())
            .registerTypeAdapter(Record.class, new RecordJsonTypeAdapter())
            .registerTypeAdapter(RecordSet.class, new RecordSetJsonTypeAdapter())
            .registerTypeAdapter(MethodListResult.class, new MethodListResultJsonTypeAdapter())
            .registerTypeAdapter(Money.class, new MoneyJsonTypeAdapter())
            .registerTypeAdapter(Date.class, new DateTimeJsonTypeAdapter())
            .registerTypeAdapter(SbisError.class, new SbisErrorJsonTypeAdapter())
            .create();

    private GsonHelper() {
    }

    /**
     * Получить экземпляр Gson.
     *
     * @return gson объект
     */
    @NonNull
    public static Gson getInstance() {
        return instance;
    }
}
package ru.tensor.sbis.network_native.json.adapter;

import androidx.annotation.NonNull;

import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

import ru.tensor.sbis.network_native.error.exceptions.DuplicateKeyException;
import ru.tensor.sbis.network_native.type.Navigation;
import ru.tensor.sbis.network_native.type.Record;
import timber.log.Timber;

/**
 * Конвертер Navigation в Json объект.
 */
public class NavigationJsonTypeAdapter implements JsonSerializer<Navigation> {

    @Override
    public JsonElement serialize(@NonNull Navigation src, Type typeOfSrc, @NonNull JsonSerializationContext context) {
        Record navigationRecord = new Record();
        try {
            navigationRecord.append("Страница", src.getPage());
            navigationRecord.append("РазмерСтраницы", src.getPageSize());
            navigationRecord.append("ЕстьЕще", src.isHasMore());
        } catch (DuplicateKeyException e) {
            Timber.e(e);
        }

        return context.serialize(navigationRecord);
    }
}

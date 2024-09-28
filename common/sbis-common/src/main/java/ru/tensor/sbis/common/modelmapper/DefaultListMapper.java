package ru.tensor.sbis.common.modelmapper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;

/**
 * Абстрактный класс для конвертирования списка моделей.
 *
 * @param <T> - тип до конвертации
 * @param <M> - тип после конвертации
 *
 * @author am.boldinov
 */
public abstract class DefaultListMapper<T, M> implements Function<List<T>, List<M>> {

    @Nullable
    protected abstract M map(@NonNull T source) throws Exception;

    @Override
    public List<M> apply(@NonNull List<T> source) throws Exception {
        List<M> result = new ArrayList<>(source.size());
        if (!source.isEmpty()) {
            for (T item : source) {
                M mapped = map(item);
                if (mapped != null) {
                    result.add(mapped);
                }
            }
        }
        return result;
    }
}

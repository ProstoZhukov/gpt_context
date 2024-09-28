package ru.tensor.sbis.common.modelmapper;

import androidx.annotation.Nullable;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;

/**
 * Класс для конвертации списка моделей с использованием стороннего маппера элементов.
 * @param <T> - тип до конвертации
 * @param <M> - тип после конвертации
 *
 * @author am.boldinov
 */
public class DelegateListMapper<T, M> extends DefaultListMapper<T, M> {

    @NonNull
    private final Function<? super T, ? extends M> mItemMapper;

    public DelegateListMapper(@NonNull Function<? super T, ? extends M> itemMapper) {
        mItemMapper = itemMapper;
    }

    @Nullable
    @Override
    protected M map(@NonNull T source) throws Exception {
        return mItemMapper.apply(source);
    }

}

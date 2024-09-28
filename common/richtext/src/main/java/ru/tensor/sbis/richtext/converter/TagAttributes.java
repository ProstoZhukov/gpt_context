package ru.tensor.sbis.richtext.converter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Атрибуты тега
 *
 * @author am.boldinov
 */
public interface TagAttributes {

    /**
     * Возвращает название тега
     */
    @NonNull
    String getTag();

    /**
     * Возвращает значение атрибута
     *
     * @param attr атрибут
     */
    @Nullable
    String getValue(@NonNull String attr);

    /**
     * @return true если атрибуты пусты
     */
    boolean isEmpty();

    /**
     * Возвращает атрибуты родителя в DOM дереве пока текущий тег открыт на чтение,
     * после закрытия тега родитель уже не будет доступен и метод вернет null.
     */
    @Nullable
    TagAttributes getParent();

}

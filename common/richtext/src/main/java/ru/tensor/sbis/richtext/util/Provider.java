package ru.tensor.sbis.richtext.util;

import androidx.annotation.NonNull;

/**
 * Поставщик компонентов и зависимостей.
 *
 * @author am.boldinov
 */
public interface Provider<T> {

    /**
     * @SelfDocumented
     */
    @NonNull
    T get();
}

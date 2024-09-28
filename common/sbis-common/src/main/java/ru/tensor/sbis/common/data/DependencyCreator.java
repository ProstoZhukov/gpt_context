package ru.tensor.sbis.common.data;

import androidx.annotation.NonNull;

/**
 * @author am.boldinov
 */
public interface DependencyCreator<T> {
    @NonNull
    T create();
}

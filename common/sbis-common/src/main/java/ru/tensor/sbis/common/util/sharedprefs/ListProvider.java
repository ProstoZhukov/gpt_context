package ru.tensor.sbis.common.util.sharedprefs;

import androidx.annotation.Nullable;

import java.util.List;

/**
 * Interface for list provider.
 * @param <T>
 *
 * @author am.boldinov
 */
public interface ListProvider<T> {

    /**
     * Get list.
     * @return
     */
    @Nullable List<T> get();

    /**
     * Set list
     * @param list
     */
    void set(@Nullable List<T> list);

}

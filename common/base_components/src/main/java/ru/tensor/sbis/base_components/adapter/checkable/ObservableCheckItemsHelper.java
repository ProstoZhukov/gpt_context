package ru.tensor.sbis.base_components.adapter.checkable;

import java.util.HashSet;

import androidx.annotation.NonNull;
import io.reactivex.Observable;

/**
 * CheckHelper's extension to observe checked count changes and get checked items.
 * @param <T> - type of items.
 *
 * @author am.boldinov
 */
public interface ObservableCheckItemsHelper<T> extends CheckHelper<T> {

    /**
     * @return observable for checked count changing.
     */
    @NonNull
    Observable<HashSet<T>> getCheckedItemsObservable();

    /**
     * @return {@link HashSet} with checked items.
     */
    @NonNull
    HashSet<T> getCheckedItems();

}

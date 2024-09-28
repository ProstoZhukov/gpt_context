package ru.tensor.sbis.base_components.adapter.checkable;

import androidx.annotation.NonNull;
import io.reactivex.Observable;

/**
 * Check helper extension to observe checked count change.
 * @param <T> - type of items
 *
 * @author am.boldinov
 */
public interface ObservableCheckCountHelper<T> extends CheckHelper<T> {

    /**
     * Returns observable for checked count changing.
     * @return observable for checked count changing
     */
    @NonNull
    Observable<Integer> getCheckedCountObservable();

}

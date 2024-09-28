package ru.tensor.sbis.base_components.adapter.checkable;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import io.reactivex.Observable;

/**
 * Implements business logic of list checking.
 *
 * @param <T> - type of items in list
 * @author am.boldinov
 */
public interface CheckHelper<T> {

    /**
     * Attaches to adapter.
     *
     * @param adapter - adapter for attaching
     */
    void attachToAdapter(@NonNull CheckableListAdapter<T> adapter);

    /**
     * Detach current adapter from helper.
     */
    void detachFromAdapter();

    /**
     * Mark specified item as checked/unchecked.
     *
     * @param item    - item to change state
     * @param checked - state
     */
    void setChecked(@NonNull T item, boolean checked);

    /**
     * Пометить выбранными переданные элементы и снять отметки со всех остальных
     *
     * @param items - список элементов для выделения
     */
    @SuppressWarnings("unused")
    void setCheckedTheseAndUncheckedOther(@NonNull ArrayList<T> items);

    /**
     * Clear checks from all items.
     */
    void clearChecks();

    /**
     * Check all presented items.
     */
    void checkAll();

    /**
     * Invert check for all presented items.
     */
    void invertAll();

    /**
     * Returns true if item is checked, else otherwise.
     *
     * @param item - item to check state
     */
    boolean isChecked(@NonNull T item);

    /**
     * Returns count of checked items.
     *
     * @return count of checked items
     */
    int getCheckedCount();

    /**
     * Returns list of checked items.
     *
     * @return list of checked items
     */
    @NonNull
    List<T> getChecked();

    /**
     * Returns list of unchecked items.
     *
     * @return list of unchecked items
     */
    @NonNull
    List<T> getUnchecked();

    /**
     * Calls when content of list was changed.
     */
    void onContentChanged();

    /**
     * Clear checks that not presented in list already.
     */
    void clearNotPresentedChecks();

    /** SelfDocumented */
    void disableCheckMode();

    /** SelfDocumented */
    void enableCheckMode();

    /** SelfDocumented */
    boolean isCheckModeEnabled();

    /** SelfDocumented */
    Observable<Boolean> getCheckModeEnabledObservable();

}

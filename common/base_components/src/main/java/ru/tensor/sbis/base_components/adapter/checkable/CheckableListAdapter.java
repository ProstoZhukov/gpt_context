package ru.tensor.sbis.base_components.adapter.checkable;

import java.util.List;

import androidx.annotation.NonNull;

/**
 * Interface for list adapter with checkable option.
 * Using by check helper to provide view callbacks on check events.
 * @param <T> - type of items
 *
 * @author am.boldinov
 */
public interface CheckableListAdapter<T> {

    void attachHelper(ru.tensor.sbis.base_components.adapter.checkable.CheckHelper<T> helper);

    void detachHelper(CheckHelper<T> helper);

    /**
     * Returns content for helper to build checked/unchecked lists.
     * @return content
     */
    @NonNull
    List<T> getContent();

    /**
     * Returns list of checked items.
     * @return list of checked items
     */
    List<T> getChecked();

    /**
     * Returns list of unchecked items.
     * @return list of unchecked items
     */
    List<T> getUnchecked();

    /**
     * Calls when check state of specified item was changed.
     * @param item      - item
     * @param checked   - check-state
     */
    void onChecked(@NonNull T item, boolean checked);

    /**
     * Calls when all checks was cleared.
     */
    void onClearChecks();

    /**
     * Calls when all items was checked.
     */
    void onCheckAll();

    /**
     * Calls when all items checks was inverted.
     */
    void onInvertCheckAll();

    /** SelfDocumented */
    void showCheckMode();

    /** SelfDocumented */
    void hideCheckMode();

}

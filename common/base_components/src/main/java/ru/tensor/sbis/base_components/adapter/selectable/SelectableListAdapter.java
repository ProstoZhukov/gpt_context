package ru.tensor.sbis.base_components.adapter.selectable;

import androidx.annotation.NonNull;

/**
 * Interface for adapter working with {@link ru.tensor.sbis.base_components.adapter.selectable.SelectionHelper}.
 * @param <T> - list item type.
 */
public interface SelectableListAdapter<T> {

    /**
     * Attaches specified {@link ru.tensor.sbis.base_components.adapter.selectable.SelectionHelper} to current {@link SelectableListAdapter}.
     *
     * @param selectionHelper - {@link ru.tensor.sbis.base_components.adapter.selectable.SelectionHelper} for attaching.
     */
    void attachSelectionHelper(@NonNull ru.tensor.sbis.base_components.adapter.selectable.SelectionHelper<T> selectionHelper);

    /**
     * Detaches specified {@link ru.tensor.sbis.base_components.adapter.selectable.SelectionHelper} from current {@link SelectableListAdapter}.
     *
     * @param selectionHelper - {@link ru.tensor.sbis.base_components.adapter.selectable.SelectionHelper} for detaching.
     */
    void detachSelectionHelper(@NonNull ru.tensor.sbis.base_components.adapter.selectable.SelectionHelper<T> selectionHelper);

    /**
     * Intended in order to {@link ru.tensor.sbis.base_components.adapter.selectable.SelectionHelper} can notify {@link SelectableListAdapter}
     * if item list selection was changed.
     *
     * @param newSelectedItem      - new selected item. Can't be null. Should be equal to value
     *                             from {@link #provideStubItem()} method if
     *                             {@link SelectionHelper#resetSelection()} method was called.
     * @param previousSelectedItem - previous selected item or null. Can't be null. If there
     *                             is not previous selection then use value from
     *                             {@link #provideStubItem()} method.
     */
    void onItemSelected(@NonNull T newSelectedItem, @NonNull T previousSelectedItem);

    /**
     * Provides stub item for resetting list selection state.
     *
     * @return - item that will be indicate no selection state. Can't be null.
     */
    @NonNull
    T provideStubItem();

}

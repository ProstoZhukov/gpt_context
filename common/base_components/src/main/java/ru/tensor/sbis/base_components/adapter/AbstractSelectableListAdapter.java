package ru.tensor.sbis.base_components.adapter;

import android.os.Bundle;

import java.util.List;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.UiThread;
import androidx.recyclerview.widget.RecyclerView;
import ru.tensor.sbis.base_components.adapter.selectable.SelectableListAdapter;
import ru.tensor.sbis.base_components.adapter.selectable.SelectableViewHolder;
import ru.tensor.sbis.base_components.adapter.selectable.SelectionHelper;

/**
 * Legacy-код
 * @param <T>
 * @param <VH>
 */
@UiThread
public abstract class AbstractSelectableListAdapter<T, VH extends RecyclerView.ViewHolder & SelectableViewHolder>
        extends AbstractListAdapter<T, VH>
        implements SelectableListAdapter<T> {

    /**
     * Constant, signals that position not determined.
     */
    protected static final int NO_POSITION = -1;
    private static final String PAYLOAD_SELECTED = "payload_selected";
    private static final String PAYLOAD_NOT_SELECTED = "payload_not_selected";

    /**
     * Helper that stores last selection list item. It notifies all its subscribers about
     * new selection and attached adapter that implements {@link SelectableListAdapter} interface.
     */
    protected SelectionHelper<T> mSelectionHelper;


    public void onSavedInstanceState(@NonNull Bundle outState) {

    }

    public void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {

    }

    //region Support section

    /**
     * Override this method and return false if you don't want to use logic from adapter.
     * See {@link SelectionHelper} and {@link SelectableListAdapter} for more details.
     */
    @SuppressWarnings("unused")
    protected boolean needSaveStateInAdapter() {
        return true;
    }

    protected boolean isItemSelectable(int holderType) {
        return true;
    }
    //endregion Support section

    //region Binding
    @CallSuper
    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        bindSelectionState(holder, position);
    }

    @CallSuper
    @Override
    public void onBindViewHolder(@NonNull VH holder, int position, @NonNull List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
        if (payloads.contains(PAYLOAD_SELECTED) && mSelectionHelper.isTablet()) {
            holder.updateSelectionState(true);
        } else if (payloads.contains(PAYLOAD_NOT_SELECTED)) {
            holder.updateSelectionState(false);
        }
    }

    // Override if you need to change selection state binding
    protected void bindSelectionState(@NonNull VH holder, int position) {
        if (mSelectionHelper != null && isItemSelectable(holder.getItemViewType())) {
            // make selection if needed only for tablets
            holder.updateSelectionState(
                    mSelectionHelper.isTablet() &&
                            isMatching(getItem(position), mSelectionHelper.getSelectedItem())
            );
        }
    }
    //endregion Binding

    //region SelectableListAdapter interface implementation
    @Override
    public void attachSelectionHelper(@NonNull SelectionHelper<T> selectionHelper) {
        mSelectionHelper = selectionHelper;
    }

    @Override
    public void detachSelectionHelper(@NonNull SelectionHelper<T> selectionHelper) {
        mSelectionHelper = null;
    }

    @Override
    public void onItemSelected(@NonNull T newSelectedItem, @NonNull T previousSelectedItem) {
        int newSelectedPosition = getPositionForItem(newSelectedItem);
        int previousSelectedPosition = getPositionForItem(previousSelectedItem);

        if (newSelectedPosition != NO_POSITION) {
            notifyItemChanged(newSelectedPosition, PAYLOAD_SELECTED);
        }
        if (previousSelectedPosition != NO_POSITION) {
            notifyItemChanged(previousSelectedPosition, PAYLOAD_NOT_SELECTED);
        }
    }

    @Override
    @NonNull
    public T provideStubItem() {
        /*
            Override this method and provide stub item to use SelectionHelper!
            It implemented here with null return value in order to
            escape of explicitly implementation it in child adapters
            that doesn't use SelectionHelper.
        */
        //noinspection ConstantConditions
        return null;
    }
    //endregion SelectableListAdapter interface implementation

    //region Base logic

    /**
     * Used to find position of element in list.
     *
     * @param src  - element to find
     * @param item - item from list
     * @return true if src equals to item, false otherwise
     */
    protected boolean isMatching(@NonNull T src, @NonNull T item) {
        // override to use
        return false;
    }

    /**
     * Get position for specified item.
     */
    protected int getPositionForItem(@NonNull T item) {
        List<T> content = getContent();
        final int size = content.size();
        for (int pos = 0; pos < size; pos++) {
            if (isMatching(item, content.get(pos))) {
                return pos;
            }
        }
        return NO_POSITION;
    }
    //endregion Base logic

}

package ru.tensor.sbis.base_components.adapter.selectable;

/** SelfDocumented */
public interface SelectableViewHolder {

    /**
     * Unified method for updating ViewHolder selection state.
     * Base implementation is contained in
     * {@link ru.tensor.sbis.base_components.adapter.AbstractSelectableListAdapter},
     * override if needed.
     */
    void updateSelectionState(boolean selected);

}

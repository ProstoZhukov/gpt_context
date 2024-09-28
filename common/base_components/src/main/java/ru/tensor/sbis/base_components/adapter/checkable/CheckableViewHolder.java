package ru.tensor.sbis.base_components.adapter.checkable;

/**
 * @author am.boldinov
 */
public interface CheckableViewHolder {

    /**
     * Update check-state
     * @param checked - check-state
     * @param animate - animate changes
     */
    @SuppressWarnings("unused")
    void updateCheckState(boolean checked, boolean animate);

}

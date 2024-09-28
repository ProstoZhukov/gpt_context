package ru.tensor.sbis.mvp.multiselection;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Collection;

import ru.tensor.sbis.mvp.multiselection.data.MultiSelectionItem;

/**
 * Legacy-код
 *
 * @deprecated Устаревший подход, переходим на mvi
 */
@Deprecated
@SuppressWarnings("JavaDoc")
public interface MultiSelectedItemsPanel {

    /**
     * @SelfDocumented
     */
    void setOnArrowsClickListener(@Nullable OnArrowsClickListener onArrowsClickListener);

    /**
     * @SelfDocumented
     */
    void addItem(@NonNull MultiSelectionItem item);

    /**
     * @SelfDocumented
     */
    void removeItem(@NonNull MultiSelectionItem item);

    /**
     * @SelfDocumented
     */
    void setItems(@NonNull Collection<MultiSelectionItem> contacts);

    /**
     * Method for displaying view state of panel. There are two states - expanded and roll upped.
     *
     * @param isExpanded boolean flag for changing view state of panel
     */
    void setupToggle(boolean isExpanded);

    /**
     * Method for enabling/disabling roll up arrow.
     *
     * @param isEnabled boolean flag for changing visibility of roll up arrow
     */
    void enableRollUpArrow(boolean isEnabled);

    /**
     * @SelfDocumented
     */
    interface OnArrowsClickListener {

        /**
         * @SelfDocumented
         */
        void onExpandArrowClick();

        /**
         * @SelfDocumented
         */
        void onRollUpArrowClick();

    }

}

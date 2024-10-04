package ru.tensor.sbis.design.view_ext.swipereveallayout;

import androidx.annotation.NonNull;

import ru.tensor.sbis.design.view_ext.swipereveallayout.updated.UpdatedSwipeHolderInterface;

/**
 * Use {@link UpdatedSwipeHolderInterface} instead.
 */

@SuppressWarnings("deprecation")
@Deprecated
public interface SwipeHolderInterface {
    @NonNull
    SwipeRevealLayout getSwipeRevealLayout();

    int getAdapterPosition();

    @SuppressWarnings("unused")
    void checkState(@NonNull ViewBinderHelper viewBinderHelper, boolean lock);
}

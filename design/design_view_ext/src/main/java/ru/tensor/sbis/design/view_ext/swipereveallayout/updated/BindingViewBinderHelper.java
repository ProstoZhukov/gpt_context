package ru.tensor.sbis.design.view_ext.swipereveallayout.updated;

import java.io.Serializable;

/**
 * Вспомогательный класс для управления состоянием свайпа элементов.
 */
@SuppressWarnings("deprecation")
public class BindingViewBinderHelper<T extends Serializable> extends UpdatedViewBinderHelper<T> {

    @SuppressWarnings("unused")
    @Override
    public void bind(UpdatedSwipeRevealLayout swipeLayout, T uuid) {
        boolean oldLockDragState = swipeLayout.isDragLocked();
        super.bind(swipeLayout, uuid);
        swipeLayout.setLockDrag(oldLockDragState);
    }
}

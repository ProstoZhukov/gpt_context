package ru.tensor.sbis.swipeablelayout.util

import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.design.utils.extentions.doOnDetachedFromWindow
import ru.tensor.sbis.swipeable_layout.R
import ru.tensor.sbis.swipeablelayout.SwipeableLayout
import ru.tensor.sbis.swipeablelayout.util.swipestate.SwipeListVm

/**
 * Отслеживает изменения в адаптере, связанные с изменением или удалением элементов.
 *
 * @author us.bessonov
 */
private class SwipeListAdapterChangesObserver(
    private val recyclerView: RecyclerView
) : RecyclerView.AdapterDataObserver() {

    override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
        getSwipeListVm(recyclerView)?.onItemsChangedOrRemoved()
    }

    override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {
        getSwipeListVm(recyclerView)?.onItemsChangedOrRemoved()
    }

    override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
        getSwipeListVm(recyclerView)?.onItemsChangedOrRemoved()
    }

    override fun onChanged() {
        getSwipeListVm(recyclerView)?.onItemsChangedOrRemoved()
    }
}

/**
 * Установить слушатель (общий для списка [SwipeableLayout]), который отслеживает изменения в адаптере списка и
 * уведомляет о событиях [SwipeListVm].
 */
internal fun setSwipeListAdapterChangesObserver(recyclerView: RecyclerView, cleanUpOnRecyclerDetached: () -> Unit) {
    val key = R.id.swipeable_layout_is_swipe_list_adapter_observer_set
    if (recyclerView.getTag(key) != null) return
    recyclerView.setTag(key, true)
    recyclerView.adapter?.let { adapter ->
        val observer = SwipeListAdapterChangesObserver(recyclerView)
        adapter.registerAdapterDataObserver(observer)
        recyclerView.doOnDetachedFromWindow {
            adapter.unregisterAdapterDataObserver(observer)
            cleanUpOnRecyclerDetached()
        }
    }
}
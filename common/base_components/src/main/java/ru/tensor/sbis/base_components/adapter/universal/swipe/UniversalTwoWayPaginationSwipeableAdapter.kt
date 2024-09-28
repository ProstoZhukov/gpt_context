package ru.tensor.sbis.base_components.adapter.universal.swipe

import android.os.Bundle
import androidx.annotation.CallSuper
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.base_components.adapter.AbstractViewHolder
import ru.tensor.sbis.base_components.adapter.universal.UniversalBindingItem
import ru.tensor.sbis.base_components.adapter.universal.UniversalTwoWayPaginationAdapter
import ru.tensor.sbis.swipeablelayout.util.SwipeHelper

/**
 * Базовый адаптер для работы со свайпом на элементах списка
 *
 * @author am.boldinov
 */
abstract class UniversalTwoWayPaginationSwipeableAdapter<DM : UniversalBindingItem>(
    private val swipeHandler: ItemSwipeHandler
) : UniversalTwoWayPaginationAdapter<DM>() {

    private var stateSaved = false

    private val swipeableListener = object : UniversalSwipeableHolder.SwipeableListener {
        override fun onSwipeDismissed(uuid: String, bySwipe: Boolean) {
            ForceRemoveItemRunnable(uuid).run()
        }

        override fun invoke(uuid: String): Boolean {
            return content.find { it.itemTypeId == uuid } != null
        }
    }

    override fun onSavedInstanceState(outState: Bundle) {
        super.onSavedInstanceState(outState)
        stateSaved = true
    }

    override fun onBindViewHolder(holder: AbstractViewHolder<DM>, position: Int) {
        if (isSwipeableViewHolder(holder.itemViewType)) {
            bindSwipeHolder(holder as UniversalSwipeableHolder)
        }
        super.onBindViewHolder(holder, position)
    }

    override fun onViewDetachedFromWindow(holder: AbstractViewHolder<DM>) {
        super.onViewDetachedFromWindow(holder)
        if (!stateSaved && isSwipeableViewHolder(holder.itemViewType) && (holder as UniversalSwipeableHolder).isSwipeOpened()) {
            holder.closeSwipeIfNeed()
        }
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        swipeDismissImmediately()
    }

    override fun setContent(newContent: MutableList<DM>?, notifyDataSetChanged: Boolean) {
        if (newContent !is UniversalDismissibleItemList) {
            swipeDismissImmediately()
        }
        super.setContent(newContent, notifyDataSetChanged)
    }

    /**
     * Сбрасывает состояние свайпов и операций по ним
     */
    fun resetAllSwipeStates() {
        swipeDismissImmediately()
        closeAllSwipePanels()
    }

    /**
     * Закрывает все свайпы
     */
    fun closeAllSwipePanels() {
        SwipeHelper.resetAll(animated = false, withDismissedWithTimeout = false)
    }

    @CallSuper
    protected open fun isSwipeableViewHolder(holderViewType: Int): Boolean {
        return holderViewType != HOLDER_BOTTOM_PADDING && holderViewType != HOLDER_EMPTY && holderViewType != HOLDER_PROGRESS
    }

    private fun bindSwipeHolder(holder: UniversalSwipeableHolder<*>) {
        holder.attachListener(swipeableListener)
    }

    private fun swipeDismissImmediately() {
        SwipeHelper.forceDismissItemsWithTimeout()
    }

    private inner class ForceRemoveItemRunnable(
        private val uuid: String
    ) : Runnable {

        override fun run() {
            if (mRecyclerView?.isComputingLayout == true) {
                mRecyclerView!!.post(this)
                return
            }
            val position = content.indexOfFirst { it.itemTypeId == uuid }
            if (position >= 0) {
                content.removeAt(position)
                notifyItemRemoved(mOffset + position)
            }
            swipeHandler.onItemRemoved(uuid)
        }
    }
}
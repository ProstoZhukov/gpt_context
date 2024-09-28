package ru.tensor.sbis.mvp.layoutmanager

import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager

/**
 * Наблюдатель за положением скролла списка с пагинацией.
 * Необходимо использовать в связке с [LayoutManager] для делегирования событий.
 *
 * @author am.boldinov
 */
@Deprecated("Устаревший подход, переходим на mvi_extension")
interface PaginationScrollWatcher {

    /**
     * @see [LayoutManager.onAttachedToWindow]
     */
    fun onAttachedToRecyclerView(view: RecyclerView?)

    /**
     * @see [LayoutManager.onDetachedFromWindow]
     */
    fun onDetachedFromRecyclerView(view: RecyclerView?)

    /**
     * @see [LayoutManager.onItemsChanged]
     */
    fun onItemsChanged()

    /**
     * @see [LayoutManager.onItemsRemoved]
     */
    fun onItemsRemoved(positionStart: Int, itemCount: Int)

    /**
     * @see [LayoutManager.onItemsUpdated]
     */
    fun onItemsUpdated(positionStart: Int, itemCount: Int)

    /**
     * @see [LayoutManager.onItemsAdded]
     */
    fun onItemsAdded(positionStart: Int, itemCount: Int)

    /**
     * @see [LayoutManager.scrollVerticallyBy]
     */
    fun preventOverScroll(dy: Int, recycler: RecyclerView.Recycler, state: RecyclerView.State): Boolean

}

@Deprecated("Устаревший подход, переходим на mvi_extension")
interface ScrollToTopMediator {

    /**
     * Проверить, доступен ли ScrollToTop. К примеру
     * на планшетах может быть недоступен.
     */
    fun isAvailable(view: RecyclerView): Boolean

    /**
     * Отключить скролл в самый верх.
     */
    fun disableScrollToTop()

    /**
     * Включить скролл в самый верх.
     */
    fun enableScrollToTop()
}
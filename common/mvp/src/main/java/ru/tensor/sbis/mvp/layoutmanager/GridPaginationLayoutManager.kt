package ru.tensor.sbis.mvp.layoutmanager

import android.content.Context
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.base_components.adapter.BaseTwoWayPaginationAdapter.HOLDER_BOTTOM_PADDING
import ru.tensor.sbis.base_components.adapter.BaseTwoWayPaginationAdapter.HOLDER_EMPTY
import ru.tensor.sbis.base_components.adapter.BaseTwoWayPaginationAdapter.HOLDER_PROGRESS
import ru.tensor.sbis.common.util.CommonUtils
import ru.tensor.sbis.common.util.scroll.ScrollHelper
import timber.log.Timber

/**
 * Реализация [GridLayoutManager] с поддержкой постраничной загрузки.
 * Предотвращает лишний скролл при двусторонней пагинации.
 *
 * @author am.boldinov
 */
@Deprecated("Устаревший подход, переходим на mvi_extension")
open class GridPaginationLayoutManager(
    context: Context,
    spanCount: Int,
    private val scrollHelper: ScrollHelper? = null,
    private val progressViewType: Int = HOLDER_PROGRESS,
    private val emptyViewType: Int = HOLDER_EMPTY,
    private val bottomPaddingViewType: Int = HOLDER_BOTTOM_PADDING
) : GridLayoutManager(context, spanCount) {

    private val scrollWatcher by lazy(LazyThreadSafetyMode.NONE) {
        PaginationLinearScrollWatcher(
            this,
            scrollHelper,
            progressViewType,
            emptyViewType,
            bottomPaddingViewType
        )
    }

    override fun onAttachedToWindow(view: RecyclerView?) {
        super.onAttachedToWindow(view)
        scrollWatcher.onAttachedToRecyclerView(view)
    }

    override fun onDetachedFromWindow(
        view: RecyclerView?,
        recycler: RecyclerView.Recycler?
    ) {
        super.onDetachedFromWindow(view, recycler)
        scrollWatcher.onDetachedFromRecyclerView(view)
    }

    override fun scrollVerticallyBy(
        dy: Int,
        recycler: RecyclerView.Recycler,
        state: RecyclerView.State
    ): Int {
        if (scrollWatcher.preventOverScroll(dy, recycler, state)) {
            return 0
        }
        return try {
            super.scrollVerticallyBy(dy, recycler, state)
        } catch (e: Exception) {
            CommonUtils.handleException(e)
            0
        }
    }

    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State?) {
        try {
            super.onLayoutChildren(recycler, state)
        } catch (e: IndexOutOfBoundsException) {
            Timber.e(e)
            recycler?.clear()
        }
    }

    override fun onItemsChanged(recyclerView: RecyclerView) {
        super.onItemsChanged(recyclerView)
        scrollWatcher.onItemsChanged()
    }

    override fun onItemsRemoved(recyclerView: RecyclerView, positionStart: Int, itemCount: Int) {
        super.onItemsRemoved(recyclerView, positionStart, itemCount)
        scrollWatcher.onItemsRemoved(positionStart, itemCount)
    }

    override fun onItemsUpdated(recyclerView: RecyclerView, positionStart: Int, itemCount: Int) {
        super.onItemsUpdated(recyclerView, positionStart, itemCount)
        scrollWatcher.onItemsUpdated(positionStart, itemCount)
    }

    override fun onItemsAdded(recyclerView: RecyclerView, positionStart: Int, itemCount: Int) {
        super.onItemsAdded(recyclerView, positionStart, itemCount)
        scrollWatcher.onItemsAdded(positionStart, itemCount)
    }

    override fun supportsPredictiveItemAnimations() = false
}
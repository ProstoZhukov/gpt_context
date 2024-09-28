package ru.tensor.sbis.mvp.layoutmanager.sticky

import android.content.Context
import android.graphics.Rect
import android.util.TypedValue.COMPLEX_UNIT_DIP
import android.util.TypedValue.applyDimension
import android.view.View
import androidx.annotation.Px
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.common.util.scroll.ScrollHelper
import ru.tensor.sbis.mvp.layoutmanager.PaginationLayoutManager

private const val BOTTOM_ITEM_THRESHOLD = 10

/**
 * LayoutManager, поддерживающий поведение sticky заголовков
 *
 * @property stickyHeaderView           Вьюшка sticky заголовка
 * @property stickyHeaderInfoProvider   Поставщик информации sticky заголовка
 * @property stickyHeaderUpdater        Объект, обновляющий sticky заголовок
 *
 * @author sa.nikitin
 */
@Deprecated("Устаревший подход, переходим на mvi_extension")
@Suppress("MemberVisibilityCanBePrivate")
open class StickyHeaderLayoutManager<I : StickyHeaderInfo> @JvmOverloads constructor(
    context: Context,
    var stickyHeaderView: View,
    var stickyHeaderInfoProvider: StickyHeaderInfoProvider<I>,
    var stickyHeaderUpdater: StickyHeaderUpdater<I>,
    scrollHelper: ScrollHelper? = null
) : PaginationLayoutManager(context, scrollHelper) {

    interface LaidOutItemsListener {

        fun onFirstItemShownStateChanged(shown: Boolean, atBottomOfItem: Boolean)

        fun onItemsLaidOut(topMostItemPosition: Int, bottomMostItemPosition: Int)
    }

    /** @SelfDocumented */
    var currentTopOffset: Int = 0

    /** @SelfDocumented */
    var laidOutItemsListener: LaidOutItemsListener? = null

    /** @SelfDocumented */
    @Px
    var stickyHeaderPositionError: Int = applyDimension(COMPLEX_UNIT_DIP, 1f, context.resources.displayMetrics).toInt()

    // for delayed start of animation
    private val rect: Rect = Rect()

    private var hasPadding: Boolean = false
    private var stickyHeaderTop: Int = 0
    private var stickingEnabled = true

    /**
     * Включить прилипание
     */
    fun switchEnableSticky(enabled: Boolean) {
        if (stickingEnabled != enabled) {
            stickingEnabled = enabled
            if (stickingEnabled) {
                updateHeader()
            } else {
                updateHeaderInfo(null)
            }
        }
    }

    override fun onAttachedToWindow(view: RecyclerView?) {
        super.onAttachedToWindow(view)
        hasPadding = view?.paddingTop ?: 0 > 0
        recyclerView = view
    }

    override fun onDetachedFromWindow(view: RecyclerView?, recycler: RecyclerView.Recycler?) {
        super.onDetachedFromWindow(view, recycler)
        recyclerView = null
    }

    override fun onLayoutChildren(
        recycler: RecyclerView.Recycler?,
        state: RecyclerView.State
    ) {
        super.onLayoutChildren(recycler, state)
        updateHeader()
    }

    override fun scrollVerticallyBy(dy: Int, recycler: RecyclerView.Recycler, state: RecyclerView.State): Int =
        super.scrollVerticallyBy(dy, recycler, state).also { scrollVerticallyBy ->
            if (scrollVerticallyBy != 0) {
                updateHeader()
            }
        }

    /**
     * Обновить заголовок
     */
    fun updateHeader() {
        if (!stickingEnabled) {
            return
        }
        stickyHeaderTop = stickyHeaderView.top - currentTopOffset

        val reversed = reverseLayout
        val lastVisibleItemAdapterPosition = getLastVisibleItemPosition(reversed)
        val firstVisibleItemAdapterPosition = getFirstVisibleItemPosition(reversed)

        val topMostItemPosition = if (reversed) lastVisibleItemAdapterPosition else firstVisibleItemAdapterPosition
        val bottomMostItemPosition = if (reversed) firstVisibleItemAdapterPosition else lastVisibleItemAdapterPosition

        if (topMostItemPosition >= 0) {

            laidOutItemsListener?.let { laidOutItemsListener ->
                laidOutItemsListener.onItemsLaidOut(topMostItemPosition, bottomMostItemPosition)

                val firstItemShown = bottomMostItemPosition == 0
                val bottomMostView = findViewByPosition(bottomMostItemPosition)
                bottomMostView!!.getGlobalVisibleRect(rect)
                val bottomMostItemBottom = rect.bottom
                var bottomPosDifference = 0
                recyclerView?.let { recyclerView ->
                    recyclerView.getGlobalVisibleRect(rect)
                    bottomPosDifference = bottomMostItemBottom - (rect.bottom - recyclerView.paddingBottom)
                }
                val atBottomOfItem = firstItemShown && bottomPosDifference < BOTTOM_ITEM_THRESHOLD
                laidOutItemsListener.onFirstItemShownStateChanged(firstItemShown, atBottomOfItem)
            }

            val topMostView = findViewByPosition(topMostItemPosition)
            if (!reversed && topMostItemPosition == 0 && (topMostView == null || topMostView.top + stickyHeaderPositionError >= stickyHeaderTop)) {
                updateHeaderInfo(null)
                return
            }
        }

        updateHeaderInfo(stickyHeaderInfoProvider.getStickyHeaderInfo(topMostItemPosition))
    }

    private fun getFirstVisibleItemPosition(reversed: Boolean): Int {
        var firstVisibleItemAdapterPosition = findFirstVisibleItemPosition()
        if (hasPadding && !clipToPadding && !reversed) {
            var view: View? = findViewByPosition(firstVisibleItemAdapterPosition)
            while (firstVisibleItemAdapterPosition > 0 && (view == null || view.top > stickyHeaderTop)) {
                firstVisibleItemAdapterPosition--
                view = findViewByPosition(firstVisibleItemAdapterPosition)
            }
        }
        return firstVisibleItemAdapterPosition
    }

    private fun getLastVisibleItemPosition(reversed: Boolean): Int {
        var lastVisibleItemAdapterPosition = findLastVisibleItemPosition()
        if (lastVisibleItemAdapterPosition == RecyclerView.NO_POSITION) {
            return lastVisibleItemAdapterPosition
        }
        if (hasPadding && !clipToPadding && reversed) {
            var view: View? = findViewByPosition(lastVisibleItemAdapterPosition)
            while (view != null && view.top > stickyHeaderTop) {
                lastVisibleItemAdapterPosition++
                view = findViewByPosition(lastVisibleItemAdapterPosition)
            }
        }
        return lastVisibleItemAdapterPosition
    }

    private fun updateHeaderInfo(stickyHeaderInfo: I?) {
        if (stickyHeaderInfo != null && stickyHeaderInfo.isNotEmpty()) {
            stickyHeaderUpdater.updateStickyHeader(stickyHeaderInfo)
            stickyHeaderView.visibility = View.VISIBLE
        } else {
            stickyHeaderView.visibility = View.INVISIBLE
        }
    }
}
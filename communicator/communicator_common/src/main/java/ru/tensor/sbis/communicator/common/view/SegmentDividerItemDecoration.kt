package ru.tensor.sbis.communicator.common.view

import android.graphics.Canvas
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.tensor.sbis.mvp.layoutmanager.PaginationLayoutManager

/**
 * Sticky-top header для списка RecyclerView.
 *
 * Начинает отображаться для каждого item'a RecyclerView после заданной позиции в списке
 */
class SegmentDividerItemDecoration (
        private val segmentDividerResource: Int,
        private val layoutManager: PaginationLayoutManager,
        private val callback: Callback
) : RecyclerView.ItemDecoration() {

    companion object {
        private const val STILL_NOT_CALCULATED = -1
    }

    private var segmentDividerView: View? = null
    private var topSeparatorMargin = STILL_NOT_CALCULATED
    private var segmentDividerState: SegmentDividerState = SegmentDividerState.HIDDEN

    override fun onDrawOver(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(canvas, parent, state)

        if (isNeedToShowStickyHeader(layoutManager.findFirstVisibleItemPosition())) {
            if (segmentDividerView == null) {
                segmentDividerView = LayoutInflater.from(parent.context).inflate(segmentDividerResource, parent, false)
                fixLayoutSize(segmentDividerView!!, parent)
            }
            segmentDividerView!!.draw(canvas)
            if (segmentDividerState == SegmentDividerState.HIDDEN) onVisibilityChanged()
        } else {
            if (segmentDividerState == SegmentDividerState.VISIBLE) onVisibilityChanged()
        }
    }

    /**
     * Проверка на необходимость отображения sticky header
     *
     * @param position - позиция первого видимого элемента в списке
     * @return true - если нужно отобразить sticky header
     */
    private fun isNeedToShowStickyHeader(position: Int): Boolean {
        val segmentDividerPosition = callback.getSegmentDividerPosition()
        if (segmentDividerPosition < 0 || segmentDividerPosition == Integer.MAX_VALUE) {
            return false
        }

        // Дополнительные условия связаны с отображением при открытой клавиатуре и при полностью видимом контенте
        if (position >= segmentDividerPosition && (!callback.isAllItemsVisible ||
                        callback.isAllItemsVisible && layoutManager.findFirstCompletelyVisibleItemPosition() != 0)) {
            if (topSeparatorMargin == STILL_NOT_CALCULATED) topSeparatorMargin = callback.getTopSeparatorMargin()
            val segmentDividerItem = layoutManager.findViewByPosition(segmentDividerPosition)
            if (segmentDividerItem == null || segmentDividerItem.top < - topSeparatorMargin) {
                return true
            }
        }
        return false
    }

    private fun fixLayoutSize(view: View, parent: ViewGroup) {
        val widthSpec = View.MeasureSpec.makeMeasureSpec(parent.width, View.MeasureSpec.EXACTLY)
        val heightSpec = View.MeasureSpec.makeMeasureSpec(parent.height, View.MeasureSpec.UNSPECIFIED)

        val childWidth = ViewGroup.getChildMeasureSpec(widthSpec, parent.paddingLeft + parent.paddingRight, view.layoutParams.width)
        val childHeight = ViewGroup.getChildMeasureSpec(heightSpec, parent.paddingTop + parent.paddingBottom, view.layoutParams.height)

        view.measure(childWidth, childHeight)
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)
    }

    private fun onVisibilityChanged() {
        segmentDividerState = if (segmentDividerState == SegmentDividerState.VISIBLE) SegmentDividerState.HIDDEN else SegmentDividerState.VISIBLE
        callback.onVisibilityChanged(segmentDividerState == SegmentDividerState.VISIBLE)
    }

    interface Callback {

        /**
         * Видны ли на экране все элементы RecyclerView
         */
        val isAllItemsVisible: Boolean

        /**
         * Получить позицию item'a, начиная с которого нужно отображать stick-top header
         */
        fun getSegmentDividerPosition(): Int

        /**
         * Отступ от верхней границы item'а, после которого начинается отрисовка stick-top header'а. По умолчанию равен нулю
         */
        fun getTopSeparatorMargin(): Int = 0

        /**
         * Выполнить какие-либо действия при смене видимости stick-top header'а
         */
        fun onVisibilityChanged(isVisible: Boolean)
    }

    enum class SegmentDividerState {
        VISIBLE,
        HIDDEN
    }
}
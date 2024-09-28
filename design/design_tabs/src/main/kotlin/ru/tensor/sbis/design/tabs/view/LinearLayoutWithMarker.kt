package ru.tensor.sbis.design.tabs.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.RectF
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import androidx.core.view.isVisible
import ru.tensor.sbis.design.tabs.R
import ru.tensor.sbis.design.tabs.tabItem.SbisTabItemStyleHolder
import ru.tensor.sbis.design.tabs.tabItem.SbisTabView
import ru.tensor.sbis.design.theme.HorizontalPosition
import ru.tensor.sbis.design.utils.ThemeContextBuilder
import ru.tensor.sbis.design.utils.delegateNotEqual

/**
 * View group, в котором можно нарисовать маркер.
 *
 * Позиционирование элементов осуществляется как в Linear Layout с горизонтальной ориентацией.
 * @author da.zolotarev
 */
internal class LinearLayoutWithMarker constructor(
    context: Context
) : ViewGroup(
    ThemeContextBuilder(context, R.attr.sbisTabsView_Theme, R.style.SbisTabsViewDefaultTheme).build(),
    null,
    R.attr.sbisTabsView_Theme,
    R.style.SbisTabsViewDefaultTheme
) {
    private lateinit var styleHolder: SbisTabsStyleHolder
    private lateinit var tabItemStyleHolder: SbisTabItemStyleHolder
    private lateinit var mainTabItemStyleHolder: SbisTabItemStyleHolder

    private var isTabsFitViewGroup = false

    private val markerRect = RectF()

    /** Индекс выбранной вкладки. */
    internal var selectedIndex: Int by delegateNotEqual(0) { _ -> invalidate() }

    /** Видимость нижней границы вкладок */
    internal var isBottomBorderVisible by delegateNotEqual(false) { _ -> invalidate() }

    /**
     * Добавить view во view group, учитывая позицию.
     */
    fun addViewWithPosition(child: View, params: ViewGroup.LayoutParams, position: HorizontalPosition) {
        addView(child, LayoutParams(params, position))
    }

    /**
     * @SelfDocumented
     */
    fun setStyleHolder(
        styleHolder: SbisTabsStyleHolder,
        tabItemStyleHolder: SbisTabItemStyleHolder,
        mainTabItemStyleHolder: SbisTabItemStyleHolder
    ) {
        this.styleHolder = styleHolder
        this.tabItemStyleHolder = tabItemStyleHolder
        this.mainTabItemStyleHolder = mainTabItemStyleHolder
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val parentWidthSize = MeasureSpec.getSize(widthMeasureSpec)

        var childrenWidth = paddingLeft + paddingRight

        children.filter { it.isVisible }.forEach {
            measureChild(it, widthMeasureSpec, heightMeasureSpec)
            val lp = it.layoutParams as LayoutParams
            childrenWidth += lp.leftMargin + it.measuredWidth + lp.rightMargin
        }

        isTabsFitViewGroup = childrenWidth < parentWidthSize

        if (!isTabsFitViewGroup) {
            setMeasuredDimension(childrenWidth, heightMeasureSpec)
        } else {
            setMeasuredDimension(parentWidthSize, heightMeasureSpec)
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val dX = layoutLeftPositionChildren(bottom)
        layoutRightPositionChildren(right, bottom, dX)
    }

    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)
        childCount
        val childView = getChildAt(selectedIndex.coerceAtMost(childCount - 1)) as SbisTabView?
        childView ?: return
        val isMain = childView.styleHolder == mainTabItemStyleHolder

        markerRect.left = childView.left.toFloat() + tabItemStyleHolder.horizontalPadding
        markerRect.right = if (isMain) {
            markerRect.left + styleHolder.mainTabMarkerWidth
        } else {
            childView.right.toFloat() - tabItemStyleHolder.horizontalPadding
        }
        markerRect.top = height - styleHolder.markerThick.toFloat()
        markerRect.bottom = height.toFloat()
        canvas?.drawRoundRect(
            markerRect,
            styleHolder.markerCornerRadius,
            styleHolder.markerCornerRadius,
            styleHolder.markerPaint
        )
        if (isBottomBorderVisible) {
            canvas?.drawRect(
                left.toFloat(),
                bottom - styleHolder.borderHeight,
                right.toFloat(),
                bottom.toFloat(),
                styleHolder.borderPaint
            )
        }
    }

    private fun layoutLeftPositionChildren(bottom: Int): Int {
        var dX = paddingLeft
        children.filter {
            (it.layoutParams as LayoutParams).position == HorizontalPosition.LEFT
        }.forEach {
            val lp = it.layoutParams as LayoutParams
            val (childWidth: Int, childBottom, childTop) = getChildBounds(it, bottom, lp)
            if (it.isVisible) {
                it.layout(
                    dX + lp.leftMargin,
                    childTop,
                    dX + lp.leftMargin + childWidth + lp.rightMargin,
                    childBottom
                )
                dX += childWidth + lp.leftMargin
            }
        }
        return dX
    }

    private fun layoutRightPositionChildren(right: Int, bottom: Int, dX: Int) {
        var xLeftPos = dX
        var xRightPos = right - paddingRight

        if (isTabsFitViewGroup) {
            children.filter {
                (it.layoutParams as LayoutParams).position == HorizontalPosition.RIGHT
            }.toList().reversed().forEach {
                val lp = it.layoutParams as LayoutParams
                val (childWidth: Int, childBottom, childTop) = getChildBounds(it, bottom, lp)
                if (it.isVisible) {
                    it.layout(
                        xRightPos - lp.rightMargin - childWidth + lp.leftMargin,
                        childTop,
                        xRightPos - lp.rightMargin,
                        childBottom
                    )
                    xRightPos += -lp.rightMargin - childWidth + lp.leftMargin
                }
            }
        } else {
            children.filter {
                (it.layoutParams as LayoutParams).position == HorizontalPosition.RIGHT
            }.toList().forEach {
                val lp = it.layoutParams as LayoutParams
                val (childWidth: Int, childBottom, childTop) = getChildBounds(it, bottom, lp)
                if (it.isVisible) {
                    it.layout(
                        xLeftPos + lp.leftMargin,
                        childTop,
                        xLeftPos + lp.leftMargin + childWidth + lp.rightMargin,
                        childBottom
                    )
                    xLeftPos += childWidth + lp.leftMargin
                }
            }
        }
    }

    private fun getChildBounds(
        it: View,
        bottom: Int,
        lp: LayoutParams
    ): Triple<Int, Int, Int> {
        val childWidth: Int = it.measuredWidth
        val childBottom = bottom - paddingBottom - lp.bottomMargin
        val childTop = childBottom - it.measuredHeight
        return Triple(childWidth, childBottom, childTop)
    }

    /**
     * Набор layout params для [LinearLayoutWithMarker].
     *
     * [position] - позиция дочерней view.
     */
    class LayoutParams(
        source: ViewGroup.LayoutParams,
        val position: HorizontalPosition
    ) : MarginLayoutParams(source)
}
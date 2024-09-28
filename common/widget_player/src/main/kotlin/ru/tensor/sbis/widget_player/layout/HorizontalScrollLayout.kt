package ru.tensor.sbis.widget_player.layout

import android.content.Context
import android.graphics.Canvas
import android.view.MotionEvent
import android.view.View
import android.widget.HorizontalScrollView
import ru.tensor.sbis.richtext.span.view.table.NestedScrollTouchInterceptor
import ru.tensor.sbis.widget_player.layout.internal.fadingedge.HorizontalFadingEdgeDrawer
import kotlin.math.max

/**
 * Контейнер для горизонтального скролла контента.
 * Может содержать только единственный child-контент.
 *
 * @property fadingEdgeWidth толщина тени у краёв при горизонтальном скроле
 * (тень показывает, что часть контента находится за пределами видимости)
 *
 * @author am.boldinov
 */
open class HorizontalScrollLayout(context: Context) : HorizontalScrollView(context) {

    private var fadingEdgeEnabled = false

    private val fadingEdgeDrawer = HorizontalFadingEdgeDrawer()

    @Suppress("LeakingThis")
    private val nestedScrollInterceptor = NestedScrollTouchInterceptor(this)

    init {
        fadingEdgeDrawer.color = solidColor
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return nestedScrollInterceptor.onInterceptTouchEvent(ev) && super.onInterceptTouchEvent(ev)
    }

    override fun setFadingEdgeLength(length: Int) {
        if (fadingEdgeDrawer.length != length) {
            fadingEdgeDrawer.length = length
            invalidate()
        }
    }

    override fun getHorizontalFadingEdgeLength(): Int {
        if (isHorizontalFadingEdgeEnabled) {
            return fadingEdgeDrawer.length
        }
        return 0
    }

    override fun isHorizontalFadingEdgeEnabled(): Boolean {
        return fadingEdgeEnabled
    }

    override fun setHorizontalFadingEdgeEnabled(horizontalFadingEdgeEnabled: Boolean) {
        if (fadingEdgeEnabled != horizontalFadingEdgeEnabled) {
            fadingEdgeEnabled = horizontalFadingEdgeEnabled
            invalidate()
        }
    }

    /**
     * Реализация для одинаковой работы на всех версиях Android
     * (со стандартной реализацией поведение на 5 и 6 отлчается от других).
     */
    override fun measureChildWithMargins(
        child: View,
        parentWidthMeasureSpec: Int,
        widthUsed: Int,
        parentHeightMeasureSpec: Int,
        heightUsed: Int
    ) {
        val lp = child.layoutParams as MarginLayoutParams
        val childHeightMeasureSpec = getChildMeasureSpec(
            parentHeightMeasureSpec,
            paddingTop + paddingBottom + lp.topMargin + lp.bottomMargin + heightUsed,
            lp.height
        )
        val usedTotal = paddingLeft + paddingRight + lp.leftMargin + lp.rightMargin + widthUsed
        val childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(
            max(0, MeasureSpec.getSize(parentWidthMeasureSpec) - usedTotal),
            MeasureSpec.UNSPECIFIED
        )
        child.measure(childWidthMeasureSpec, childHeightMeasureSpec)
    }

    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)
        if (isHorizontalFadingEdgeEnabled) {
            fadingEdgeDrawer.draw(this, canvas, leftFadingEdgeStrength, rightFadingEdgeStrength)
        }
    }

}
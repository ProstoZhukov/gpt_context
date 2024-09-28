package ru.tensor.sbis.widget_player.layout

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import androidx.core.view.isGone

/**
 * @author am.boldinov
 */
open class VerticalBlockLayout(
    context: Context
) : ViewGroup(context) {

    var fixedParentWidth: Boolean = false
        set(value) {
            if (field != value) {
                field = value
                requestLayout()
            }
        }

    var clipMargins: Boolean = true
        set(value) {
            if (field != value) {
                field = value
                requestLayout()
            }
        }

    var gapSize: Int = 0
        set(value) {
            if (field != value) {
                field = value
                requestLayout()
            }
        }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthSpec = if (fixedParentWidth) {
            MeasureSpec.makeMeasureSpec(
                MeasureSpec.getSize(widthMeasureSpec),
                MeasureSpec.EXACTLY
            )
        } else {
            widthMeasureSpec
        }
        var usedHeight = 0
        var maxWidth = 0

        var childState = 0

        var prevLp: LayoutParams? = null
        children.forEachIndexed { index, child ->
            if (!child.isGone) {
                val lp = child.layoutParams as LayoutParams

                clipVirtualMargins(index, child, lp, prevLp)

                measureChildWithMargins(child, widthSpec, 0, heightMeasureSpec, usedHeight)

                val childHeight = child.measuredHeight + lp.virtualTopMargin + lp.virtualBottomMargin
                usedHeight = maxOf(usedHeight, usedHeight + childHeight)

                val horizontalMargin = lp.virtualLeftMargin + lp.virtualRightMargin
                val childWidth = child.measuredWidth + horizontalMargin
                maxWidth = maxOf(maxWidth, childWidth)
                childState = combineMeasuredStates(childState, child.measuredState)
                prevLp = lp
            }
        }

        usedHeight += paddingTop + paddingBottom
        usedHeight = maxOf(usedHeight, suggestedMinimumHeight)

        maxWidth += paddingStart + paddingEnd
        maxWidth = maxOf(maxWidth, suggestedMinimumWidth)

        setMeasuredDimension(
            resolveSizeAndState(maxWidth, widthSpec, childState),
            resolveSizeAndState(usedHeight, heightMeasureSpec, 0)
        )
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        var childTop = paddingTop
        children.forEach { child ->
            if (!child.isGone) {
                val lp = child.layoutParams as LayoutParams
                val childWidth = child.measuredWidth
                val childHeight = child.measuredHeight
                val childLeft = paddingStart + lp.virtualLeftMargin
                childTop += lp.virtualTopMargin
                child.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight)
                childTop += childHeight + lp.virtualBottomMargin
            }
        }
    }

    override fun measureChildWithMargins(
        child: View,
        parentWidthMeasureSpec: Int,
        widthUsed: Int,
        parentHeightMeasureSpec: Int,
        heightUsed: Int
    ) {
        val lp = child.layoutParams as LayoutParams

        val childWidthMeasureSpec = getChildMeasureSpec(
            parentWidthMeasureSpec,
            paddingStart + paddingEnd + lp.virtualLeftMargin + lp.virtualRightMargin + widthUsed,
            lp.width
        )
        val childHeightMeasureSpec = getChildMeasureSpec(
            parentHeightMeasureSpec,
            paddingTop + paddingBottom + lp.virtualTopMargin + lp.virtualBottomMargin + heightUsed,
            lp.height
        )

        child.measure(childWidthMeasureSpec, childHeightMeasureSpec)
    }

    override fun getBaseline(): Int {
        if (childCount > 0) {
            return getChildAt(0).baseline
        }
        return super.getBaseline()
    }

    override fun checkLayoutParams(lp: ViewGroup.LayoutParams?): Boolean {
        return lp is LayoutParams
    }

    override fun generateLayoutParams(attrs: AttributeSet?): ViewGroup.LayoutParams {
        return LayoutParams(context, attrs)
    }

    override fun generateLayoutParams(lp: ViewGroup.LayoutParams): ViewGroup.LayoutParams {
        if (lp is MarginLayoutParams) {
            return LayoutParams(lp)
        }
        return LayoutParams(lp)
    }

    override fun generateDefaultLayoutParams(): ViewGroup.LayoutParams {
        return LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    /**
     * Возвращает измеренную высоту чайлда вместе с отступами.
     */
    fun getFullMeasuredHeight(childIndex: Int): Int {
        return getChildAt(childIndex)?.let {
            val lp = it.layoutParams as LayoutParams
            it.measuredHeight + lp.virtualTopMargin + lp.virtualBottomMargin
        } ?: 0
    }

    /**
     * Возвращает измеренную ширину чайлда вместе с отступами.
     */
    fun getFullMeasuredWidth(childIndex: Int): Int {
        return getChildAt(childIndex)?.let {
            val lp = it.layoutParams as LayoutParams
            it.measuredWidth + lp.virtualLeftMargin + lp.virtualRightMargin
        } ?: 0
    }

    protected open fun getTopOffset(index: Int, child: View): Int {
        return if (gapSize > 0 && index > 0) {
            gapSize
        } else {
            0
        }
    }

    protected open fun getLeftOffset(index: Int, child: View): Int {
        return 0
    }

    private fun clipVirtualMargins(childIndex: Int, child: View, current: LayoutParams, previous: LayoutParams?) {
        if (clipMargins) {
            previous?.let { prev ->
                current.virtualTopMargin = maxOf(prev.bottomMargin, current.topMargin) + getTopOffset(childIndex, child)
            } ?: run {
                current.virtualTopMargin = 0
            }
            current.virtualBottomMargin = 0
        } else {
            current.virtualTopMargin = current.topMargin + getTopOffset(childIndex, child)
            current.virtualBottomMargin = current.bottomMargin
        }
        current.virtualLeftMargin = current.leftMargin + getLeftOffset(childIndex, child)
        current.virtualRightMargin = current.rightMargin
    }

    class LayoutParams : MarginLayoutParams {

        var virtualLeftMargin = leftMargin
            internal set

        var virtualRightMargin = rightMargin
            internal set

        var virtualTopMargin = topMargin
            internal set

        var virtualBottomMargin = bottomMargin
            internal set

        constructor(c: Context?, attrs: AttributeSet?) : super(c, attrs)
        constructor(width: Int, height: Int) : super(width, height)
        constructor(source: MarginLayoutParams?) : super(source)
        constructor(source: ViewGroup.LayoutParams?) : super(source)
    }
}
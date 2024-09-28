package ru.tensor.sbis.widget_player.widget.column

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import ru.tensor.sbis.widget_player.layout.HorizontalScrollLayout

/**
 * @author am.boldinov
 */
@SuppressLint("ViewConstructor")
internal class ColumnLayoutView(
    context: Context,
    options: ColumnLayoutOptions
) : HorizontalScrollLayout(context) {

    private val container = ColumnContainer(context, options).apply {
        layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
    }

    val childrenContainer: ViewGroup = container

    init {
        setFadingEdgeLength(options.fadingEdgeLength.getValuePx(context))
        isHorizontalFadingEdgeEnabled = true
        addView(container)
    }

    fun setChildrenProportions(proportions: List<Int>) {
        container.proportions = proportions
    }

    private class ColumnContainer(
        context: Context,
        private val options: ColumnLayoutOptions
    ) : LinearLayout(context) {

        var proportions: List<Int> = emptyList()
            set(value) {
                if (field != value) {
                    field = value
                    proportionSum = value.sum()
                    requestLayout()
                }
            }
        private var proportionSum = 0

        private val minimalColumnWidth = options.minimalColumnWidth.getValuePx(context)
        private val gapSize = options.gapSize.getValuePx(context)

        init {
            orientation = HORIZONTAL
            showDividers = SHOW_DIVIDER_MIDDLE
            dividerDrawable = DividerDrawable(gapSize)
        }

        override fun measureChildWithMargins(
            child: View,
            parentWidthMeasureSpec: Int,
            widthUsed: Int,
            parentHeightMeasureSpec: Int,
            heightUsed: Int
        ) {
            val proportionSum = proportionSum.takeIf { it > 0 } ?: (childCount * options.defaultItemProportion)
            val index = indexOfChild(child)
            val columnWidth = proportions.getOrElse(index) {
                options.defaultItemProportion
            }.let {
                val maxPercent = ((100f / proportionSum) * it) / 100f
                val parentWidth = MeasureSpec.getSize(parentWidthMeasureSpec) - gapSize * (childCount - 1)
                maxOf((parentWidth * maxPercent).toInt(), minimalColumnWidth)
            }
            val lp = child.layoutParams as MarginLayoutParams

            val childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(
                columnWidth - (paddingStart + paddingEnd + lp.leftMargin + lp.rightMargin),
                MeasureSpec.EXACTLY
            )
            val childHeightMeasureSpec = getChildMeasureSpec(
                parentHeightMeasureSpec,
                paddingTop + paddingBottom + lp.topMargin + lp.bottomMargin + heightUsed,
                lp.height
            )

            child.measure(childWidthMeasureSpec, childHeightMeasureSpec)
        }
    }

    private class DividerDrawable(private val size: Int) : Drawable() {

        override fun getIntrinsicWidth(): Int = size

        override fun draw(canvas: Canvas) {

        }

        override fun setAlpha(alpha: Int) {

        }

        override fun setColorFilter(colorFilter: ColorFilter?) {

        }

        @Deprecated("Deprecated in Java", ReplaceWith("PixelFormat.TRANSLUCENT", "android.graphics.PixelFormat"))
        override fun getOpacity(): Int = PixelFormat.TRANSLUCENT
    }
}
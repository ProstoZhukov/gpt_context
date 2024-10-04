package ru.tensor.sbis.design.radio_group.control.layout

import android.view.View
import android.view.ViewGroup
import androidx.core.view.forEachIndexed
import ru.tensor.sbis.design.custom_view_tools.utils.MeasureSpecUtils
import ru.tensor.sbis.design.custom_view_tools.utils.layout
import ru.tensor.sbis.design.radio_group.control.Size
import kotlin.math.max

/**
 * Реализация горизонтальной стратегии измерения и размещения элементов с поддержкой переноса на несколько строк.
 *
 * [multiline] будут ли переноситься радиокнопки на следующую строку, если они не входят на одну строку.
 *
 * @author ps.smirnyh
 */
internal class RadioGroupLayoutHorizontalStrategy(
    private val horizontalPadding: Int,
    private val multiline: Boolean = true
) : RadioGroupLayoutStrategy {

    override fun measure(viewGroup: ViewGroup, widthMeasureSpec: Int, heightMeasureSpec: Int) = with(viewGroup) {
        val availableWidth = View.MeasureSpec.getSize(widthMeasureSpec)
        val availableHeight = View.MeasureSpec.getSize(heightMeasureSpec)

        if (multiline) {
            multiLineMeasure(availableWidth, availableHeight)
        } else {
            singleLineMeasure(availableWidth, availableHeight)
        }
    }

    override fun layout(viewGroup: ViewGroup, left: Int, top: Int) = with(viewGroup) {
        if (multiline) {
            var x = left
            var y = top
            var maxHeightInLine = 0
            forEachIndexed { index, view ->
                val viewWidthWithPadding = getValueWithPaddingByIndex(view.measuredWidth, index)
                if ((x + viewWidthWithPadding) <= measuredWidth) {
                    view.layout(getValueWithPaddingByIndex(x, index), y)
                    x += viewWidthWithPadding
                    maxHeightInLine = max(maxHeightInLine, view.measuredHeight)
                } else {
                    y += maxHeightInLine
                    maxHeightInLine = 0
                    x = left
                    view.layout(x, y)
                    x += view.measuredWidth
                    maxHeightInLine = max(maxHeightInLine, view.measuredHeight)
                }
            }
        } else {
            var x = left
            val y = top
            forEachIndexed { index, view ->
                view.layout(getValueWithPaddingByIndex(x, index), y)
                x += getValueWithPaddingByIndex(view.measuredWidth, index)
            }
        }
    }

    private fun ViewGroup.singleLineMeasure(availableWidth: Int, availableHeight: Int): Size {
        var currentWidth = 0
        var currentHeight = 0
        forEachIndexed { index, view ->
            view.measure(
                MeasureSpecUtils.makeAtMostSpec(availableWidth),
                MeasureSpecUtils.makeAtMostSpec(availableHeight)
            )
            currentWidth += getValueWithPaddingByIndex(view.measuredWidth, index)
            currentHeight = max(currentHeight, view.measuredHeight)
        }
        return Size(currentWidth, currentHeight)
    }

    private fun ViewGroup.multiLineMeasure(availableWidth: Int, availableHeight: Int): Size {
        var resultWidth = 0
        var resultHeight = 0
        var currentWidth = 0
        var currentHeight = 0
        forEachIndexed { index, view ->
            view.measure(
                MeasureSpecUtils.makeAtMostSpec(availableWidth),
                MeasureSpecUtils.makeAtMostSpec(availableHeight)
            )
            val viewWidthWithPadding = getValueWithPaddingByIndex(view.measuredWidth, index)
            if ((currentWidth + viewWidthWithPadding) <= availableWidth) {
                currentWidth += viewWidthWithPadding
                currentHeight = max(currentHeight, view.measuredHeight)
            } else {
                resultWidth = max(resultWidth, currentWidth)
                resultHeight += currentHeight
                currentWidth = view.measuredWidth
                currentHeight = view.measuredHeight
            }
            if (index == lastIndex) {
                resultWidth = max(resultWidth, currentWidth)
                resultHeight += currentHeight
            }
        }
        return Size(resultWidth, resultHeight)
    }

    private fun getValueWithPaddingByIndex(value: Int, index: Int) =
        if (index == 0) {
            value
        } else {
            value + horizontalPadding
        }

    private val ViewGroup.lastIndex: Int
        get() = childCount - 1
}
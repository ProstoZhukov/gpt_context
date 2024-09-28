package ru.tensor.sbis.design.radio_group.control.layout

import android.view.View.MeasureSpec
import android.view.ViewGroup
import androidx.core.view.forEach
import ru.tensor.sbis.design.custom_view_tools.utils.MeasureSpecUtils
import ru.tensor.sbis.design.custom_view_tools.utils.layout
import ru.tensor.sbis.design.radio_group.control.Size
import ru.tensor.sbis.design.radio_group.item.SbisRadioGroupItemView
import kotlin.math.max

/**
 * Реализация вертикальной стратегии измерения и расположения элементов внутри группы.
 *
 * [getHierarchyPadding] функция для получения отступа вложенных элементов иерархии.
 *
 * @author ps.smirnyh
 */
internal class RadioGroupLayoutVerticalStrategy(private val getHierarchyPadding: () -> Int) : RadioGroupLayoutStrategy {
    override fun measure(viewGroup: ViewGroup, widthMeasureSpec: Int, heightMeasureSpec: Int) = with(viewGroup) {
        val availableWidth = MeasureSpec.getSize(widthMeasureSpec)
        val availableHeight = MeasureSpec.getSize(heightMeasureSpec)

        var currentWidth = 0
        var currentHeight = 0

        forEach {
            it.measure(
                MeasureSpecUtils.makeAtMostSpec(availableWidth),
                MeasureSpecUtils.makeAtMostSpec(availableHeight)
            )
            currentWidth = max(currentWidth, (it as SbisRadioGroupItemView).getHierarchyWidth())
            currentHeight += it.measuredHeight
        }

        Size(currentWidth, currentHeight)
    }

    override fun layout(viewGroup: ViewGroup, left: Int, top: Int) = with(viewGroup) {
        var y = top
        forEach {
            it.layout(getHierarchyPadding() * (it as SbisRadioGroupItemView).hierarchyLevel + left, y)
            y += it.measuredHeight
        }
    }

    private fun SbisRadioGroupItemView.getHierarchyWidth() =
        measuredWidth + getHierarchyPadding() * hierarchyLevel
}
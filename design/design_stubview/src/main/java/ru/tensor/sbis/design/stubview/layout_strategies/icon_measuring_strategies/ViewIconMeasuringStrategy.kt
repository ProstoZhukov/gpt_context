package ru.tensor.sbis.design.stubview.layout_strategies.icon_measuring_strategies

import android.view.View
import androidx.annotation.Px

/**
 * Стратегия измерения кастомной иконки
 *
 * @author ma.kolpakov
 */
internal class ViewIconMeasuringStrategy : IconMeasuringStrategy {

    override fun measure(icon: View, @Px containerWidth: Int, @Px iconMinSize: Int, @Px iconMaxSize: Int) {
        val size = View.MeasureSpec.makeMeasureSpec(iconMaxSize, View.MeasureSpec.AT_MOST)
        icon.measure(size, size)
    }
}

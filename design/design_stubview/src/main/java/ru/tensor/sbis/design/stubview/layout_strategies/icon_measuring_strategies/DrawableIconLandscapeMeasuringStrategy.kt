package ru.tensor.sbis.design.stubview.layout_strategies.icon_measuring_strategies

import android.view.View
import android.view.View.MeasureSpec
import androidx.annotation.Px

/**
 * Стратегия измерения drawable иконки для ландшафтного режима
 *
 * @author ma.kolpakov
 */
internal class DrawableIconLandscapeMeasuringStrategy : IconMeasuringStrategy {

    private companion object {
        const val PERCENT_30 = 0.3
    }

    override fun measure(icon: View, @Px containerWidth: Int, @Px iconMinSize: Int, @Px iconMaxSize: Int) {
        val oneThirdOfWidth = (containerWidth * PERCENT_30).toInt()
        val iconSize = oneThirdOfWidth.coerceIn(iconMinSize, iconMaxSize)

        icon.measure(
            MeasureSpec.makeMeasureSpec(iconSize, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(iconSize, MeasureSpec.AT_MOST)
        )
    }
}

package ru.tensor.sbis.design.stubview.layout_strategies.icon_measuring_strategies

import android.view.View
import android.view.View.MeasureSpec
import androidx.annotation.Px

/**
 * Стратегия измерения drawable иконки для портретного режима
 *
 * @author ma.kolpakov
 */
internal class DrawableIconPortraitMeasuringStrategy : IconMeasuringStrategy {

    override fun measure(icon: View, @Px containerWidth: Int, @Px iconMinSize: Int, @Px iconMaxSize: Int) {
        val halfContainerWidth = containerWidth / 2
        val iconSize = halfContainerWidth.coerceIn(iconMinSize, iconMaxSize)

        icon.measure(
            MeasureSpec.makeMeasureSpec(iconSize, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(iconSize, MeasureSpec.AT_MOST)
        )
    }
}

package ru.tensor.sbis.design.topNavigation.util

import android.content.Context
import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.annotation.Dimension
import ru.tensor.sbis.design.topNavigation.R
import ru.tensor.sbis.design.utils.extentions.getColorFromAttr

/**
 * Класс для хранения ресурсов.
 *
 * @author da.zolotarev
 */
internal class FlatIndicatorViewStyleHolder {
    @Dimension
    var rectHeight = 0f

    @Dimension
    var rectWidth = 0f

    @Dimension
    var rectPadding = 0

    @Dimension
    var rectCornerRadius = 0

    @ColorInt
    var rectActiveColor = Color.MAGENTA

    @ColorInt
    var rectColor = Color.MAGENTA

    fun initStyle(context: Context) {
        context.apply {
            rectHeight = context.resources.getDimension(R.dimen.sbis_top_navigation_progress_view_rect_height)
            rectWidth = context.resources.getDimension(R.dimen.sbis_top_navigation_progress_view_rect_width)
            rectPadding =
                context.resources.getDimensionPixelSize(R.dimen.sbis_top_navigation_progress_view_rect_padding)
            rectCornerRadius =
                context.resources.getDimensionPixelSize(R.dimen.sbis_top_navigation_progress_view_rect_corner_radius)
            rectActiveColor = context.getColorFromAttr(ru.tensor.sbis.design.R.attr.flatIndicatorActiveColor)
            rectColor = context.getColorFromAttr(ru.tensor.sbis.design.R.attr.flatIndicatorColor)
        }
    }
}
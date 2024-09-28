package ru.tensor.sbis.widget_player.widget.image

import ru.tensor.sbis.widget_player.config.WidgetOptionsBuilder
import ru.tensor.sbis.widget_player.res.DesignAttr
import ru.tensor.sbis.widget_player.res.color.ColorRes
import ru.tensor.sbis.widget_player.res.color.attr
import ru.tensor.sbis.widget_player.res.dimen.DimenRes
import ru.tensor.sbis.widget_player.res.dimen.attr

/**
 * @author am.boldinov
 */
class ImageOptions(
    val verticalMargin: DimenRes,
    val borderRadius: DimenRes,
    val placeholderColor: ColorRes,
    val fadeDurationMillis: Int,
)

class ImageOptionsBuilder : WidgetOptionsBuilder<ImageOptions>() {

    private val verticalMargin = DimenRes.attr(DesignAttr.offset_3xs)

    var borderRadius: DimenRes = DimenRes.attr(DesignAttr.borderRadius_2xs)

    var placeholderColor = ColorRes.attr(DesignAttr.paleColor)

    var fadeDurationMillis = 150

    override fun build(): ImageOptions {
        return ImageOptions(
            verticalMargin = verticalMargin,
            borderRadius = borderRadius,
            placeholderColor = placeholderColor,
            fadeDurationMillis = fadeDurationMillis
        )
    }

}
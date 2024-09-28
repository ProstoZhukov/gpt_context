package ru.tensor.sbis.widget_player.widget.spoiler

import ru.tensor.sbis.widget_player.config.WidgetOptionsBuilder
import ru.tensor.sbis.widget_player.res.DesignAttr
import ru.tensor.sbis.widget_player.res.color.ColorRes
import ru.tensor.sbis.widget_player.res.color.attr
import ru.tensor.sbis.widget_player.res.dimen.DimenRes
import ru.tensor.sbis.widget_player.res.dimen.attr

/**
 * @author am.boldinov
 */
class SpoilerOptions(
    val verticalMargin: DimenRes,
    val contentMargin: DimenRes,
    val iconColor: ColorRes,
    val iconSize: DimenRes,
    val iconPadding: DimenRes
)

class SpoilerOptionsBuilder : WidgetOptionsBuilder<SpoilerOptions>() {

    private val verticalMargin = DimenRes.attr(DesignAttr.offset_3xs)
    private val contentMargin = DimenRes.attr(DesignAttr.offset_3xs)
    private val iconColor = ColorRes.attr(DesignAttr.iconColor)
    private val iconSize = DimenRes.attr(DesignAttr.iconSize_s)
    private val iconPadding = DimenRes.attr(DesignAttr.offset_2xs)

    override fun build(): SpoilerOptions {
        return SpoilerOptions(
            verticalMargin = verticalMargin,
            contentMargin = contentMargin,
            iconColor = iconColor,
            iconSize = iconSize,
            iconPadding = iconPadding
        )
    }

}
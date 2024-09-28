package ru.tensor.sbis.widget_player.widget.tabs

import ru.tensor.sbis.widget_player.config.WidgetOptionsBuilder
import ru.tensor.sbis.widget_player.converter.style.FontSize
import ru.tensor.sbis.widget_player.res.DesignAttr
import ru.tensor.sbis.widget_player.res.color.ColorRes
import ru.tensor.sbis.widget_player.res.color.attr
import ru.tensor.sbis.widget_player.res.dimen.DimenRes
import ru.tensor.sbis.widget_player.res.dimen.attr

/**
 * @author am.boldinov
 */
class TabOptions(
    val marginTop: DimenRes,
    val marginBottom: DimenRes,
    val contentPaddingTop: DimenRes,
    val contentPaddingBottom: DimenRes,
    val contentPaddingLeft: DimenRes,
    val contentPaddingRight: DimenRes,
    val borderColor: ColorRes,
    val borderRadius: DimenRes,
    val borderThickness: DimenRes,
    val titleFontSize: FontSize
)

class TabOptionsBuilder : WidgetOptionsBuilder<TabOptions>() {

    var borderColor: ColorRes = ColorRes.attr(DesignAttr.borderColor)
    var borderRadius: DimenRes = DimenRes.attr(DesignAttr.borderRadius_2xs)
    var borderThickness: DimenRes = DimenRes.attr(DesignAttr.borderThickness_s)

    var marginTop: DimenRes = DimenRes.attr(DesignAttr.offset_m)
    var marginBottom: DimenRes = marginTop

    var contentPaddingTop: DimenRes = DimenRes.attr(DesignAttr.offset_m)
    var contentPaddingBottom: DimenRes = contentPaddingTop
    var contentPaddingLeft: DimenRes = contentPaddingTop
    var contentPaddingRight: DimenRes = contentPaddingTop

    var titleFontSize: FontSize = FontSize.attr(DesignAttr.fontSize_m_scaleOff)

    override fun build() = TabOptions(
        marginTop = marginTop,
        marginBottom = marginBottom,
        contentPaddingTop = contentPaddingTop,
        contentPaddingBottom = contentPaddingBottom,
        contentPaddingLeft = contentPaddingLeft,
        contentPaddingRight = contentPaddingRight,
        borderColor = borderColor,
        borderRadius = borderRadius,
        borderThickness = borderThickness,
        titleFontSize = titleFontSize
    )

}
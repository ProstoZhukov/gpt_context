package ru.tensor.sbis.widget_player.widget.text

import ru.tensor.sbis.widget_player.config.WidgetOptionsBuilder
import ru.tensor.sbis.widget_player.converter.style.FontSize
import ru.tensor.sbis.widget_player.converter.style.TextColor
import ru.tensor.sbis.widget_player.res.DesignAttr
import ru.tensor.sbis.widget_player.res.array.IntArrayRes
import ru.tensor.sbis.widget_player.res.array.idColor
import ru.tensor.sbis.widget_player.res.color.attr
import ru.tensor.sbis.widget_player.res.dimen.attr

/**
 * @author am.boldinov
 */
class TextOptions(
    val textSize: FontSize,
    val textColor: TextColor,
    val linkTextColor: TextColor,
    val linksClickable: Boolean,
    val textColorPalette: IntArrayRes,
    val backgroundColorPalette: IntArrayRes
)

class TextOptionsBuilder : WidgetOptionsBuilder<TextOptions>() {

    var textSize: FontSize = FontSize.attr(DesignAttr.fontSize_m_scaleOff)

    var textColor: TextColor = TextColor.attr(DesignAttr.textColor)

    var linkTextColor: TextColor = TextColor.attr(DesignAttr.linkTextColor)

    var linksClickable: Boolean = true

    var textColorPalette: IntArrayRes = IntArrayRes.idColor(ru.tensor.sbis.richtext.R.array.richtext_css_class_text_color_palette_1)

    var backgroundColorPalette: IntArrayRes = IntArrayRes.idColor(ru.tensor.sbis.richtext.R.array.richtext_css_class_background_color_palette_1)

    override fun build(): TextOptions {
        return TextOptions(
            textSize = textSize,
            textColor = textColor,
            linkTextColor = linkTextColor,
            linksClickable = linksClickable,
            textColorPalette = textColorPalette,
            backgroundColorPalette = backgroundColorPalette
        )
    }

}
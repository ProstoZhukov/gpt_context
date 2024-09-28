package ru.tensor.sbis.widget_player.widget.paragraph

import ru.tensor.sbis.widget_player.config.WidgetOptionsBuilder
import ru.tensor.sbis.widget_player.converter.style.FontSize
import ru.tensor.sbis.widget_player.converter.style.FontStyle
import ru.tensor.sbis.widget_player.converter.style.StylePropertiesBuilder
import ru.tensor.sbis.widget_player.converter.style.TextAlignment
import ru.tensor.sbis.widget_player.converter.style.TextColor
import ru.tensor.sbis.widget_player.converter.style.WidgetProperties
import ru.tensor.sbis.widget_player.res.DesignAttr
import ru.tensor.sbis.widget_player.res.color.attr
import ru.tensor.sbis.widget_player.res.dimen.DimenRes
import ru.tensor.sbis.widget_player.res.dimen.attr

/**
 * @author am.boldinov
 */
class ParagraphOptions(
    val verticalMargin: DimenRes,
    val minHeight: DimenRes,
    val levelOptions: Map<ParagraphLevel, WidgetProperties>
)

class ParagraphOptionsBuilder : WidgetOptionsBuilder<ParagraphOptions>() {

    var verticalMargin: DimenRes = DimenRes.attr(DesignAttr.offset_2xs)

    var minHeight: DimenRes = DimenRes.attr(DesignAttr.inlineHeight_5xs)

    private val levelOptions = mapOf(
        ParagraphLevel.P2 to StylePropertiesBuilder(
            textAlignment = TextAlignment.CENTER,
            fontStyle = FontStyle.ITALIC,
            fontSize = FontSize.attr(DesignAttr.fontSize_m_scaleOff),
            textColor = TextColor.attr(DesignAttr.unaccentedTextColor)
        )
    )

    override fun build(): ParagraphOptions {
        return ParagraphOptions(verticalMargin, minHeight, levelOptions)
    }
}
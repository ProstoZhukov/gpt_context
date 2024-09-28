package ru.tensor.sbis.widget_player.widget.header

import android.graphics.Paint
import ru.tensor.sbis.widget_player.config.WidgetOptionsBuilder
import ru.tensor.sbis.widget_player.converter.style.FontSize
import ru.tensor.sbis.widget_player.res.DesignAttr
import ru.tensor.sbis.widget_player.res.ThemeResProvider
import ru.tensor.sbis.widget_player.res.WidgetDimen
import ru.tensor.sbis.widget_player.res.color.ColorRes
import ru.tensor.sbis.widget_player.res.color.attr
import ru.tensor.sbis.widget_player.res.dimen.DimenRes
import ru.tensor.sbis.widget_player.res.dimen.attr
import ru.tensor.sbis.widget_player.res.dimen.id
import ru.tensor.sbis.widget_player.res.dimen.valueInt

/**
 * @author am.boldinov
 */
class HeaderOptions(
    val levelOptions: Map<HeaderLevel, LevelOptions>
)

class HeaderOptionsBuilder : WidgetOptionsBuilder<HeaderOptions>() {

    private val levelOptions = mapOf(
        HeaderLevel.H0 to LevelOptions(
            textSize = FontSize.id(WidgetDimen.widget_player_header0_text_size),
            bottomMargin = DimenRes.id(WidgetDimen.widget_player_header0_bottom_margin)
        ),
        HeaderLevel.H1 to LevelOptions(
            textSize = FontSize.id(WidgetDimen.widget_player_header1_text_size),
            topMargin = DimenRes.id(WidgetDimen.widget_player_header1_top_margin),
            bottomMargin = DimenRes.id(WidgetDimen.widget_player_header1_bottom_margin),
            withBottomLine = true
        ),
        HeaderLevel.H2 to LevelOptions(
            textSize = FontSize.id(WidgetDimen.widget_player_header2_text_size),
            topMargin = DimenRes.id(WidgetDimen.widget_player_header2_top_margin),
            bottomMargin = DimenRes.id(WidgetDimen.widget_player_header2_bottom_margin)
        ),
        HeaderLevel.H3 to LevelOptions(
            textSize = FontSize.id(WidgetDimen.widget_player_header3_text_size),
            topMargin = DimenRes.id(WidgetDimen.widget_player_header3_top_margin),
            bottomMargin = DimenRes.id(WidgetDimen.widget_player_header3_bottom_margin)
        ),
        HeaderLevel.H4 to LevelOptions(
            textSize = FontSize.id(WidgetDimen.widget_player_header4_text_size),
            topMargin = DimenRes.id(WidgetDimen.widget_player_header4_top_margin),
            bottomMargin = DimenRes.id(WidgetDimen.widget_player_header4_bottom_margin)
        ),
        HeaderLevel.H5 to LevelOptions(
            textSize = FontSize.id(WidgetDimen.widget_player_header5_text_size),
            topMargin = DimenRes.id(WidgetDimen.widget_player_header5_top_margin),
            bottomMargin = DimenRes.id(WidgetDimen.widget_player_header5_bottom_margin)
        ),
        HeaderLevel.H6 to LevelOptions(
            textSize = FontSize.id(WidgetDimen.widget_player_header6_text_size),
            topMargin = DimenRes.id(WidgetDimen.widget_player_header6_top_margin),
            bottomMargin = DimenRes.id(WidgetDimen.widget_player_header6_bottom_margin)
        )
    )

    override fun build(): HeaderOptions {
        return HeaderOptions(levelOptions)
    }
}

class LevelOptions(
    val topMargin: DimenRes = DimenRes.valueInt(0),
    val bottomMargin: DimenRes = DimenRes.valueInt(0),
    val textSize: FontSize,
    val linePadding: DimenRes = DimenRes.attr(DesignAttr.offset_xs),
    val lineColor: ColorRes = ColorRes.attr(DesignAttr.unaccentedBorderColor),
    val lineThickness: DimenRes = DimenRes.attr(DesignAttr.borderThickness_s),
    withBottomLine: Boolean = false
) {

    internal val bottomLinePaintProvider = if (withBottomLine) ThemeResProvider.cached { context ->
        Paint().apply {
            style = Paint.Style.STROKE
            color = lineColor.getValue(context)
            strokeWidth = lineThickness.getValue(context)
        }
    } else null
}
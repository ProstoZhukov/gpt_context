package ru.tensor.sbis.widget_player.widget.table

import ru.tensor.sbis.richtext.converter.cfg.TableSize
import ru.tensor.sbis.widget_player.config.WidgetOptionsBuilder
import ru.tensor.sbis.widget_player.res.DesignAttr
import ru.tensor.sbis.widget_player.res.RichTextDimen
import ru.tensor.sbis.widget_player.res.WidgetDimen
import ru.tensor.sbis.widget_player.res.color.ColorRes
import ru.tensor.sbis.widget_player.res.color.attr
import ru.tensor.sbis.widget_player.res.dimen.DimenRes
import ru.tensor.sbis.widget_player.res.dimen.attr
import ru.tensor.sbis.widget_player.res.dimen.id

/**
 * @author am.boldinov
 */
class TableOptions(
    val marginTop: DimenRes,
    val marginBottom: DimenRes,
    val tableSize: TableSize,
    val borderColor: ColorRes,
    val borderThickness: DimenRes,
    val cellContentMargin: DimenRes,
    val minimalColumnWidth: DimenRes,
    val fadingEdgeLength: DimenRes
)

class TableOptionsBuilder : WidgetOptionsBuilder<TableOptions>() {

    var marginTop: DimenRes = DimenRes.attr(DesignAttr.offset_m)

    var marginBottom: DimenRes = marginTop

    var tableSize: TableSize = TableSize.FullSize

    var borderColor: ColorRes = ColorRes.attr(DesignAttr.unaccentedBorderColor)

    var borderThickness: DimenRes = DimenRes.attr(DesignAttr.borderThickness_s)

    val cellContentMargin: DimenRes = DimenRes.attr(DesignAttr.offset_xs)

    val minimalColumnWidth: DimenRes = DimenRes.id(RichTextDimen.richtext_table_min_column_width)

    val fadingEdgeLength: DimenRes = DimenRes.id(WidgetDimen.widget_player_default_fading_edge_length)

    override fun build(): TableOptions {
       return TableOptions(
           marginTop = marginTop,
           marginBottom = marginBottom,
           tableSize = tableSize,
           borderColor = borderColor,
           borderThickness = borderThickness,
           cellContentMargin = cellContentMargin,
           minimalColumnWidth = minimalColumnWidth,
           fadingEdgeLength = fadingEdgeLength
       )
    }

}
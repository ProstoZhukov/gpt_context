package ru.tensor.sbis.widget_player.widget.column

import ru.tensor.sbis.widget_player.config.WidgetOptionsBuilder
import ru.tensor.sbis.widget_player.res.DesignAttr
import ru.tensor.sbis.widget_player.res.WidgetDimen
import ru.tensor.sbis.widget_player.res.dimen.DimenRes
import ru.tensor.sbis.widget_player.res.dimen.attr
import ru.tensor.sbis.widget_player.res.dimen.id

/**
 * @author am.boldinov
 */
class ColumnLayoutOptions(
    val marginTop: DimenRes,
    val marginBottom: DimenRes,
    val defaultItemProportion: Int,
    val minimalColumnWidth: DimenRes,
    val gapSize: DimenRes,
    val fadingEdgeLength: DimenRes
)

class ColumnLayoutOptionsBuilder : WidgetOptionsBuilder<ColumnLayoutOptions>() {

    private val defaultItemProportion = 20

    var marginTop: DimenRes = DimenRes.attr(DesignAttr.offset_m)

    var marginBottom: DimenRes = marginTop

    var minimalColumnWidth: DimenRes = DimenRes.id(WidgetDimen.widget_player_min_column_width)

    var gapSize: DimenRes = DimenRes.attr(DesignAttr.offset_s)

    val fadingEdgeLength: DimenRes = DimenRes.id(WidgetDimen.widget_player_default_fading_edge_length)

    override fun build(): ColumnLayoutOptions {
        return ColumnLayoutOptions(
            marginTop = marginTop,
            marginBottom = marginBottom,
            defaultItemProportion = defaultItemProportion,
            minimalColumnWidth = minimalColumnWidth,
            gapSize = gapSize,
            fadingEdgeLength = fadingEdgeLength
        )
    }

}

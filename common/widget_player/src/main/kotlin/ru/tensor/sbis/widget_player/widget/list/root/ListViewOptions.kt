package ru.tensor.sbis.widget_player.widget.list.root

import ru.tensor.sbis.widget_player.config.WidgetOptionsBuilder
import ru.tensor.sbis.widget_player.converter.style.TextColor
import ru.tensor.sbis.widget_player.res.DesignAttr
import ru.tensor.sbis.widget_player.res.WidgetDimen
import ru.tensor.sbis.widget_player.res.color.attr
import ru.tensor.sbis.widget_player.res.dimen.DimenRes
import ru.tensor.sbis.widget_player.res.dimen.attr
import ru.tensor.sbis.widget_player.res.dimen.id

/**
 * @author am.boldinov
 */
class ListViewOptions(
    val markerSize: DimenRes,
    val bulletSize: DimenRes,
    val checkedTextColor: TextColor
)

class ListViewOptionsBuilder : WidgetOptionsBuilder<ListViewOptions>() {

    var markerSize: DimenRes = DimenRes.attr(DesignAttr.inlineHeight_5xs)

    var bulletSize: DimenRes = DimenRes.id(WidgetDimen.widget_player_list_view_bullet_size)

    var checkedTextColor: TextColor = TextColor.attr(DesignAttr.unaccentedTextColor)

    override fun build(): ListViewOptions {
        return ListViewOptions(
            markerSize = markerSize,
            bulletSize = bulletSize,
            checkedTextColor = checkedTextColor
        )
    }

}
package ru.tensor.sbis.widget_player.widget.image.icon

import ru.tensor.sbis.widget_player.config.WidgetOptionsBuilder
import ru.tensor.sbis.widget_player.res.DesignAttr
import ru.tensor.sbis.widget_player.res.dimen.DimenRes
import ru.tensor.sbis.widget_player.res.dimen.attr

/**
 * @author am.boldinov
 */
class IconOptions(
    val borderRadius: DimenRes
)

class IconOptionsBuilder : WidgetOptionsBuilder<IconOptions>() {

    var borderRadius: DimenRes = DimenRes.attr(DesignAttr.borderRadius_3xs)

    override fun build(): IconOptions {
        return IconOptions(
            borderRadius = borderRadius
        )
    }

}
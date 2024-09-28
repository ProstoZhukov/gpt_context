package ru.tensor.sbis.widget_player.widget.root.layout

import ru.tensor.sbis.widget_player.config.WidgetOptionsBuilder
import ru.tensor.sbis.widget_player.res.dimen.DimenRes
import ru.tensor.sbis.widget_player.res.dimen.valueInt

/**
 * @author am.boldinov
 */
class RootLayoutOptions(
    val paddingTop: DimenRes,
    val paddingLeft: DimenRes,
    val paddingBottom: DimenRes,
    val paddingRight: DimenRes,
    val columnGap: DimenRes
)

class RootLayoutOptionsBuilder : WidgetOptionsBuilder<RootLayoutOptions>() {

    var paddingTop: DimenRes = DimenRes.valueInt(0)

    var paddingLeft: DimenRes = DimenRes.valueInt(0)

    var paddingBottom: DimenRes = paddingTop

    var paddingRight: DimenRes = paddingLeft

    var columnGap: DimenRes = DimenRes.valueInt(0)

    override fun build(): RootLayoutOptions {
        return RootLayoutOptions(
            paddingTop = paddingTop,
            paddingLeft = paddingLeft,
            paddingBottom = paddingBottom,
            paddingRight = paddingRight,
            columnGap = columnGap
        )
    }

}
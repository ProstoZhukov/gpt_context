package ru.tensor.sbis.widget_player.widget.audio

import ru.tensor.sbis.widget_player.config.WidgetOptionsBuilder
import ru.tensor.sbis.widget_player.res.DesignAttr
import ru.tensor.sbis.widget_player.res.dimen.DimenRes
import ru.tensor.sbis.widget_player.res.dimen.attr

/**
 * @author am.boldinov
 */
class AudioMessageOptions(
    val marginTop: DimenRes,
    val marginBottom: DimenRes
)

class AudioMessageOptionsBuilder : WidgetOptionsBuilder<AudioMessageOptions>() {

    var marginTop: DimenRes = DimenRes.attr(DesignAttr.offset_m)
    var marginBottom: DimenRes = marginTop

    override fun build() = AudioMessageOptions(
        marginTop = marginTop,
        marginBottom = marginBottom
    )

}
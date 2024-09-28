package ru.tensor.sbis.widget_player.widget.video

import ru.tensor.sbis.widget_player.config.WidgetOptionsBuilder
import ru.tensor.sbis.widget_player.res.DesignAttr
import ru.tensor.sbis.widget_player.res.dimen.DimenRes
import ru.tensor.sbis.widget_player.res.dimen.attr

/**
 * @author am.boldinov
 */
class VideoMessageOptions(
    val marginTop: DimenRes,
    val marginBottom: DimenRes
)

class VideoMessageOptionsBuilder : WidgetOptionsBuilder<VideoMessageOptions>() {

    var marginTop: DimenRes = DimenRes.attr(DesignAttr.offset_m)
    var marginBottom: DimenRes = marginTop

    override fun build() = VideoMessageOptions(
        marginTop = marginTop,
        marginBottom = marginBottom
    )

}
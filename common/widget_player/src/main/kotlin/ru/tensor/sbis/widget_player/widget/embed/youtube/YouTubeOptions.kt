package ru.tensor.sbis.widget_player.widget.embed.youtube

import ru.tensor.sbis.widget_player.config.WidgetOptionsBuilder
import ru.tensor.sbis.widget_player.res.RichTextDimen
import ru.tensor.sbis.widget_player.res.dimen.DimenRes
import ru.tensor.sbis.widget_player.res.dimen.id

/**
 * @author am.boldinov
 */
class YouTubeOptions(
    val maxHeight: DimenRes
)

class YouTubeOptionsBuilder : WidgetOptionsBuilder<YouTubeOptions>() {

    var maxHeight: DimenRes = DimenRes.id(RichTextDimen.richtext_view_max_height)

    override fun build(): YouTubeOptions {
        return YouTubeOptions(maxHeight)
    }

}
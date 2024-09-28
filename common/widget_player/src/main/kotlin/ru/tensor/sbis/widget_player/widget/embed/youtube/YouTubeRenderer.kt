package ru.tensor.sbis.widget_player.widget.embed.youtube

import android.annotation.SuppressLint
import ru.tensor.sbis.richtext.R
import ru.tensor.sbis.richtext.span.view.youtube.YouTubePreviewer
import ru.tensor.sbis.richtext.span.view.youtube.YouTubeUtil
import ru.tensor.sbis.widget_player.layout.widget.WidgetContext
import ru.tensor.sbis.widget_player.layout.widget.WidgetRenderer
import ru.tensor.sbis.widget_player.util.setDefaultWidgetLayoutParams

/**
 * @author am.boldinov
 */
internal class YouTubeRenderer(
    context: WidgetContext,
    options: YouTubeOptions
) : WidgetRenderer<YouTubeElement> {

    @SuppressLint("InflateParams")
    override val view = (context.layoutInflater.inflate(
        R.layout.richtext_youtube_video,
        null
    ) as YouTubePreviewer).apply {
        setDefaultWidgetLayoutParams()
        setMaxPreviewHeightInPx(options.maxHeight.getValuePx(context))
        setOnPreviewClickListener { videoId ->
            YouTubeUtil.playYouTubeVideo(context, videoId)
        }
    }

    override fun render(element: YouTubeElement) {
        with(view) {
            setVideoId(element.videoId)
        }
    }
}
package ru.tensor.sbis.widget_player.widget.embed

import ru.tensor.sbis.richtext.span.view.youtube.YouTubeUtil
import ru.tensor.sbis.widget_player.converter.attributes.WidgetMapAttributes
import ru.tensor.sbis.widget_player.converter.attributes.WidgetAttributes
import ru.tensor.sbis.widget_player.converter.WidgetElementFactory
import ru.tensor.sbis.widget_player.converter.WidgetEnvironment
import ru.tensor.sbis.widget_player.converter.attributes.store.getNotNull
import ru.tensor.sbis.widget_player.widget.embed.youtube.YouTubeElement
import ru.tensor.sbis.widget_player.widget.link.TextLinkElementFactory

/**
 * @author am.boldinov
 */
internal class EmbedElementFactory : WidgetElementFactory<EmbedElement> {

    private val textLinkElementFactory = TextLinkElementFactory()

    override fun create(tag: String, attributes: WidgetAttributes, environment: WidgetEnvironment): EmbedElement {
        val src = attributes.getNotNull("value")
        val content = if (YouTubeUtil.isYouTubeVideo(src)) {
            val videoId = YouTubeUtil.pickVideoId(src)
            YouTubeElement(tag, WidgetMapAttributes.EMPTY, environment.resources, videoId)
        } else {
            val linkAttributes = WidgetMapAttributes("href" to src)
            textLinkElementFactory.create(tag, linkAttributes, environment)
        }
        return EmbedElement(tag, attributes, environment.resources, content)
    }
}
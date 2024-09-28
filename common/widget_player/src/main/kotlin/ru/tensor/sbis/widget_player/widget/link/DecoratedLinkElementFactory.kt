package ru.tensor.sbis.widget_player.widget.link

import ru.tensor.sbis.richtext.span.decoratedlink.DecoratedLinkType
import ru.tensor.sbis.widget_player.converter.attributes.WidgetAttributes
import ru.tensor.sbis.widget_player.converter.WidgetElementFactory
import ru.tensor.sbis.widget_player.converter.WidgetEnvironment
import ru.tensor.sbis.widget_player.converter.attributes.store.getNotNull

/**
 * @author am.boldinov
 */
internal class DecoratedLinkElementFactory(
    private val linkOptions: DecoratedLinkOptions,
    private val linkType: DecoratedLinkType? = null
) : WidgetElementFactory<DecoratedLinkElement> {

    override fun create(
        tag: String,
        attributes: WidgetAttributes,
        environment: WidgetEnvironment
    ): DecoratedLinkElement {
        val url = attributes.getNotNull("href")
        val linkType =
            this.linkType ?: DecoratedLinkType.fromValue(attributes.get("decorationType")) ?: DecoratedLinkType.MEDIUM
        val linkData = if (linkType == DecoratedLinkType.SMALL) {
            val decorationJson = attributes.getNotNull("decorationData")
            linkOptions.repository.getInlineLinkData(url, decorationJson)
        } else {
            linkOptions.repository.getLinkData(url)
        }
        return DecoratedLinkElement(tag, attributes, environment.resources, linkData, linkType)
    }
}
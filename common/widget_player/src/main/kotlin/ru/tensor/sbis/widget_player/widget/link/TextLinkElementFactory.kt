package ru.tensor.sbis.widget_player.widget.link

import ru.tensor.sbis.widget_player.converter.style.FormattedTextAttributes
import ru.tensor.sbis.widget_player.converter.style.FormattedTextStyle
import ru.tensor.sbis.widget_player.converter.style.FormattedTextStyleRange
import ru.tensor.sbis.widget_player.converter.attributes.WidgetAttributes
import ru.tensor.sbis.widget_player.converter.WidgetElementFactory
import ru.tensor.sbis.widget_player.converter.WidgetEnvironment
import ru.tensor.sbis.widget_player.widget.text.*

/**
 * @author am.boldinov
 */
internal class TextLinkElementFactory : WidgetElementFactory<FormattedTextElement> {

    override fun create(
        tag: String,
        attributes: WidgetAttributes,
        environment: WidgetEnvironment
    ): FormattedTextElement {
        val url = attributes.get("href")!!
        return FormattedTextElement(tag, attributes, environment.resources).apply {
            text = url
            textAttributes = FormattedTextAttributes(
                formats = listOf(
                    FormattedTextStyleRange(0, url.length, FormattedTextStyle(href = url))
                )
            )
        }
    }
}
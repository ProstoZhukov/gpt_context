package ru.tensor.sbis.widget_player.widget.paragraph

import ru.tensor.sbis.widget_player.converter.attributes.WidgetAttributes
import ru.tensor.sbis.widget_player.converter.style.TextAlignment
import ru.tensor.sbis.widget_player.converter.WidgetElementFactory
import ru.tensor.sbis.widget_player.converter.WidgetEnvironment
import ru.tensor.sbis.widget_player.converter.style.StylePropertiesBuilder

/**
 * @author am.boldinov
 */
internal class ParagraphElementFactory(
    private val options: ParagraphOptions,
    private val level: ParagraphLevel? = null
) : WidgetElementFactory<ParagraphElement> {

    override fun create(tag: String, attributes: WidgetAttributes, environment: WidgetEnvironment): ParagraphElement {
        return ParagraphElement(
            tag,
            attributes,
            environment.resources
        ).apply {
            styleReducer = {
                textAlignment = TextAlignment.fromValue(attributes.get("align"))
                applyLevelStyle(level)
            }
        }
    }

    private fun StylePropertiesBuilder.applyLevelStyle(level: ParagraphLevel?) {
        level?.let {
            options.levelOptions[it]?.let { properties ->
                apply(properties)
            }
        }
    }
}
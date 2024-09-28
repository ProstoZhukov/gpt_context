package ru.tensor.sbis.widget_player.widget.header

import ru.tensor.sbis.widget_player.converter.attributes.WidgetAttributes
import ru.tensor.sbis.widget_player.converter.style.FontWeight
import ru.tensor.sbis.widget_player.converter.style.TextAlignment
import ru.tensor.sbis.widget_player.converter.WidgetElementFactory
import ru.tensor.sbis.widget_player.converter.WidgetEnvironment
import ru.tensor.sbis.widget_player.converter.attributes.store.getAsInt

/**
 * @author am.boldinov
 */
internal class HeaderElementFactory(
    private val headerOptions: HeaderOptions,
    private val level: HeaderLevel? = null
) : WidgetElementFactory<HeaderElement> {

    override fun create(tag: String, attributes: WidgetAttributes, environment: WidgetEnvironment): HeaderElement {
        val align = attributes.get("align")
        val level = this.level ?: parseLevel(tag, attributes)?.let {
            HeaderLevel.fromValue(it)
        } ?: HeaderLevel.H3
        return HeaderElement(tag, attributes, environment.resources, level).apply {
            val options = headerOptions.levelOptions[level]
            styleReducer = {
                textAlignment = TextAlignment.fromValue(align)
                fontWeight = FontWeight.BOLD
                options?.let {
                    fontSize = it.textSize
                }
            }
        }
    }

    private fun parseLevel(tag: String, attributes: WidgetAttributes): Int? {
        return attributes.getAsInt("level") ?: tag.replace("[^0-9]".toRegex(), "").toIntOrNull()
    }
}
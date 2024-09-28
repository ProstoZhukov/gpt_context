package ru.tensor.sbis.widget_player.widget.codeblock

import ru.tensor.sbis.widget_player.converter.FormattedTextSpanClassBuilder
import ru.tensor.sbis.widget_player.converter.FormattedTextStyleBuilder
import ru.tensor.sbis.widget_player.converter.attributes.WidgetAttributes
import ru.tensor.sbis.widget_player.converter.style.FontStyle
import ru.tensor.sbis.widget_player.converter.style.FormattedTextColor
import ru.tensor.sbis.widget_player.converter.style.FormattedTextStyle
import ru.tensor.sbis.widget_player.res.color.ColorRes
import ru.tensor.sbis.widget_player.converter.WidgetElementFactory
import ru.tensor.sbis.widget_player.converter.WidgetEnvironment
import ru.tensor.sbis.widget_player.converter.attributes.store.getNotEmpty
import ru.tensor.sbis.widget_player.converter.attributes.store.getNotNull

/**
 * @author am.boldinov
 */
internal class CodeBlockElementFactory(
    options: CodeBlockOptions
) : WidgetElementFactory<CodeBlockElement> {

    private val defaultBuilder = SpanStyleBuilder(options.colorPalette)
    private val cssBuilder = SpanStyleBuilder(options.cssColorPalette)

    override fun create(tag: String, attributes: WidgetAttributes, environment: WidgetEnvironment): CodeBlockElement {
        val value = attributes.getNotNull("value")
        val valueWithTokens = attributes.getNotEmpty("valueWithTokens")
        val element = if (valueWithTokens != null) {
            val lang = attributes.get("lang")
            environment.textConverter.convertRichText(valueWithTokens, getLangBuilder(lang))
        } else {
            environment.textConverter.convert(value)
        }
        return CodeBlockElement(tag, attributes, environment.resources).apply {
            styleReducer = {
                fontStyle = FontStyle.MONO
            }
            addChild(element)
        }
    }

    private fun getLangBuilder(lang: String?): FormattedTextStyleBuilder {
        return when (lang?.lowercase()) {
            "css" -> cssBuilder
            else  -> defaultBuilder
        }
    }

    private class SpanStyleBuilder(
        private val palette: Map<String, ColorRes>
    ) : FormattedTextSpanClassBuilder() {

        override fun build(value: String): FormattedTextStyle? {
            return palette[value]?.let {
                FormattedTextStyle(
                    color = FormattedTextColor(
                        absoluteColor = it
                    )
                )
            }
        }
    }
}
package ru.tensor.sbis.widget_player.converter

import ru.tensor.sbis.widget_player.converter.attributes.store.AttributesStore
import ru.tensor.sbis.widget_player.converter.style.FormattedTextStyle
import ru.tensor.sbis.widget_player.widget.text.FormattedTextElement

/**
 * @author am.boldinov
 */
interface FormattedTextConverter {

    fun convertRichText(source: String, builder: FormattedTextStyleBuilder): FormattedTextElement

    fun convert(source: String): FormattedTextElement
}

fun interface FormattedTextStyleBuilder {

    fun build(tag: String, attributes: AttributesStore): FormattedTextStyle?
}

class FormattedTextStyleCompositeBuilder(
    vararg val source: FormattedTextStyleBuilder
) : FormattedTextStyleBuilder {

    override fun build(tag: String, attributes: AttributesStore): FormattedTextStyle? {
        source.forEach {
            val style = it.build(tag, attributes)
            if (style != null) {
                return style
            }
        }
        return null
    }
}

abstract class FormattedTextSpanClassBuilder : FormattedTextStyleBuilder {

    final override fun build(tag: String, attributes: AttributesStore): FormattedTextStyle? {
        return tag.takeIf { it == "span" }?.let {
            attributes.get("class")?.let { value ->
                build(value)
            }
        }
    }

    protected abstract fun build(value: String): FormattedTextStyle?
}
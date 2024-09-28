package ru.tensor.sbis.widget_player.converter.internal

import ru.tensor.sbis.jsonconverter.generated.RichTextHandler
import ru.tensor.sbis.jsonconverter.generated.RichTextParser
import ru.tensor.sbis.widget_player.converter.FormattedTextConverter
import ru.tensor.sbis.widget_player.converter.FormattedTextStyleBuilder
import ru.tensor.sbis.widget_player.converter.attributes.store.MapAttributesStore
import ru.tensor.sbis.widget_player.converter.style.FormattedTextAttributes
import ru.tensor.sbis.widget_player.converter.style.FormattedTextStyle
import ru.tensor.sbis.widget_player.converter.style.FormattedTextStyleRange
import ru.tensor.sbis.widget_player.widget.text.FormattedTextElement
import java.util.LinkedList

/**
 * @author am.boldinov
 */
internal class FormattedTextJsonConverter(
    private val factory: () -> FormattedTextElement
) : FormattedTextConverter {

    override fun convertRichText(source: String, builder: FormattedTextStyleBuilder): FormattedTextElement {
        val stream = StringBuilder()
        val rangeStyleList = mutableListOf<FormattedTextStyleRange>()
        val rangeStream = LinkedList<FormattedTextStyleRange>()
        val rtHandler = object : RichTextHandler() {

            override fun onElementBegin(type: String, value: String, attributes: HashMap<String, String>): Boolean {
                if (type == ConverterParams.Type.TAG) {
                    val range = builder.build(value, MapAttributesStore(attributes))?.let {
                        FormattedTextStyleRange(
                            location = stream.length,
                            length = 0,
                            style = it
                        )
                    } ?: STUB_FORMATTED_STYLE_RANGE
                    rangeStream.add(range)
                } else if (type == ConverterParams.Type.TEXT) {
                    stream.append(value)
                }
                return true
            }

            override fun onElementEnd(type: String, value: String): Boolean {
                if (type == ConverterParams.Type.TAG) {
                    rangeStream.pollLast()?.takeIf {
                        it !== STUB_FORMATTED_STYLE_RANGE
                    }?.let {
                        rangeStyleList.add(
                            it.copy(
                                length = stream.length - it.location
                            )
                        )
                    }
                }
                return true
            }

            override fun onAttribute(key: String, value: String) = false
        }
        RichTextParser.create(rtHandler, true).parse(source)
        return factory.invoke().apply {
            text = stream.toString()
            textAttributes = FormattedTextAttributes(rangeStyleList)
        }
    }

    override fun convert(source: String): FormattedTextElement {
        return factory.invoke().apply {
            text = source
        }
    }

    private companion object {

        private val STUB_FORMATTED_STYLE_RANGE = FormattedTextStyleRange(0, 0, FormattedTextStyle())
    }

}
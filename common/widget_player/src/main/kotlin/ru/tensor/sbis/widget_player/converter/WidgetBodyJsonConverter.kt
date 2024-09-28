package ru.tensor.sbis.widget_player.converter

import ru.tensor.sbis.jsonconverter.generated.RichTextHandler
import ru.tensor.sbis.widget_player.config.WidgetConfiguration
import ru.tensor.sbis.widget_player.converter.element.TextElement
import ru.tensor.sbis.widget_player.converter.attributes.WidgetMapAttributes
import ru.tensor.sbis.widget_player.converter.element.GroupWidgetElement
import ru.tensor.sbis.widget_player.converter.element.TextWrapperElement
import ru.tensor.sbis.widget_player.converter.internal.ConverterParams
import ru.tensor.sbis.widget_player.converter.internal.ElementTreeBuilder
import ru.tensor.sbis.widget_player.converter.internal.FormattedTextAttributesBridge
import ru.tensor.sbis.widget_player.converter.internal.FormattedTextJsonConverter
import ru.tensor.sbis.widget_player.converter.internal.HeaderAttributesBuffer
import ru.tensor.sbis.widget_player.converter.internal.HeaderAttributesHandler
import ru.tensor.sbis.widget_player.converter.style.StyleProperties
import ru.tensor.sbis.widget_player.widget.text.FormattedTextElement
import timber.log.Timber

/**
 * @author am.boldinov
 */
class WidgetBodyJsonConverter(
    private val configuration: WidgetConfiguration
) {

    private val widgetStore get() = configuration.widgetStore
    private val textWidget get() = ConverterParams.ReservedTag.TEXT_WIDGET

    private val builder = ElementTreeBuilder()

    private val environment = WidgetEnvironment(
        context = configuration.context,
        resources = with(configuration.options) {
            WidgetResources(
                globalStyle = StyleProperties(
                    fontSize = textOptions.textSize,
                    textColor = textOptions.textColor,
                    linkTextColor = textOptions.linkTextColor
                )
            )
        },
        textConverter = FormattedTextJsonConverter {
            buildFormattedTextElement()
        }
    )

    private val childrenFactoryStore = mutableMapOf<String, WidgetElementFactory<*>>()

    private val headerAttributesHandler = HeaderAttributesHandler()

    private val rtHandler = object : RichTextHandler() {

        private val headerBuffer = HeaderAttributesBuffer()

        override fun onElementBegin(type: String, value: String, attributes: HashMap<String, String>): Boolean {
            if (type == ConverterParams.Type.TAG) {
                val factory = widgetStore.get(value)?.elementFactory ?: childrenFactoryStore[value.lowercase()]
                if (factory is TreeElementFactory<*>) {
                    childrenFactoryStore.putAll(factory.store)
                }
                factory?.let {
                    headerAttributesHandler.replaceMetaAttributes(value, attributes, headerBuffer)
                    try {
                        val element = it.create(
                            value,
                            WidgetMapAttributes(map = attributes, resource = headerBuffer.resource),
                            environment
                        )
                        builder.beginElement(element)
                    } catch (e: Exception) {
                        Timber.e(e)
                    } finally {
                        headerBuffer.clear()
                    }
                }
            } else if (type == ConverterParams.Type.TEXT) {
                tryGetTextElement()?.text = value
            }
            return true
        }

        override fun onElementEnd(type: String, value: String): Boolean {
            if (type == ConverterParams.Type.TAG) {
                val factory = widgetStore.get(value)?.elementFactory ?: childrenFactoryStore[value.lowercase()]
                if (factory != null) {
                    builder.commitElement()
                }
            }
            return true
        }

        override fun onAttribute(key: String, value: String): Boolean {
            return false
        }
    }

    private val formattedTextHandler = FormattedTextAttributesBridge {
        getTextElement()?.textAttributes = this
    }

    @Synchronized
    fun convert(parser: WidgetBodySaxParser): WidgetBody {
        try {
            parser.parse(
                handler = rtHandler,
                formattedTextAttributesHandler = formattedTextHandler,
                frameHeaderAttributesHandler = headerAttributesHandler,
                aggregateAttributes = true
            )
        } catch (e: Exception) {
            Timber.e(e)
        }
        val body = try {
            WidgetBody(builder.build(), widgetStore)
        } catch (e: Exception) {
            Timber.e(e)
            buildErrorBody()
        } finally {
            headerAttributesHandler.release()
        }
        return body
    }

    /**
     * Возвращает тело документа с элеметом-заглушкой (пустой документ).
     */
    fun buildErrorBody(): WidgetBody {
        return WidgetBody(
            ElementTree(
                root = GroupWidgetElement(
                    ConverterParams.ReservedTag.FRAME_ERROR,
                    WidgetMapAttributes.EMPTY,
                    environment.resources
                )
            ), widgetStore
        )
    }

    private fun getTextElement(): TextElement? {
        return builder.element(TextElement::class.java)
    }

    private fun tryGetTextElement(): TextElement? {
        return getTextElement() ?: run {
            builder.element(GroupWidgetElement::class.java)?.takeIf {
                it is TextWrapperElement
            }?.let {
                rtHandler.onElementBegin(ConverterParams.Type.TAG, textWidget, ConverterParams.EMPTY_ATTRIBUTES)
                val element = getTextElement()
                rtHandler.onElementEnd(ConverterParams.Type.TAG, textWidget)
                element
            }
        }
    }

    private fun buildFormattedTextElement(): FormattedTextElement {
        return widgetStore.get(textWidget)!!.elementFactory.create(
            textWidget,
            WidgetMapAttributes.EMPTY,
            environment
        ) as FormattedTextElement
    }
}
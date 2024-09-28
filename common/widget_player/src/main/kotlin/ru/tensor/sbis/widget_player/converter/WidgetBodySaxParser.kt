package ru.tensor.sbis.widget_player.converter

import ru.tensor.sbis.jsonconverter.generated.RichTextHandler
import ru.tensor.sbis.jsonconverter.generated.SabyDocMteFormattedTextAttributesHandler
import ru.tensor.sbis.jsonconverter.generated.SabyDocMteFrameHeaderAttributesHandler
import ru.tensor.sbis.widget_player.converter.attributes.WidgetAttributes
import ru.tensor.sbis.widget_player.converter.internal.ConverterParams

/**
 * @author am.boldinov
 */
fun interface WidgetBodySaxParser {

    fun parse(
        handler: RichTextHandler,
        formattedTextAttributesHandler: SabyDocMteFormattedTextAttributesHandler,
        frameHeaderAttributesHandler: SabyDocMteFrameHeaderAttributesHandler,
        aggregateAttributes: Boolean
    )
}

fun RichTextHandler.beginFrame(id: String) {
    onElementBegin(
        ConverterParams.Type.TAG,
        ConverterParams.ReservedTag.FRAME_ROOT,
        hashMapOf(WidgetAttributes.ID to id)
    )
}

fun RichTextHandler.endFrame() {
    onElementEnd(ConverterParams.Type.TAG, ConverterParams.ReservedTag.FRAME_ROOT)
}

fun RichTextHandler.beginWidget(tag: String, attributes: HashMap<String, String>) {
    onElementBegin(ConverterParams.Type.TAG, tag, attributes)
}

fun RichTextHandler.beginTextWidget(id: String) {
    onElementBegin(
        ConverterParams.Type.TAG,
        ConverterParams.ReservedTag.TEXT_WIDGET,
        hashMapOf(WidgetAttributes.ID to id)
    )
}

fun RichTextHandler.beginText(text: String) {
    onElementBegin(
        ConverterParams.Type.TEXT,
        text,
        ConverterParams.EMPTY_ATTRIBUTES
    )
}

fun RichTextHandler.endWidget(tag: String) {
    onElementEnd(ConverterParams.Type.TAG, tag)
}
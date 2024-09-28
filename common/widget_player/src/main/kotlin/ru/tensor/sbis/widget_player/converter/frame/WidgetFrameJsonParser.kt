package ru.tensor.sbis.widget_player.converter.frame

import ru.tensor.sbis.jsonconverter.generated.RichTextHandler
import ru.tensor.sbis.jsonconverter.generated.SabyDocMteFormattedTextAttributesHandler
import ru.tensor.sbis.jsonconverter.generated.SabyDocMteFrameHeaderAttributesHandler
import ru.tensor.sbis.jsonconverter.generated.SabyDocMteParser
import ru.tensor.sbis.widget_player.converter.WidgetBodySaxParser

/**
 * @author am.boldinov
 */
internal class WidgetFrameJsonParser(
    private val json: String
) : WidgetBodySaxParser {

    override fun parse(
        handler: RichTextHandler,
        formattedTextAttributesHandler: SabyDocMteFormattedTextAttributesHandler,
        frameHeaderAttributesHandler: SabyDocMteFrameHeaderAttributesHandler,
        aggregateAttributes: Boolean
    ) {
        SabyDocMteParser.create(
            handler,
            formattedTextAttributesHandler,
            frameHeaderAttributesHandler,
            true
        ).parse(json)
    }
}
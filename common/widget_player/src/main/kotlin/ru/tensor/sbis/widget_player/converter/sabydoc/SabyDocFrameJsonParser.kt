package ru.tensor.sbis.widget_player.converter.sabydoc

import ru.tensor.sbis.jsonconverter.generated.RichTextHandler
import ru.tensor.sbis.jsonconverter.generated.SabyDocMetaHandler
import ru.tensor.sbis.jsonconverter.generated.SabyDocMteFormattedTextAttributesHandler
import ru.tensor.sbis.jsonconverter.generated.SabyDocMteFrameHeaderAttributesHandler
import ru.tensor.sbis.jsonconverter.generated.SabyDocParser
import ru.tensor.sbis.widget_player.converter.WidgetBodySaxParser

/**
 * @author am.boldinov
 */
internal class SabyDocFrameJsonParser(
    private val json: String,
    private val metaHandler: SabyDocMetaHandler
) : WidgetBodySaxParser {

    override fun parse(
        handler: RichTextHandler,
        formattedTextAttributesHandler: SabyDocMteFormattedTextAttributesHandler,
        frameHeaderAttributesHandler: SabyDocMteFrameHeaderAttributesHandler,
        aggregateAttributes: Boolean
    ) {
        SabyDocParser.createWithMte(
            handler,
            metaHandler,
            formattedTextAttributesHandler,
            frameHeaderAttributesHandler
        ).parse(json)
    }
}
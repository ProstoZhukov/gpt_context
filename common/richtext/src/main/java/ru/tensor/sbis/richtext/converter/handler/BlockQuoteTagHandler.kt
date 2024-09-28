package ru.tensor.sbis.richtext.converter.handler

import android.text.Editable
import ru.tensor.sbis.common.util.UUIDUtils
import ru.tensor.sbis.plugin_struct.utils.SbisThemedContext
import ru.tensor.sbis.richtext.converter.MarkSpan.BlockQuote
import ru.tensor.sbis.richtext.converter.TagAttributes
import ru.tensor.sbis.richtext.converter.cfg.BlockQuoteSpanConfiguration
import ru.tensor.sbis.richtext.converter.handler.base.MarkedTagHandler
import ru.tensor.sbis.richtext.span.BlockQuoteData
import ru.tensor.sbis.richtext.util.HtmlHelper

/**
 * Обработчик тегов цитат: blockquote
 *
 * @author am.boldinov
 */
class BlockQuoteTagHandler(
    context: SbisThemedContext,
    configuration: BlockQuoteSpanConfiguration
) : MarkedTagHandler() {

    private val style by lazy(LazyThreadSafetyMode.NONE) {
        BlockQuoteStyle(context, configuration)
    }

    override fun onStartTag(stream: Editable, attributes: TagAttributes) {
        appendVerticalPadding(stream)
        val messageUuid = UUIDUtils.fromString(attributes.getValue("data-msg-id"))
        val senderUuid = UUIDUtils.fromString(attributes.getValue("data-msg-sender"))
        val messageTheme = UUIDUtils.fromString(attributes.getValue("data-msg-theme"))
        val data = if (messageUuid != null && messageTheme != null && senderUuid != null) {
            BlockQuoteData(
                messageUuid = messageUuid,
                messageTheme = messageTheme,
                senderUuid = senderUuid
            )
        } else {
            null
        }
        mark(stream, BlockQuote(style.lineWidth, style.lineColor, data))
    }

    override fun onEndTag(stream: Editable) {
        val result = span(stream, BlockQuote::class.java)
        if (result) {
            appendVerticalPadding(stream)
        }
    }

    override fun recycle() {
        // ignore
    }

    private fun appendVerticalPadding(stream: Editable) {
        HtmlHelper.appendLineBreakHeight(stream, style.verticalPadding)
    }

    private class BlockQuoteStyle(
        context: SbisThemedContext,
        configuration: BlockQuoteSpanConfiguration
    ) {

        val verticalPadding = configuration.verticalPadding.getDimenPx(context)
        val lineWidth = configuration.lineWidth.getDimen(context)
        val lineColor = configuration.lineColor.getColor(context)
    }
}

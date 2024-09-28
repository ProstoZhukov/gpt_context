package ru.tensor.sbis.communicator.base.conversation.qoute_tag_handler

import android.content.Context
import android.text.Editable
import ru.tensor.sbis.richtext.converter.TagAttributes
import ru.tensor.sbis.richtext.converter.handler.view.ContentViewTagHandler
import ru.tensor.sbis.richtext.span.view.BaseAttributesVM

/**
 * Обработчик тегов цитат: blockquote.
 * Цитата сокращается до 2 строк.
 *
 * @author da.zhukov
 */
class QuoteTagHandler(
    context: Context
) : ContentViewTagHandler(context) {

    override fun onStartTag(stream: Editable, attributes: TagAttributes) {
        super.onStartTag(stream, attributes)
        startContent(stream)
    }

    override fun onEndTag(stream: Editable) {
        val content = stopContent(stream)
        currentVM?.let { if (it is QuoteAttributesVM) it.setContent(content) }
        super.onEndTag(stream)
    }

    override fun createAttributesVM(attributes: TagAttributes): BaseAttributesVM =
        QuoteAttributesVM(attributes.tag)
}
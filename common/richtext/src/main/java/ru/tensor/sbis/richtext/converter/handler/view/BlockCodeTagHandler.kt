package ru.tensor.sbis.richtext.converter.handler.view

import android.text.Editable
import ru.tensor.sbis.plugin_struct.utils.SbisThemedContext
import ru.tensor.sbis.richtext.converter.MarkSpan
import ru.tensor.sbis.richtext.converter.TagAttributes

/**
 * Обработчик тега с блоком кода.
 *
 * @author am.boldinov
 */
class BlockCodeTagHandler(context: SbisThemedContext) : BlockTagHandler(context) {

    override fun onStartTag(stream: Editable, attributes: TagAttributes) {
        super.onStartTag(stream, attributes)
        mark(stream, MarkSpan.Mono(mContext))
    }

    override fun onEndTag(stream: Editable) {
        span(stream, MarkSpan.Mono::class.java)
        super.onEndTag(stream)
    }
}
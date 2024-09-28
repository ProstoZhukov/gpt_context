package ru.tensor.sbis.richtext.converter.handler.base

import android.text.Editable
import org.apache.commons.lang3.StringUtils
import ru.tensor.sbis.richtext.converter.MarkSpan
import ru.tensor.sbis.richtext.converter.TagAttributes
import ru.tensor.sbis.richtext.util.SpannableUtil

/**
 * Обработчик тега, который игнорирует весь вложенный текст и не добавляет его к результату.
 *
 * @author am.boldinov
 */
internal class IgnoreTextHandler : MarkedTagHandler() {

    override fun onStartTag(stream: Editable, attributes: TagAttributes) {
        mark(stream, IgnoreTextSpan())
    }

    override fun onEndTag(stream: Editable) {
        SpannableUtil.getLast(stream, IgnoreTextSpan::class.java)?.let { last ->
            val where = stream.getSpanStart(last)
            stream.removeSpan(last)
            val len = stream.length
            if (where in 0 until len) {
                stream.replace(where, len, StringUtils.EMPTY)
            }
        }
    }

    override fun recycle() {
        // ignore
    }

    private class IgnoreTextSpan : MarkSpan() {
        override fun getRealSpan() = this
    }
}
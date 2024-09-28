package ru.tensor.sbis.richtext.converter.handler

import android.text.Editable
import org.apache.commons.lang3.CharUtils
import org.apache.commons.lang3.StringUtils
import ru.tensor.sbis.richtext.converter.MarkSpan
import ru.tensor.sbis.richtext.converter.TagAttributes
import ru.tensor.sbis.richtext.converter.cfg.BrConfiguration
import ru.tensor.sbis.richtext.converter.cfg.RenderOptions
import ru.tensor.sbis.richtext.converter.handler.base.MarkedTagHandler
import ru.tensor.sbis.richtext.converter.handler.postprocessor.SpanPostprocessor
import ru.tensor.sbis.richtext.span.view.ViewStubSpan
import ru.tensor.sbis.richtext.util.HtmlHelper
import ru.tensor.sbis.richtext.util.SpannableUtil
import ru.tensor.sbis.richtext.view.strategy.SpannableLineBreakHandler

/**
 * Обработчик тегов "p"
 *
 * @author am.boldinov
 */
internal class ParagraphTagHandler(
    private val configuration: BrConfiguration,
    private val renderOptions: RenderOptions
) : MarkedTagHandler(), SpanPostprocessor {

    override fun onStartTag(stream: Editable, attributes: TagAttributes) {
        mark(stream, MarkSpan.Paragraph())
    }

    override fun onEndTag(stream: Editable) {
        val last = SpannableUtil.getLast(stream, MarkSpan.Paragraph::class.java)
        if (last != null) {
            if (last.isHandled) {
                stream.removeSpan(last)
            } else {
                SpannableUtil.setSpanFromMark(stream, last, last)
                if (!isInlineParagraph(stream, last)) {
                    if (renderOptions.isDrawWrappedImages) {
                        stream.append(StringUtils.LF)
                    } else {
                        HtmlHelper.appendLineBreakIgnoreSpace(stream, configuration.maxLineBreakCount)
                    }
                }
            }
        }
    }

    override fun recycle() {

    }

    override fun process(text: Editable) {
        if (text.isEmpty()) {
            return
        }
        if (text.length > 1) {
            deleteLastLF(text) // всегда удаляем последний перенос строки, если он не единственный
        }
        // если нет обтекания или в тексте отсутствуют вьюхи
        if (!renderOptions.isDrawWrappedImages || text.getSpans(0, text.length, ViewStubSpan::class.java).isEmpty()) {
            // сначала удаляем переносы с конца
            while (text.isNotEmpty()) {
                if (deleteLastLF(text)) {
                    continue
                }
                break
            }
            if (renderOptions.isDrawWrappedImages) {
                SpannableLineBreakHandler().removeEmptyLineBreaks(text, configuration.maxLineBreakCount)
            }
        }
    }

    /**
     * @return true если содержимое параграфа является переносом строки
     */
    private fun isInlineParagraph(stream: Editable, span: MarkSpan.Paragraph): Boolean {
        val start = stream.getSpanStart(span)
        return start >= 0 && start == stream.length - 1 && stream[start] == CharUtils.LF
    }

    private fun deleteLastLF(text: Editable): Boolean {
        val endPosition = text.length
        val startPosition = endPosition - 1
        if (text[startPosition] == CharUtils.LF) {
            text.delete(startPosition, endPosition)
            return true
        }
        return false
    }
}
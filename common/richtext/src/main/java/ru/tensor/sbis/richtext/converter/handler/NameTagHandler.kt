package ru.tensor.sbis.richtext.converter.handler

import android.text.Editable
import ru.tensor.sbis.richtext.converter.MarkSpan
import ru.tensor.sbis.richtext.converter.TagAttributes
import ru.tensor.sbis.richtext.converter.css.CssClassSpanConverter
import ru.tensor.sbis.richtext.converter.handler.base.SimpleCollectionMarkedTagHandler
import ru.tensor.sbis.richtext.util.HtmlHelper
import ru.tensor.sbis.richtext.R

/**
 * Обработчик тега с названием файла (документа) [ru.tensor.sbis.richtext.util.HtmlTag.SabyDoc.NAME].
 * Выводится в качестве заголовка документа.
 *
 * @author am.boldinov
 */
internal class NameTagHandler(
    private val spanConverter: CssClassSpanConverter
) : SimpleCollectionMarkedTagHandler() {

    override fun createSpanCollection(attributes: TagAttributes): MutableList<MarkSpan> {
        return spanConverter.convert(R.style.RichTextTitleStyle_Name)
    }

    override fun onEndTag(stream: Editable) {
        super.onEndTag(stream)
        HtmlHelper.appendLineBreak(stream, 0)
        HtmlHelper.appendLineBreak(stream, 0)
    }
}
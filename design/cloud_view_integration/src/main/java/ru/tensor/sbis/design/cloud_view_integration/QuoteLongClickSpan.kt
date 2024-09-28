package ru.tensor.sbis.design.cloud_view_integration

import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View
import ru.tensor.sbis.design.cloud_view.content.quote.QuoteClickListener
import ru.tensor.sbis.richtext.span.LongClickSpan
import java.util.*

/**
 * Реализация ClickableSpan для обработки нажатий на цитаты
 *
 * @author ma.kolpakov
 */
internal class QuoteLongClickSpan(
    private val enclosingMessageUuid: UUID,
    private val quotedMessageUuid: UUID,
    private val listener: QuoteClickListener
) : ClickableSpan(), LongClickSpan {
    override fun onClick(widget: View) {
        listener.onQuoteClicked(quotedMessageUuid)
    }

    override fun onLongClick(widget: View) {
        listener.onQuoteLongClicked(enclosingMessageUuid)
    }

    override fun updateDrawState(ds: TextPaint) {
        // оставить пустым, чтоб спан не переопределял стили
    }
}
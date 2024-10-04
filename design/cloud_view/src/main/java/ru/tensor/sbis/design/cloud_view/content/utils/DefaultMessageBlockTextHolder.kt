package ru.tensor.sbis.design.cloud_view.content.utils

import android.content.Context
import android.text.Spannable
import android.view.View
import android.widget.TextView
import ru.tensor.sbis.design.cloud_view.content.quote.QuoteClickSpan
import ru.tensor.sbis.design.cloud_view.model.QuoteCloudContent
import ru.tensor.sbis.design.utils.checkNotNullSafe
import ru.tensor.sbis.design.R as RDesign

/**
 * Дефолтная реализация [MessageBlockTextHolder], использующая TextView для текста
 *
 * @author da.zolotarev
 */
class DefaultMessageBlockTextHolder : MessageBlockTextHolder {

    /** @SelfDocumented*/
    var textView: TextView? = null

    override fun getTextView(context: Context): TextView {
        return textView ?: run {
            val buffTextView = TextView(context, null, RDesign.style.MessagesListItem_RegularText)
            textView = buffTextView
            buffTextView
        }
    }

    override fun getTextLayoutView(context: Context): View = getTextView(context)

    override fun setText(message: Spannable?) {
        checkNotNullSafe(textView) { "TextView not initialized" }
        textView?.apply { text = message }
    }

    override fun setQuoteClickSpan(message: Spannable, quoteContentList: List<QuoteCloudContent>) {
        val quoteContent = quoteContentList.firstOrNull() ?: return
        val textSpans = message.getSpans(0, message.length, Any::class.java)
            .takeIf { it.isNotEmpty() }
            ?: return
        val quote = quoteContent.quote
        message.setSpan(
            QuoteClickSpan(quote.messageUuid, quoteContent.listener),
            message.getSpanStart(textSpans[0]),
            message.getSpanEnd(textSpans[0]),
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }
}

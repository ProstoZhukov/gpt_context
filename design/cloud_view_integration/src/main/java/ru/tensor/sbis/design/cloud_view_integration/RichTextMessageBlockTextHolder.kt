package ru.tensor.sbis.design.cloud_view_integration

import android.content.Context
import android.text.Spannable
import android.text.Spanned
import android.widget.TextView
import ru.tensor.sbis.design.R
import ru.tensor.sbis.design.cloud_view.content.phone_number.PhoneNumberClickListener
import ru.tensor.sbis.design.cloud_view.content.utils.MessageBlockTextHolder
import ru.tensor.sbis.design.cloud_view.model.QuoteCloudContent
import ru.tensor.sbis.richtext.span.BlockQuoteSpan
import ru.tensor.sbis.richtext.span.LinkUrlSpan
import ru.tensor.sbis.richtext.span.PrioritySpan
import ru.tensor.sbis.richtext.view.RichTextView
import ru.tensor.sbis.richtext.view.RichViewLayout

/**
 * Реализация [MessageBlockTextHolder], используюшая [RichTextView]
 *
 * @author da.zolotarev
 */
class RichTextMessageBlockTextHolder : MessageBlockTextHolder {

    private var textLayout: RichViewLayout? = null

    override fun getTextView(context: Context): TextView =
        getTextLayoutView(context).textView

    override fun getTextLayoutView(context: Context): RichViewLayout =
        textLayout ?: run {
            val buffRichViewLayout =
                RichViewLayout(context, null, R.style.MessagesListItem_RegularText)
            textLayout = buffRichViewLayout
            buffRichViewLayout
        }

    override fun setText(message: Spannable?) {
        checkNotNull(textLayout) {
            "TextLayout not initialized. You should call getTextLayoutView() before this method"
        }
        textLayout?.setText(message)
    }

    override fun setQuoteClickSpan(message: Spannable, quoteContentList: List<QuoteCloudContent>) {
        val quoteSpans = message.getSpans(0, message.length, BlockQuoteSpan::class.java)
        quoteContentList.forEachIndexed { index, quoteContent ->
            val quoteSpan = quoteSpans.getOrNull(index) ?: return
            val quote = quoteContent.quote
            message.setSpan(
                QuoteLongClickSpan(quote.enclosingMessageUuid, quote.messageUuid, quoteContent.listener),
                message.getSpanStart(quoteSpan),
                message.getSpanEnd(quoteSpan),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        message.getSpans(0, message.length, LinkUrlSpan::class.java).forEach { linkSpan ->
            if (linkSpan.priority == PrioritySpan.MIN_PRIORITY) {
                val start = message.getSpanStart(linkSpan)
                val end = message.getSpanEnd(linkSpan)
                val topPriority = PrioritySpan.USER_PRIORITY shl Spanned.SPAN_PRIORITY_SHIFT
                val flags = Spannable.SPAN_EXCLUSIVE_EXCLUSIVE or topPriority
                message.setSpan(linkSpan, start, end, flags)
            }
        }
    }

    override fun setPhoneClickSpan(message: Spannable, listener: PhoneNumberClickListener) {
        val linkSpans = message.getSpans(0, message.length, LinkUrlSpan::class.java)
        linkSpans.forEach { linkSpan ->
            if (linkSpan.url.contains(TEL_URL_PREFIX)) {
                val start = message.getSpanStart(linkSpan)
                val end = message.getSpanEnd(linkSpan)
                val topPriority = PrioritySpan.USER_PRIORITY shl Spanned.SPAN_PRIORITY_SHIFT
                val flags = Spannable.SPAN_EXCLUSIVE_EXCLUSIVE or topPriority
                message.removeSpan(linkSpan)
                message.setSpan(
                    PhoneNumberLongClickSpan(linkSpan.url, listener),
                    start,
                    end,
                    flags
                )
            }
        }
    }
}

private const val TEL_URL_PREFIX = "tel:"
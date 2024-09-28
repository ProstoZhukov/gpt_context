package ru.tensor.sbis.design.message_view.content.service_views

import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View
import org.apache.commons.lang3.builder.EqualsBuilder
import org.apache.commons.lang3.builder.HashCodeBuilder

/**
 * Кликабельный Spannable для сервисных сообщений.
 *
 * @author dv.baranov
 */
class ChatServiceMessageClickableSpan(private val withColor: Boolean) : ClickableSpan() {

    /** @SelfDocumented */
    var callback: ChatSpanCallback? = null

    /** @SelfDocumented */
    fun removeCallback(callback: ChatSpanCallback) {
        if (this.callback === callback) {
            this.callback = null
        }
    }

    /** @SelfDocumented */
    override fun onClick(widget: View) {
        callback?.onClick()
    }

    /** @SelfDocumented */
    override fun updateDrawState(ds: TextPaint) {
        if (withColor) {
            super.updateDrawState(ds)
            ds.isUnderlineText = false
        }
    }

    override fun equals(other: Any?): Boolean =
        other is ChatServiceMessageClickableSpan &&
            EqualsBuilder()
                .append(withColor, other.withColor)
                .append(callback, other.callback)
                .isEquals

    override fun hashCode(): Int =
        HashCodeBuilder()
            .append(withColor)
            .append(callback)
            .toHashCode()
}

/** Слушатель клика по Span'у */
interface ChatSpanCallback {

    /** @SelfDocumented */
    fun onClick()

}
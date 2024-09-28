package ru.tensor.sbis.communicator.base.conversation.qoute_tag_handler

import android.text.Spannable
import ru.tensor.sbis.richtext.span.view.ContentAttributesVM
import ru.tensor.sbis.richtext.view.RichViewLayout
import ru.tensor.sbis.richtext.view.RichViewLayout.ViewHolderFactory
import ru.tensor.sbis.richtext.view.RichViewParent

/**
 * Вью-модель атрибутов цитаты.
 *
 * @author da.zhukov
 */
internal class QuoteAttributesVM(
    tag: String,
) : ContentAttributesVM(tag) {

    private lateinit var content: Spannable

    override fun size(): Int = 0

    override fun getContent(index: Int): Spannable = content

    /**
     * Устанавливает стилизованный контент цитаты.
     */
    fun setContent(content: Spannable) {
        this.content = content
    }

    override fun createViewHolderFactory(): ViewHolderFactory =
        ViewHolderFactory { parent: RichViewParent ->
            val layout = QuoteViewLayout(parent.context, parent.richViewFactory)
            layout.layoutParams = RichViewLayout.LayoutParams(
                RichViewLayout.LayoutParams.MATCH_PARENT,
                RichViewLayout.LayoutParams.WRAP_CONTENT
            )
            QuoteViewHolder(layout)
        }

    private class QuoteViewHolder(
        val quoteViewLayout: QuoteViewLayout
    ) : RichViewLayout.ViewHolder<QuoteAttributesVM>(quoteViewLayout) {

        override fun bind(attributesVM: QuoteAttributesVM) {
            quoteViewLayout.setContent(attributesVM.content)
        }

        override fun onRecycle() {
            super.onRecycle()
            quoteViewLayout.recycle()
        }
    }
}
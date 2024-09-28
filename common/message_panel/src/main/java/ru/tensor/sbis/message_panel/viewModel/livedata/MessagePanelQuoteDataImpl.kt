package ru.tensor.sbis.message_panel.viewModel.livedata

import io.reactivex.subjects.BehaviorSubject
import org.apache.commons.lang3.StringUtils
import ru.tensor.sbis.design.message_panel.decl.quote.MessagePanelQuote

/**
 * @author vv.chekurda
 * Создан 8/16/2019
 */
internal class MessagePanelQuoteDataImpl : MessagePanelQuoteData {
    override val originalMessageTitle = BehaviorSubject.create<CharSequence>()
    override val originalMessageSubtitle = BehaviorSubject.create<CharSequence>()
    override val originalMessageText = BehaviorSubject.create<CharSequence>()
    override val quotePanelVisible = BehaviorSubject.createDefault(false)
    override val quoteData = BehaviorSubject.createDefault(
        MessagePanelQuote(StringUtils.EMPTY, StringUtils.EMPTY)
    )

    override fun setQuoteText(
        title: CharSequence,
        subtitle: CharSequence,
        text: CharSequence
    ) {
        originalMessageTitle.onNext(title)
        originalMessageSubtitle.onNext(subtitle)
        originalMessageText.onNext(text)
        quoteData.onNext(MessagePanelQuote(title.toString(), text.toString()))
        setQuoteVisibility(true)
    }

    override fun setQuoteVisibility(visible: Boolean) {
        quotePanelVisible.onNext(visible)
    }
}
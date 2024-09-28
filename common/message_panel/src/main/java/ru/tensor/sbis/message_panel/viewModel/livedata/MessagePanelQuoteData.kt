package ru.tensor.sbis.message_panel.viewModel.livedata

import io.reactivex.Observable
import ru.tensor.sbis.design.message_panel.decl.quote.MessagePanelQuote

/**
 * Модель данных цитаты
 *
 * @author vv.chekurda
 * @since 7/16/2019
 */
@Deprecated("https://online.sbis.ru/opendoc.html?guid=bb1754f3-4936-4641-bdc2-beec53070c4b")
interface MessagePanelQuoteData {
    val originalMessageTitle: Observable<CharSequence>
    val originalMessageSubtitle: Observable<CharSequence>
    val originalMessageText: Observable<CharSequence>
    val quotePanelVisible: Observable<Boolean>
    val quoteData: Observable<MessagePanelQuote>

    fun setQuoteText(
        title: CharSequence,
        subtitle: CharSequence,
        text: CharSequence = subtitle,
    )
    fun setQuoteVisibility(visible: Boolean)
}
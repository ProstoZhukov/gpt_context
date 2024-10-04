package ru.tensor.sbis.design.message_panel.vm.quote

import kotlinx.coroutines.CoroutineScope
import ru.tensor.sbis.design.message_panel.decl.quote.MessagePanelQuote

/**
 * Внутренний API для управления цитатой/блоком редактирования панели ввода
 *
 * @author ma.kolpakov
 */
internal interface QuoteDelegate : MessagePanelQuoteApi {

    fun attachQuoteScope(scope: CoroutineScope)

    fun setQuote(quoteInfo: MessagePanelQuote?)

    fun clearQuote()
}

package ru.tensor.sbis.design.message_panel.vm.quote

import kotlinx.coroutines.flow.StateFlow
import ru.tensor.sbis.design.message_panel.decl.quote.MessagePanelQuote

/**
 * Публичный API для управления цитатой/блоком редактирования панели ввода
 *
 * @author ma.kolpakov
 */
interface MessagePanelQuoteApi {

    val quote: StateFlow<MessagePanelQuote?>

    val quoteVisible: StateFlow<Boolean>

    fun onQuoteClearClicked()
}
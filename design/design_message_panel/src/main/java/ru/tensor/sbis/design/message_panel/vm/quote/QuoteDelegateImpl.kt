package ru.tensor.sbis.design.message_panel.vm.quote

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import ru.tensor.sbis.design.message_panel.decl.quote.MessagePanelQuote
import javax.inject.Inject

/**
 * @author ma.kolpakov
 */
internal class QuoteDelegateImpl @Inject constructor() : QuoteDelegate {

    override val quote = MutableStateFlow<MessagePanelQuote?>(null)

    override lateinit var quoteVisible: StateFlow<Boolean>
        private set

    override fun attachQuoteScope(scope: CoroutineScope) {
        quoteVisible = quote.map { it != null }
            .stateIn(scope, SharingStarted.Eagerly, false)
    }

    override fun setQuote(quoteInfo: MessagePanelQuote?) {
        quote.value = quoteInfo
    }

    override fun onQuoteClearClicked() =
        clearQuote()


    override fun clearQuote() {
        setQuote(null)
    }
}

package ru.tensor.sbis.design.message_panel.domain

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.tensor.sbis.design.message_panel.decl.env.MessagePanelEnvironmentModel
import ru.tensor.sbis.design.message_panel.decl.quote.MessagePanelQuote
import ru.tensor.sbis.design.message_panel.di.usecase.MessagePanelUseCaseComponent
import ru.tensor.sbis.design.message_panel.domain.common.CleanupUseCase
import ru.tensor.sbis.design.message_panel.domain.common.DraftUseCase
import ru.tensor.sbis.design.message_panel.domain.common.SendUseCase
import ru.tensor.sbis.design.message_panel.vm.MessagePanelViewModel
import java.util.*
import kotlin.coroutines.CoroutineContext

/**
 * Сценарий работы панели ввода для цитирования
 *
 * @author ma.kolpakov
 */
class QuoteMessageUseCase internal constructor(
    val quotingMessageUuid: UUID,
    val quoteTitle: String,
    val quoteText: String,
    environment: MessagePanelEnvironmentModel,
    private val useCaseComponent: MessagePanelUseCaseComponent.Factory,
    private val dispatcher: CoroutineContext = Dispatchers.IO
) : AbstractMessagePanelUseCase(environment) {

    private lateinit var sendMessage: SendUseCase<*, *>
    private lateinit var draft: DraftUseCase<*>
    private lateinit var cleanup: CleanupUseCase

    override suspend fun setup(vm: MessagePanelViewModel) = withContext(dispatcher) {
        with(useCaseComponent.create(this@QuoteMessageUseCase, vm)) {
            sendMessage = sendUseCaseProvider.get()
            draft = draftUseCaseProvider.get()
            cleanup = cleanupUseCase.get()
        }

        cleanup()

        vm.setQuote(MessagePanelQuote(quoteTitle, quoteText))
    }

    override suspend fun send() = withContext(dispatcher) {
        draft.clearDraft()
        sendMessage()
        draft.reloadDraft()
    }
}
package ru.tensor.sbis.design.message_panel.domain

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.tensor.sbis.design.message_panel.decl.env.MessagePanelEnvironmentModel
import ru.tensor.sbis.design.message_panel.di.usecase.MessagePanelUseCaseComponent
import ru.tensor.sbis.design.message_panel.domain.common.CleanupUseCase
import ru.tensor.sbis.design.message_panel.domain.common.DraftUseCase
import ru.tensor.sbis.design.message_panel.domain.common.SendUseCase
import ru.tensor.sbis.design.message_panel.vm.MessagePanelViewModel
import kotlin.coroutines.CoroutineContext

/**
 * Сценарий работы панели ввода для обычной переписки
 *
 * @author ma.kolpakov
 */
open class SendMessageUseCase internal constructor(
    environment: MessagePanelEnvironmentModel,
    private val useCaseComponent: MessagePanelUseCaseComponent.Factory,
    private val dispatcher: CoroutineContext = Dispatchers.IO
) : AbstractMessagePanelUseCase(environment) {

    private lateinit var sendMessage: SendUseCase<*, *>
    private lateinit var draft: DraftUseCase<*>
    private lateinit var cleanup: CleanupUseCase

    override suspend fun setup(vm: MessagePanelViewModel) = withContext(dispatcher) {
        with(useCaseComponent.create(this@SendMessageUseCase, vm)) {
            sendMessage = sendUseCaseProvider.get()
            draft = draftUseCaseProvider.get()
            cleanup = cleanupUseCase.get()
        }

        cleanup()
        draft.reloadDraft()
    }

    override suspend fun send() = withContext(dispatcher) {
        draft.clearDraft()
        sendMessage()
        draft.reloadDraft()
    }

    override suspend fun save() {
        draft.saveDraft()
    }
}

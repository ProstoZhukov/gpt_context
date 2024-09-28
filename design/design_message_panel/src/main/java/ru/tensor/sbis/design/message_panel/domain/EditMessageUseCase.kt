package ru.tensor.sbis.design.message_panel.domain

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.tensor.sbis.design.message_panel.R
import ru.tensor.sbis.design.message_panel.decl.env.MessagePanelEnvironmentModel
import ru.tensor.sbis.design.message_panel.decl.quote.MessagePanelQuote
import ru.tensor.sbis.design.message_panel.di.usecase.MessagePanelUseCaseComponent
import ru.tensor.sbis.design.message_panel.domain.common.DraftUseCase
import ru.tensor.sbis.design.message_panel.domain.common.SendUseCase
import ru.tensor.sbis.design.message_panel.vm.MessagePanelViewModel
import java.util.*
import kotlin.coroutines.CoroutineContext

/**
 * Сценрарий работы панели ввода для редактирования сообщения
 *
 * @author ma.kolpakov
 */
class EditMessageUseCase internal constructor(
    val editingMessageUuid: UUID,
    val editedText: String,
    environment: MessagePanelEnvironmentModel,
    private val appContext: Context,
    private val useCaseComponent: MessagePanelUseCaseComponent.Factory,
    private val dispatcher: CoroutineContext = Dispatchers.IO
) : AbstractMessagePanelUseCase(environment) {

    private lateinit var sendMessage: SendUseCase<*, *>
    private lateinit var draft: DraftUseCase<*>

    override suspend fun setup(vm: MessagePanelViewModel) = withContext(dispatcher) {
        with(useCaseComponent.create(this@EditMessageUseCase, vm)) {
            sendMessage = sendUseCaseProvider.get()
            draft = draftUseCaseProvider.get()
        }
        draft.saveDraft()

        val title = appContext.getString(R.string.design_message_panel_editing_message_title)

        vm.setText(editedText)
        vm.setQuote(MessagePanelQuote(title, editedText))
        vm.onAttachmentsClearClicked()
    }

    override suspend fun send() = withContext(dispatcher) {
        draft.clearDraft()
        sendMessage()
        draft.reloadDraft()
    }
}

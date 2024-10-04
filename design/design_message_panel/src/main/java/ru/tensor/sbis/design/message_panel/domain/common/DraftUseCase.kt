package ru.tensor.sbis.design.message_panel.domain.common

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.tensor.sbis.design.message_panel.decl.draft.MessageDraftService
import ru.tensor.sbis.design.message_panel.decl.draft.MessageDraftServiceHelper
import ru.tensor.sbis.design.message_panel.domain.AbstractMessagePanelUseCase
import ru.tensor.sbis.design.message_panel.vm.MessagePanelViewModel
import ru.tensor.sbis.design.utils.errorSafe
import timber.log.Timber
import java.util.*
import kotlin.coroutines.CoroutineContext

/**
 * Общая механика работы с черновиком
 *
 * @author ma.kolpakov
 */
internal class DraftUseCase<DRAFT>(
    private val parentUseCase: AbstractMessagePanelUseCase,
    private val service: MessageDraftService<DRAFT>,
    private val serviceHelper: MessageDraftServiceHelper<DRAFT>,
    private val vm: MessagePanelViewModel,
    private val dispatcher: CoroutineContext = Dispatchers.IO
) {

    suspend fun saveDraft() {
        /*
         TODO: сохранение черновика для новых диалогов без conversationId.
            Проверить сценарий, не уверен, что у нового диалога нет conversationId
         */

        val isNewConversation = false
        val draftUuid = vm.draftUuid.value
            ?: errorSafe { "Unable to save draft without id" }
            ?: return
        if (isNewConversation) {
            saveNewConversationDraft(draftUuid)
        } else {
            saveDraft(draftUuid)
        }
    }

    fun clearDraft() {
        vm.setDraftUuid(null)
    }

    suspend fun reloadDraft() = withContext(dispatcher) {
        val draft = service.load(parentUseCase)
        vm.setDraftUuid(serviceHelper.getId(draft))
        val recipients = serviceHelper.getRecipients(draft)
        if (serviceHelper.isEmpty(draft)) {
            if (recipients.isNotEmpty()) {
                // устанавливаем получателей, но разрешаем переопределить т.к. другого контента нет
                // TODO: vm.loadRecipients(recipients, false)
            }
        } else {
            vm.setText(serviceHelper.getText(draft))

            // TODO: загрузить id ответа на комментарий
            // vm.setAnsweredMessageUuid(serviceHelper.getAnsweredMessageId(draft))

            if (recipients.isNotEmpty()) {
                // получатели из черновика -> выбраны пользователем -> переопределить нельзя
                // TODO: vm.loadRecipients(recipients, true)
            }
            // проверяем наличие цитирования в черновике
            val quote = serviceHelper.getQuoteContent(draft)
            if (quote != null) {
                TODO("Переключиться на цитирование")
            }
        }
    }

    /**
     * Сохраняет черновик по получателю сообщения
     */
    private suspend fun saveNewConversationDraft(draftUuid: UUID) {
        val nothingToSave = vm.text.value.isBlank() &&
                vm.attachments.value.isEmpty()
        if (nothingToSave) {
            Timber.d("Draft for new conversation is empty - ignore it")
            return
        }
        val recipients = vm.recipients.value.recipients
        if (recipients.size > 1) {
            Timber.d("Unable to save draft for new message with multiple recipients")
            return
        }
        service.save(
            parentUseCase,
            draftUuid,
            recipients.single().uuid,
            vm.text.value,
            vm.attachmentsUuid.value
        )
    }

    private suspend fun saveDraft(draftUuid: UUID) {
        service.save(
            parentUseCase,
            draftUuid,
            vm.recipientsUuid.value,
            vm.text.value,
            vm.attachmentsUuid.value
        )
    }
}

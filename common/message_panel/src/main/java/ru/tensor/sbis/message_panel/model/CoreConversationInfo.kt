package ru.tensor.sbis.message_panel.model

import androidx.annotation.IntRange
import ru.tensor.sbis.communicator.generated.Permissions
import ru.tensor.sbis.communication_decl.model.ConversationType
import ru.tensor.sbis.communication_decl.selection.recipient.RecipientSelectionUseCase
import ru.tensor.sbis.design.message_panel.decl.recipients.RecipientsView
import ru.tensor.sbis.message_panel.viewModel.livedata.recipients.MAX_RECIPIENTS_COUNT
import java.util.*

/**
 * @property isTextRequired признак необходимости наличия текста для отправки сообщения
 * @property chatMembers используется для оценки необходимости отображения панели получателей
 * @property recipients список [UUID] получателей, которые будут загружены при установке или откате в первоначальное
 * состояние
 * @property isRecipientsHintEnabled true, если при пустом списке получателей
 * необходимо показывать текст-подсказку [RecipientsView.hintText].
 * @property showRecipientsPanel true, если панель получателей должна отображаться.
 * @property requireCheckAllMembers true, если списки получателей необходимо проверять на полное совпадение с количеством
 * участнкиов переписки, чтобы отображать специальный текст формата "Всем участникам" [RecipientsView.allChosenText].
 * @property showOneRecipient отображать строку получателей, если указан по меньшей мере один получатель
 * @property recipientsRequired принудительный запрос установки получателей при попытке отправить. Открывается диалог,
 * как при нажатии на кнопку выбора получателей
 * @property inviteSupported позволять ли отправлять сообщение о напоминании диалога при выключенной кнопке отправки
 * @property recipientsLimit максимальное количество получателей для выбора. Если количество публикуется запрос на
 * одиночный выбор получателей
 * @property showQuickReplyButton нужно ли отображать кнопку открытия панели быстрых ответов для чатов crm
 */
data class CoreConversationInfo @JvmOverloads constructor(
    val conversationType: ConversationType? = ConversationType.REGULAR,
    val conversationUuid: UUID? = null,
    val isGroupConversation: Boolean = false,
    val isNewConversation: Boolean = false,
    val isNewDialogModeEnabled: Boolean = isNewConversation,
    val minLines: Int = 1,
    val isTextRequired: Boolean = false,

    val sharedText: String? = null,
    val sharedAttachments: List<String>? = null,

    val messageUuid: UUID? = null,
    val quotedMessageUuid: UUID? = null,
    val answeredMessageUuid: UUID? = null,
    val folderUuid: UUID? = null,

    val recipientSelectionUseCase: RecipientSelectionUseCase? = null,
    val recipients: List<UUID> = emptyList(),
    val isRecipientsHintEnabled: Boolean = true,
    val showRecipientsPanel: Boolean = true,
    val requireCheckAllMembers: Boolean = false,
    @Deprecated("Удалить после https://online.sbis.ru/opendoc.html?guid=6d77f60d-d7c3-455e-b235-5a51bbb843b5")
    val showOneRecipient: Boolean = false,
    val recipientsRequired: Boolean = false,
    val inviteSupported: Boolean = false,
    val sendButtonEnabled: Boolean = true,
    @IntRange(from = 1)
    val recipientsLimit: Int = MAX_RECIPIENTS_COUNT,

    val isChat: Boolean = false,
    val chatMembers: List<UUID> = emptyList(),
    val chatPermissions: Permissions? = null,

    val document: UUID? = null,

    val clearOnSendOptions: EnumSet<ClearOption> = EnumSet.noneOf(ClearOption::class.java),
    @Deprecated("Удалить после https://online.sbis.ru/opendoc.html?guid=8b7b0930-f557-4742-869f-cd80d41e1877")
    val isMultiDialog: Boolean = true,

    /**
     * true, если требуется для работы с окном получателей запрашивать прикладую сторону, иначе false
     * Удалить после https://online.sbis.ru/doc/83240201-d85d-4fa1-aa2f-3c1f1287904f
     */
    val customRecipientSelection: Boolean = false,
    val saveDraftMessage: Boolean = true,
    val loadDraftMessage: Boolean = true,

    val showAttachmentsButton: Boolean = true,
    val canRemoveAttachments: Boolean = true,
    val canRestartUploadAttachments: Boolean = true,
    val canShowUploadErrorAttachments: Boolean = true,
    val analyticsUsageName: String? = null,
    val messageMetaData: String? = null,
    val showQuickReplyButton: Boolean = false,
) {
    init {
        require(recipientsLimit > 0) { "recipientsLimit must be greater than zero" }
    }
}
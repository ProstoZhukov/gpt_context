package ru.tensor.sbis.design.selection.ui.model.share.dialog

import android.text.Spannable
import ru.tensor.sbis.attachments.models.AttachmentRegisterModel
import ru.tensor.sbis.common_views.SearchSpan
import ru.tensor.sbis.edo_decl.document.DocumentType
import ru.tensor.sbis.design.selection.ui.model.SelectorItemModel
import ru.tensor.sbis.design.selection.ui.model.share.dialog.message.SelectionDialogMessageSyncStatus
import ru.tensor.sbis.design.selection.ui.model.share.dialog.message.SelectionDialogRelevantMessageType
import ru.tensor.sbis.design.profile_decl.person.PersonData
import java.util.*

/**
 * Модель данных диалога для селектора
 *
 * @author vv.chekurda
 */
interface DialogSelectorItemModel : SelectorItemModel {
    /** Тема диалога */
    val dialogTitle: String?

    /** Timestamp диалога в мс */
    val timestamp: Long

    /** Статус синхронизации */
    val syncStatus: SelectionDialogMessageSyncStatus

    /** Список моделей персон для формирования коллажа */
    val participantsCollage: List<PersonData>

    /** Общее количество участников диалога */
    val participantsCount: Int

    /** UUID релевантного сообщения для текущего пользователя */
    var messageUuid: UUID?

    /** Тип релевантного сообщения */
    val messageType: SelectionDialogRelevantMessageType

    /** Название компании персоны, которая является отправителем релевантного сообщения */
    val messagePersonCompany: String?

    /** Текст сообщения */
    val messageText: Spannable

    /** True, если сообщение исходящее от текущего пользователя */
    val isOutgoing: Boolean

    /** True, если сообщение было прочитано хотя бы одним участником диалога, которому это сообщение было адресовано */
    var isRead: Boolean

    /** True, если сообщение было прочитано текущим пользователем */
    var isReadByMe: Boolean

    /** True, если сообщение адресовано текущему пользователю */
    val isForMe: Boolean

    /** Текст сервисного сообщения/сообщения соц. сети */
    val serviceText: Spannable?

    /** Количество непрочитанных сообщений в диалоге у текущего пользователя */
    var unreadCount: Int

    /** UUID документа, прикрепленного к диалогу */
    val documentUuid: UUID?

    /** Тип документа, прикрепленного к диалогу */
    val documentType: DocumentType?

    /** Оригинальное название документа, прикрепленного к диалогу */
    val externalEntityTitle: CharSequence?

    /** Список вложений релевантного сообщения */
    val attachments: List<AttachmentRegisterModel>?

    /** Количество вложений в релевантном сообщении */
    val attachmentCount: Int

    /** True, если чат-обсуждение */
    val isChatForOperations: Boolean

    /** True, если приватный чат */
    val isPrivateChat: Boolean

    /** True, если релевантное сообщение - событие соц-сети */
    val isSocnetEvent: Boolean

    /** Список интервалов с поисковыми совпадениями для выделения текста в сообщении */
    val searchHighlights: List<SearchSpan>?

    /** Список интервалов с поисковыми совпадениями для выделения в заголовке */
    val nameHighlights: List<SearchSpan>?

    /** Список интервалов с поисковыми совпадениями для выделения в названии прикрепленного документа */
    val docsHighlights: List<SearchSpan>?

    /** Список интервалов с поисковыми совпадениями для выделения в теме диалога */
    val dialogNameHighlights: List<SearchSpan>?
}

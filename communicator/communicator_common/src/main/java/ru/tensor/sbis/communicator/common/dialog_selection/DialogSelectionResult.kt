package ru.tensor.sbis.communicator.common.dialog_selection

import android.net.Uri
import ru.tensor.sbis.edo_decl.document.DocumentType
import java.util.*

/**
 * Результат экрана выбора диалога/участников
 *
 * @author vv.chekurda
 */
sealed class DialogSelectionResult

/**
 * Результат с выбранными участниками для нового диалога
 * @param personUuids     список идентификаторов персон
 * @param departmentUuids список идентификаторов отделов
 * @param text            текст, которым делятся
 * @param files           файлы, которыми делятся
 */
data class SelectedParticipantsResult(
    val personUuids: List<UUID>,
    val departmentUuids: List<UUID>,
    val text: String? = null,
    val files: List<Uri>? = null
) : DialogSelectionResult()

/**
 * Результат с выбранным существующим диалогом
 * @param dialogUuid          идентификатор диалога
 * @param relevantMessageUuid идентификатор релевантного сообщения
 * @param isChat              true, если чат
 * @param documentUuid        идентификатор документа, прикрепленного к диалогу
 * @param documentType        тип документа, прикрепленного к диалогу
 * @param isForMe             true, если релевантное сообщение адресовано текущему пользователю
 * @param isSocnetEvent       true, если релевантное сообщение - событие соц.сети
 * @param text                текст, которым делятся
 * @param files               файлы, которыми делятся
 */
data class SelectedDialogResult(
    val dialogUuid: UUID,
    val relevantMessageUuid: UUID?,
    val isChat: Boolean,
    val documentUuid: UUID?,
    val documentType: DocumentType?,
    val isForMe: Boolean,
    val isSocnetEvent: Boolean,
    val text: String? = null,
    val files: List<Uri>? = null
) : DialogSelectionResult()

/**
 * Результат закрытия экрана выбора диалога
 */
object CancelDialogSelectionResult: DialogSelectionResult()

/**
 * Результат очистки текущего выбора на экране выбора диалога
 */
object ClearDialogSelectionResult : DialogSelectionResult()
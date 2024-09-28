package ru.tensor.sbis.communicator.dialog_selection.data

import ru.tensor.sbis.communicator.dialog_selection.data.factory.service_wrapper.theme.DialogsResult
import ru.tensor.sbis.communicator.dialog_selection.data.factory.service_wrapper.recipients.RecipientsServiceResult

/**
 * Комбинированный результат нескольких контроллеров экрана выбора диалога/участников
 * @property recipientsResult результат контроллера получателей
 * @property dialogsResult    результат контроллера диалогов
 *
 * @author vv.chekurda
 */
internal data class DialogSelectionServiceResult(
    val recipientsResult: RecipientsServiceResult,
    val dialogsResult: DialogsResult
)
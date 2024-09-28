package ru.tensor.sbis.communicator.crm.conversation.presentation.ui.conversation_option

import androidx.annotation.StringRes
import ru.tensor.sbis.communicator.crm.conversation.R
import ru.tensor.sbis.design.SbisMobileIcon

/**
 * Enum опции переписки для чатов техподдержки.
 *
 * @param iconRes - иконка.
 * @param textRes - ресурс текста.
 * @param destructive - выделение красным цветом.
 *
 * @author dv.baranov
 */
internal enum class CRMConversationOption(
    val iconRes: SbisMobileIcon.Icon,
    @StringRes
    val textRes: Int,
    val destructive: Boolean = false
) {

    /**
     * Вернуть в очередь.
     */
    REASSIGN_TO_QUEUE(SbisMobileIcon.Icon.smi_ArrowReturn, R.string.communicator_crm_reassign_to_queue_option),

    /**
     * Переназначить на группу.
     */
    REASSIGN_TO_GROUP(SbisMobileIcon.Icon.smi_Publish2, R.string.communicator_crm_reassign_to_group_option),

    /**
     * Другому сотруднику.
     */
    REASSIGN_TO_OPERATOR(SbisMobileIcon.Icon.smi_Profile, R.string.communicator_crm_reassign_to_operator_option),

    /**
     * Скопировать ссылку.
     */
    COPY_LINK(SbisMobileIcon.Icon.smi_link, R.string.communicator_crm_copy_link_option),

    /**
     * Запросить контакты.
     */
    REQUEST_CONTACTS(SbisMobileIcon.Icon.smi_paymentSalary, R.string.communicator_crm_request_contacts_option),

    /**
     * Написать клиенту.
     */
    WRITE_TO_CLIENT(SbisMobileIcon.Icon.smi_newChat, R.string.communicator_crm_write_to_client_option),

    /**
     * Завершить.
     */
    COMPLETE(SbisMobileIcon.Icon.smi_approval, R.string.communicator_crm_complete_option),

    /**
     * Удалить.
     */
    DELETE(SbisMobileIcon.Icon.smi_delete, R.string.communicator_crm_delete_option, true)
}

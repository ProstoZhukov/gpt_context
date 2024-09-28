package ru.tensor.sbis.communicator.base.conversation.data.model

import androidx.annotation.StringRes
import ru.tensor.sbis.common.generated.SyncStatus
import ru.tensor.sbis.communicator.common.conversation.data.Message
import ru.tensor.sbis.communicator.core.utils.MessageUtils
import ru.tensor.sbis.communicator.design.R
import ru.tensor.sbis.communicator.generated.MessageRemovableType
import ru.tensor.sbis.communicator.generated.Permissions
import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.design.SbisMobileIcon.Icon.smi_AlertNull
import ru.tensor.sbis.design.SbisMobileIcon.Icon.smi_Recover
import ru.tensor.sbis.design.SbisMobileIcon.Icon.smi_SwipePensil
import ru.tensor.sbis.design.SbisMobileIcon.Icon.smi_SwipePin
import ru.tensor.sbis.design.SbisMobileIcon.Icon.smi_SwipeUnpin
import ru.tensor.sbis.design.SbisMobileIcon.Icon.smi_arrowUpLeft
import ru.tensor.sbis.design.SbisMobileIcon.Icon.smi_copy
import ru.tensor.sbis.design.SbisMobileIcon.Icon.smi_delete
import ru.tensor.sbis.design.SbisMobileIcon.Icon.smi_information
import ru.tensor.sbis.design.SbisMobileIcon.Icon.smi_newDialog

/**
 * Enum действий с сообщением.
 *
 * @param iconRes - иконка
 * @param textRes - ресурс текста
 * @param destructive - выделение красным цветом
 *
 * @author dv.baranov
 */
enum class MessageAction(
    val iconRes: SbisMobileIcon.Icon,
    @StringRes val textRes: Int,
    val destructive: Boolean = false
) {
    /** Редактировать. */
    EDIT(smi_SwipePensil, R.string.communicator_selected_message_action_edit),

    /** Повторить отправку. */
    FORCE_RESEND(smi_Recover, R.string.communicator_selected_message_action_force_resend),

    /** Удалить. */
    DELETE(smi_delete, R.string.communicator_selected_message_action_delete, true),

    /** Копировать. */
    COPY(smi_copy, R.string.communicator_selected_message_action_copy),

    /** Создать тред. */
    THREAD(smi_newDialog, R.string.communicator_selected_message_action_thread),

    /** Цитировать. */
    QUOTE(smi_arrowUpLeft, R.string.communicator_selected_message_action_quote),

    /** Информация по сообщению. */
    INFO(smi_information, R.string.communicator_selected_message_action_info),

    /** Закрепить. */
    PIN(smi_SwipePin, R.string.communicator_selected_message_action_pin),

    /** Открепить. */
    UNPIN(smi_SwipeUnpin, R.string.communicator_selected_message_action_unpin),

    /** Пожаловаться. */
    REPORT(smi_AlertNull, R.string.communicator_selected_message_action_report)
}

/**
 * Получить список действий над сообщением в переписке.
 */
fun getDefaultMessageActionsList(
    message: Message,
    complainEnabled: Boolean = false,
    isChat: Boolean = false,
    isMessagePinned: Boolean = false,
    permissions: Permissions? = null
): List<MessageAction> =
    mutableListOf<MessageAction>().apply {
        val canSendMessage = permissions?.canSendMessage == true || !isChat
        val canDeleteMessage = permissions?.canDeleteMessage == true || !isChat
        val canBeForceResend = SyncStatus.SUCCEEDED != message.syncStatus && canSendMessage
        val canBeEdited = message.editable && canSendMessage
        val canBeQuoted = message.isQuotable && canSendMessage
        val canBeRemoved = message.removableType != MessageRemovableType.NOT_REMOVABLE && canDeleteMessage
        val pinnable = message.pinnable
        val canBeCopied = !message.textForCopy.isNullOrBlank()
        val canCreateThread = message.canCreateThread && canSendMessage

        if (canBeForceResend) add(MessageAction.FORCE_RESEND)
        if (canBeEdited) add(MessageAction.EDIT)
        if (canBeQuoted) add(MessageAction.QUOTE)
        if (canBeCopied) add(MessageAction.COPY)
        if (canCreateThread) add(MessageAction.THREAD)

        if (isChat && pinnable && canSendMessage) {
            if (isMessagePinned) {
                add(MessageAction.UNPIN)
            } else {
                add(MessageAction.PIN)
            }
        }
        add(MessageAction.INFO)
        if (complainEnabled && !message.outgoing) add(MessageAction.REPORT)
        if (canBeRemoved) add(MessageAction.DELETE)
    }

/**
 * Получить список действий над аудио-сообщением в переписке.
 */
fun getAudioRecordMessageActionsList(
    message: Message,
    isChat: Boolean,
    permissions: Permissions? = null
): List<MessageAction> =
    mutableListOf<MessageAction>().apply {
        val canSendMessage = permissions?.canSendMessage == true || !isChat
        val canDeleteMessage = permissions?.canDeleteMessage == true || !isChat
        val canBeForceResend = SyncStatus.SUCCEEDED != message.syncStatus && canSendMessage
        val canBeQuoted = message.isQuotable && canSendMessage
        val canBeRemoved = message.removableType != MessageRemovableType.NOT_REMOVABLE && canDeleteMessage
        val canBeCopied = !message.textForCopy.isNullOrBlank()

        if (canBeForceResend) add(MessageAction.FORCE_RESEND)
        if (canBeQuoted) add(MessageAction.QUOTE)
        if (canBeCopied) add(MessageAction.COPY)
        if (canBeRemoved) add(MessageAction.DELETE)
    }

/**
 * Получить список действий над сообщением в чатах CRM.
 */
fun getCRMMessageActionsList(
    message: Message,
    complainEnabled: Boolean = false,
    isCompleteChat: Boolean,
): List<MessageAction> =
    mutableListOf<MessageAction>().apply {
        val canBeForceResend = SyncStatus.SUCCEEDED != message.syncStatus
        val canBeEdited = message.editable && !isCompleteChat
        val canBeQuoted = message.isQuotable && !isCompleteChat
        val canBeRemoved = message.removableType != MessageRemovableType.NOT_REMOVABLE && !isCompleteChat

        if (canBeForceResend) add(MessageAction.FORCE_RESEND)
        if (canBeQuoted) add(MessageAction.QUOTE)
        if (canBeEdited) add(MessageAction.EDIT)
        if (MessageUtils.findFirstTextInMessage(message) != null) add(MessageAction.COPY)
        if (canBeRemoved) add(MessageAction.DELETE)
        if (complainEnabled && !message.outgoing) add(MessageAction.REPORT)
    }

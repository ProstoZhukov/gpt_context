package ru.tensor.sbis.communicator.sbis_conversation.ui.conversation_option

import androidx.annotation.StringRes
import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.communicator.design.R as RCommunicatorDesign
import ru.tensor.sbis.design.R as RDesign

/**
 * Enum опций переписки
 *
 * @param iconRes - иконка
 * @param textRes - ресурс текста
 * @param destructive - выделение красным цветом
 *
 * @author da.zhukov
 */
internal enum class ConversationOption(
    val iconRes: SbisMobileIcon.Icon,
    @StringRes val textRes: Int,
    val destructive: Boolean = false
) {
    /**
     * Добавить участника
     */
    ADD_MEMBER(SbisMobileIcon.Icon.smi_ProfileNew, RCommunicatorDesign.string.communicator_add_chat_member),
    /**
     * Настройки
     */
    SETTINGS(SbisMobileIcon.Icon.smi_navBarSettings, RCommunicatorDesign.string.communicator_channel_settings_label),
    /**
     * Сменить тему диалога
     */
    CHANGE_DIALOG_NAME(SbisMobileIcon.Icon.smi_menuMessages, RCommunicatorDesign.string.communicator_dialog_name),
    /**
     * Удалить
     */
    HIDE_CHAT(SbisMobileIcon.Icon.smi_delete, RCommunicatorDesign.string.communicator_channel_remove_label, true),
    /**
     * Восстановить чат
     */
    UNHIDE_CHAT(SbisMobileIcon.Icon.smi_SwipeRecover, RCommunicatorDesign.string.communicator_channel_restore_label),
    /**
     * Покинуть чат
     */
    LEAVE_CHAT(SbisMobileIcon.Icon.smi_exit, RCommunicatorDesign.string.communicator_channel_leave_label, true),
    /**
     * Удалить переписку
     */
    DELETE_CONVERSATION(SbisMobileIcon.Icon.smi_delete, RCommunicatorDesign.string.communicator_delete_conversation, true),
    /**
     * Скопировать ссылку
     */
    COPY_LINK(SbisMobileIcon.Icon.smi_link, RDesign.string.design_menu_item_copy_link),
    /**
     * Открыть группу проекта
     */
    GO_TO_PROJECT(SbisMobileIcon.Icon.smi_docTypeText, RCommunicatorDesign.string.communicator_go_to_project),
    /**
     * Открыть группу
     */
    GO_TO_GROUP(SbisMobileIcon.Icon.smi_docTypeText, RCommunicatorDesign.string.communicator_go_to_group),
    /**
     * Добавить получателя
     */
    SELECT_RECIPIENTS(SbisMobileIcon.Icon.smi_CallButtons, RCommunicatorDesign.string.communicator_add_recipient),
    /**
     * Восстановить диалог
     */
    UNHIDE_DIALOG(SbisMobileIcon.Icon.smi_Recover, RCommunicatorDesign.string.communicator_restore_dialog),
    /**
     * Создать задачу
     */
    CREATE_TASK(SbisMobileIcon.Icon.smi_task, RCommunicatorDesign.string.communicator_create_task),

    /**
     * Информация о диалоге.
     */
    DIALOG_INFORMATION(SbisMobileIcon.Icon.smi_information, RCommunicatorDesign.string.communicator_dialog_information),

    /**
     * Информация о канале.
     */
    CHAT_INFORMATION(SbisMobileIcon.Icon.smi_information, RCommunicatorDesign.string.communicator_channel_information),

    COMPLAIN(SbisMobileIcon.Icon.smi_AlertNull, RCommunicatorDesign.string.communicator_selected_message_action_report);
}
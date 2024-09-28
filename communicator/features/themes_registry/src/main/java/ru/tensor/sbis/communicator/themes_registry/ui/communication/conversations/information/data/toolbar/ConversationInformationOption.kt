package ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.data.toolbar

import androidx.annotation.StringRes
import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.communicator.design.R as RCommunicatorDesign
import ru.tensor.sbis.design.R as RDesign

/**
 * Enum опций в информации о диалоге/канале.
 *
 * @param iconRes иконка.
 * @param textRes ресурс текста.
 * @param destructive выделение красным цветом.
 *
 * @author dv.baranov
 */
internal enum class ConversationInformationOption(
    val iconRes: SbisMobileIcon.Icon,
    @StringRes val textRes: Int,
    val destructive: Boolean = false
) {

    /**
     * Добавить участника.
     */
    ADD_MEMBER(SbisMobileIcon.Icon.smi_ProfileNew, RCommunicatorDesign.string.communicator_add_chat_member),

    /**
     * Скопировать ссылку.
     */
    COPY_LINK(SbisMobileIcon.Icon.smi_link, RDesign.string.design_menu_item_copy_link),

    /**
     * Удалить переписку.
     */
    DELETE(SbisMobileIcon.Icon.smi_delete, RCommunicatorDesign.string.communicator_channel_remove_label, true),
}
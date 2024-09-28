package ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.ui.links_list.data

import androidx.annotation.StringRes
import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.design.theme.res.SbisColor
import ru.tensor.sbis.communicator.design.R as RCommunicatorDesign
import ru.tensor.sbis.design.R as RDesign

/**
 * Enum опций для меню по лонгтапу на ссылку в списке ссылок информации о диалоге/канале.
 *
 * @param iconRes иконка.
 * @param textRes ресурс текста.
 * @param iconColor цвет иконки.
 *
 * @author dv.baranov
 */
internal enum class ConversationLinkOption(
    val iconRes: SbisMobileIcon.Icon,
    @StringRes val textRes: Int,
    val iconColor: SbisColor = SbisColor.Attr(RDesign.attr.labelIconColor)
) {

    /**
     * Открыть ссылку.
     */
    OPEN_LINK(SbisMobileIcon.Icon.smi_OpenInAnotherApp, RCommunicatorDesign.string.communicator_conversation_links_list_menu_option_open),

    /**
     * Закрепить.
     */
    PIN(SbisMobileIcon.Icon.smi_SwipePin, RCommunicatorDesign.string.communicator_conversation_links_list_menu_option_pin),

    /**
     * Открепить.
     */
    UNPIN(SbisMobileIcon.Icon.smi_SwipeUnpin, RCommunicatorDesign.string.communicator_conversation_links_list_menu_option_unpin),

    /**
     * Скопировать.
     */
    COPY(SbisMobileIcon.Icon.smi_link, RCommunicatorDesign.string.communicator_conversation_links_list_menu_option_copy),

    /**
     * Перейти к сообщению.
     */
    GO_TO_MESSAGE(SbisMobileIcon.Icon.smi_conversation, RCommunicatorDesign.string.communicator_conversation_links_list_menu_option_go_to_message),

    /**
     * Удалить ссылку.
     */
    DELETE(SbisMobileIcon.Icon.smi_SwipeDelete, RCommunicatorDesign.string.communicator_channel_remove_label, SbisColor.Attr(RDesign.attr.dangerIconColor)),
}
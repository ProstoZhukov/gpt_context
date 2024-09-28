package ru.tensor.sbis.communicator.common.conversation_preview

import ru.tensor.sbis.design.SbisMobileIcon
import java.io.Serializable
import ru.tensor.sbis.communicator.design.R as RCommunicatorDesign

/**
 * Маркерный интерфейс для элементов меню ConversationPreviewMenu.
 *
 * Используется для идентификации элементов меню, которые могут быть переданы и обработаны в `ConversationPreviewFragment`.
 *
 * @author da.zhukov
 */
interface ConversationPreviewMenuAction : Serializable {
    val textResId: Int
    val icon: SbisMobileIcon.Icon
    val isDestructive: Boolean

    sealed interface MessageConversationPreviewMenuAction : ConversationPreviewMenuAction

    sealed interface ThemeConversationPreviewMenuAction : ConversationPreviewMenuAction {

        class GoToConversation : ThemeConversationPreviewMenuAction {
            override val textResId: Int = RCommunicatorDesign.string.communicator_preview_menu_go
            override val icon: SbisMobileIcon.Icon = SbisMobileIcon.Icon.smi_MessageWithArrow
            override val isDestructive: Boolean = false
        }

        class MarkGroupAsRead : ThemeConversationPreviewMenuAction {
            override val textResId: Int = RCommunicatorDesign.string.communicator_check_panel_read
            override val icon: SbisMobileIcon.Icon = SbisMobileIcon.Icon.smi_read
            override val isDestructive: Boolean = false
        }

        class MarkGroupAsUnread : ThemeConversationPreviewMenuAction {
            override val textResId: Int = RCommunicatorDesign.string.communicator_check_panel_unread
            override val icon: SbisMobileIcon.Icon = SbisMobileIcon.Icon.smi_unread
            override val isDestructive: Boolean = false
        }

        class MoveGroupToFolder : ThemeConversationPreviewMenuAction {
            override val textResId: Int = RCommunicatorDesign.string.communicator_check_panel_move
            override val icon: SbisMobileIcon.Icon = SbisMobileIcon.Icon.smi_moveToFolder
            override val isDestructive: Boolean = false
        }

        class MarkDialog : ThemeConversationPreviewMenuAction {
            override val textResId: Int = RCommunicatorDesign.string.communicator_preview_menu_mark
            override val icon: SbisMobileIcon.Icon = SbisMobileIcon.Icon.smi_ok
            override val isDestructive: Boolean = false
        }

        class DeleteGroup : ThemeConversationPreviewMenuAction {
            override val textResId: Int = RCommunicatorDesign.string.communicator_check_panel_delete
            override val icon: SbisMobileIcon.Icon = SbisMobileIcon.Icon.smi_delete
            override val isDestructive: Boolean = true
        }

        class Report : ThemeConversationPreviewMenuAction {
            override val textResId: Int = RCommunicatorDesign.string.communicator_preview_menu_report
            override val icon: SbisMobileIcon.Icon = SbisMobileIcon.Icon.smi_AlertNull
            override val isDestructive: Boolean = false
        }

        class Pin : ThemeConversationPreviewMenuAction {
            override val textResId: Int = RCommunicatorDesign.string.communicator_pin_chat
            override val icon: SbisMobileIcon.Icon = SbisMobileIcon.Icon.smi_SwipePin
            override val isDestructive: Boolean = false
        }

        class Unpin : ThemeConversationPreviewMenuAction {
            override val textResId: Int = RCommunicatorDesign.string.communicator_unpin_chat
            override val icon: SbisMobileIcon.Icon = SbisMobileIcon.Icon.smi_SwipeUnpin
            override val isDestructive: Boolean = false
        }

        class Restore :  ThemeConversationPreviewMenuAction {
            override val textResId: Int = RCommunicatorDesign.string.communicator_restore_chat
            override val icon: SbisMobileIcon.Icon = SbisMobileIcon.Icon.smi_SwipeRecover
            override val isDestructive: Boolean = false
        }
    }
}



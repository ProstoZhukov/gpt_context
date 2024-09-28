package ru.tensor.sbis.communicator.common.contract

import CommunicatorPushKeyboardHelper
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import ru.tensor.sbis.android_ext_decl.BuildConfig
import ru.tensor.sbis.communicator.common.conversation.ConversationEventsPublisher
import ru.tensor.sbis.communicator.common.di.CommunicatorCommonComponent
import ru.tensor.sbis.communicator.contacts_declaration.controller.ContactsControllerWrapper

/**@SelfDocumented**/
interface CommunicatorCommonFeature :
    ConversationEventsPublisher.Provider,
    ContactsControllerWrapper.Provider,
    CommunicatorPushKeyboardHelper.Provider {

    // TODO прдублировано из ru.tensor.sbis.communicator.contract.CommunicatorFeatureImpl
    //  убрать дубли, поправить ссылки на константы в приложении
    companion object {
        @Suppress("unused")
        const val ACTION_CONVERSATION_RECIPIENT_SELECTION_ACTIVITY = BuildConfig.MAIN_APP_ID + ".CONVERSATION_RECIPIENTS_SELECTION_ACTIVITY"
        const val ACTION_CONVERSATION_ACTIVITY = BuildConfig.MAIN_APP_ID + ".CONVERSATION_ACTIVITY"
        @Suppress("unused")
        const val ACTION_QUICK_REPLY_ACTIVITY = BuildConfig.MAIN_APP_ID + ".QUICK_REPLY_ACTIVITY"

        const val EXTRA_IS_VIOLATION = "is_violation"
        const val EXTRA_CONVERSATION_ACTIVITY_DIALOG_UUID_KEY = "dialog_uuid"
        const val EXTRA_CONVERSATION_ACTIVITY_CONVERSATION_TYPE_KEY = "conversation_type"
        const val EXTRA_CONVERSATION_ACTIVITY_DOCUMENT_KEY = "document"

        const val CONVERSATION_ACTIVITY_PARTICIPANTS_UUIDS_KEY = "participants_list"
        const val CONVERSATION_ACTIVITY_MESSAGE_UUID_KEY = "message_uuid"
        const val CONVERSATION_ACTIVITY_SENDER_UUID_KEY = "sender_Uuid"
        const val CONVERSATION_ACTIVITY_FOLDER_UUID_KEY = "folder_uuid"
        const val CONVERSATION_ACTIVITY_ARCHIVED_CONVERSATION = "archived_dialog"
        const val CONVERSATION_ACTIVITY_IS_GROUP_CONVERSATION = "is_group_conversation"
        const val CONVERSATION_ACTIVITY_CHAT_KEY = "chat_conversation"
        const val CONVERSATION_ACTIVITY_CHATS_REGISTRY_KEY = "from_chats_registry"
        const val CONVERSATION_ACTIVITY_ARE_RECIPIENTS_SELECTED = "are_recipients_selected"
        const val CONVERSATION_ACTIVITY_TOOLBAR_VIEW_DATA = "conversation_activity_toolbar_view_data"
        const val CONVERSATION_ACTIVITY_TOOLBAR_TITLE = "conversation_activity_toolbar_title"
        const val CONVERSATION_ACTIVITY_TOOLBAR_DIALOG_TITLE = "conversation_activity_toolbar_dialog_title"
        const val CONVERSATION_ACTIVITY_TOOLBAR_PHOTO_ID = "conversation_activity_toolbar_photo_id"
        const val CONVERSATION_ACTIVITY_IS_SHARING = "conversation_activity_is_sharing"
        const val CONVERSATION_ACTIVITY_THREAD_INFO = "conversation_activity_thread_info"
        const val CONVERSATION_ACTIVITY_FROM_PARENT_THREAD = "conversation_activity_from_parent_thread"
        const val CONVERSATION_ACTIVITY_HIGHLIGHT_MESSAGE = "conversation_activity_highlight_message"
        const val CONVERSATION_ACTIVITY_CONVERSATION_ARG = "CONVERSATION_ACTIVITY_CONVERSATION_ARG"
    }
}

/** Реализация [CommunicatorCommonFeature] */
class CommunicatorCommonFeatureImpl(
    private val holder: CommunicatorCommonComponent.Holder
) : CommunicatorCommonFeature {

    private val keyboardHelper by lazy {
        object : CommunicatorPushKeyboardHelper {
            override val hideKeyboard: MutableSharedFlow<Boolean> = MutableSharedFlow(
                extraBufferCapacity = 1,
                onBufferOverflow = BufferOverflow.DROP_OLDEST
            )
        }
    }

    override fun getConversationEventsPublisher(): ConversationEventsPublisher =
        holder.communicatorCommonComponent.conversationEventsPublisher

    override val contactsControllerWrapper: ContactsControllerWrapper =
        holder.communicatorCommonComponent.contactsControllerWrapper

    override fun getCommunicatorPushKeyboardHelper(): CommunicatorPushKeyboardHelper {
        return keyboardHelper
    }
}
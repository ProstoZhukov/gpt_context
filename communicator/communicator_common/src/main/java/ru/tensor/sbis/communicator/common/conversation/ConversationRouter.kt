package ru.tensor.sbis.communicator.common.conversation

import android.content.Context
import ru.tensor.sbis.attachments.decl.v2.DefAttachmentListComponentConfig
import ru.tensor.sbis.base_components.BaseFragment
import ru.tensor.sbis.communication_decl.communicator.ui.ConversationProvider
import ru.tensor.sbis.communication_decl.selection.recipient.RecipientSelectionUseCase
import ru.tensor.sbis.communicator.common.conversation.data.LinkDialogToTask
import ru.tensor.sbis.communicator.common.conversation.data.TaskCreationResult
import ru.tensor.sbis.communicator.common.data.ConversationDetailsParams
import ru.tensor.sbis.communicator.generated.Permissions
import ru.tensor.sbis.design.profile_decl.person.PhotoData
import ru.tensor.sbis.edo_decl.document.Document
import ru.tensor.sbis.persons.ContactVM
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.tasks.feature.DiskAttachment
import ru.tensor.sbis.viewer.decl.slider.ViewerSliderArgs
import java.util.UUID

/**
 * Поставщик роутера сообщений.
 *
 * @author vv.chekurda
 */
interface ConversationRouterProvider : Feature {

    /**
     * Получить инстанс роутера.
     *
     * @param fragment    фрагмент сообщений.
     * @param containerId идентификатор корневого контейнера.
     */
    fun getConversationRouter(fragment: BaseFragment, containerId: Int = 0): ConversationRouter
}

/**
 * Интерфейс открытия других экранов из реестра сообщений.
 */
interface ConversationRouter {

    /** @SelfDocumented */
    fun showProfile(personUuid: UUID)

    fun showConversation(params: ConversationDetailsParams)

    /**
     * Показать экран подтверждения номера телефона.
     */
    fun showPhoneVerification()

    /** @SelfDocumented */
    fun showRecipientSelection(useCase: RecipientSelectionUseCase)

    /** @SelfDocumented */
    fun showAddChatParticipants(chatUuid: UUID)

    /** @SelfDocumented */
    fun showConversationMembers(
        conversationUuid: UUID,
        subtitle: String,
        isNewDialog: Boolean,
        isChat: Boolean,
        conversationName: String?,
        permissions: Permissions?,
        participantsUuids: ArrayList<UUID>?,
        isGroupConversation: Boolean,
        photoData: List<PhotoData>,
        singleParticipant: ContactVM?
    )

    /** @SelfDocumented */
    fun showTaskCreation(
        dialogUuid: UUID,
        linkType: LinkDialogToTask?,
        currentAccountUUID: UUID,
        executors: List<UUID>,
        description: String,
        attachmentsFromUris: List<String>,
        attachmentsFromDisk: List<DiskAttachment>,
        listener: ((TaskCreationResult) -> Unit)?
    )

    /** @SelfDocumented */
    fun showMessageInformationScreen(dialogUuid: UUID, messageUuid: UUID, groupDialog: Boolean, isChannel: Boolean)

    /** @SelfDocumented */
    fun showChatSettings(conversationUuid: UUID?, isNewChat: Boolean, isDraft: Boolean)

    /** @SelfDocumented */
    fun exit()

    /** @SelfDocumented */
    fun showDocument(document: Document)

    /** @SelfDocumented */
    fun openWebView(title: String, url: String)

    /** @SelfDocumented */
    fun showViewerSlider(args: ViewerSliderArgs)

    /** @SelfDocumented */
    fun showFolder(attachmentListComponentConfig: DefAttachmentListComponentConfig)

    /** @SelfDocumented */
    fun openLink(url: String)

    /** @SelfDocumented */
    fun openTask(uuid: String)

    /** @SelfDocumented */
    fun runWithContext(action: ((Context) -> Unit))

    /** @SelfDocumented */
    fun callTheNumber(phoneNumber: String)

    /** @SelfDocumented */
    fun addNumberToPhoneBook(phoneNumber: String)

    /** @SelfDocumented */
    fun dialPhoneNumber(phoneNumber: String)

    /** @SelfDocumented */
    fun changeRegistrySelectedItem(conversationUuid: UUID)

    companion object {
        const val DIALOG_PARTICIPANTS_ACTIVITY_CODE = ConversationProvider.DIALOG_PARTICIPANTS_ACTIVITY_CODE
        const val CONVERSATION_VIEWER_SLIDER_CODE = 11
        const val CHAT_SETTINGS_REQUEST_CODE = 12
        const val CONVERSATION_INFO_SELECTION_RESULT_KEY = "CONVERSATION_INFO_SELECTION_RESULT_KEY"
        const val CONVERSATION_INFO_SELECTION_RESULT_UUID_KEY = "CONVERSATION_INFO_SELECTION_RESULT_UUID_KEY"
    }
}
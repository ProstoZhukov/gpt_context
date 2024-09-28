package ru.tensor.sbis.communicator.themes_registry

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import ru.tensor.sbis.android_ext_decl.IntentAction
import ru.tensor.sbis.common.data.DependencyProvider
import ru.tensor.sbis.common.navigation.MenuNavigationItemType
import ru.tensor.sbis.common.util.UUIDUtils
import ru.tensor.sbis.common.util.asArrayList
import ru.tensor.sbis.communication_decl.model.ConversationType
import ru.tensor.sbis.communicator.common.themes_registry.AddChatParticipantsIntentFactory
import ru.tensor.sbis.communicator.common.themes_registry.ChatSettingsIntentFactory
import ru.tensor.sbis.communicator.common.themes_registry.ConversationInformationFactory
import ru.tensor.sbis.communicator.common.themes_registry.ConversationParticipantsFactory
import ru.tensor.sbis.communicator.declaration.counter.CommunicatorCounterModel
import ru.tensor.sbis.communicator.declaration.counter.factory.CommunicatorCounterProviderFactory
import ru.tensor.sbis.communicator.declaration.counter.nav_counters.CommunicatorNavCounters
import ru.tensor.sbis.communicator.declaration.model.CommunicatorRegistryType
import ru.tensor.sbis.communicator.declaration.theme.ThemesRegistryFragmentFactory
import ru.tensor.sbis.communicator.generated.Permissions
import ru.tensor.sbis.communicator.generated.UnreadCountersController
import ru.tensor.sbis.communicator.themes_registry.contract.ThemesRegistryDependency
import ru.tensor.sbis.communicator.themes_registry.contract.ThemesRegistryFeature
import ru.tensor.sbis.communicator.themes_registry.ui.chatrecipientselection.ChatRecipientSelectionActivity
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.chats.settings.ChatSettingsActivity
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.ConversationInformationFragment
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.ConversationInformationFragment.Companion.CONVERSATION_CHAT_PERMISSION_KEY
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.ConversationInformationFragment.Companion.CONVERSATION_IS_CHAT_KEY
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.ConversationInformationFragment.Companion.CONVERSATION_IS_GROUP_KEY
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.ConversationInformationFragment.Companion.CONVERSATION_IS_NEW_KEY
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.ConversationInformationFragment.Companion.CONVERSATION_PARTICIPANTS_SUBTITLE
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.ConversationInformationFragment.Companion.CONVERSATION_PHOTO_DATA_KEY
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.ConversationInformationFragment.Companion.CONVERSATION_SINGLE_PARTICIPANT
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.ConversationInformationFragment.Companion.CONVERSATION_TITLE_KEY
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.ConversationInformationFragment.Companion.CONVERSATION_UUID_KEY
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.theme.ThemeFragment
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.theme.delegates.ThemeNotificationsConfigHelper
import ru.tensor.sbis.communicator.themes_registry.ui.conversationparticipants.ConversationParticipantsActivity
import ru.tensor.sbis.communicator.themes_registry.utils.counter.CommunicatorCounterProvider
import ru.tensor.sbis.communicator.themes_registry.utils.counter.CommunicatorCounterRepository
import ru.tensor.sbis.communicator.themes_registry.utils.counter.navigation.CommunicatorNavCountersImpl
import ru.tensor.sbis.deeplink.DeeplinkAction
import ru.tensor.sbis.deeplink.DeeplinkActionNode
import ru.tensor.sbis.deeplink.OpenConversationDeeplinkAction
import ru.tensor.sbis.design.profile_decl.person.PhotoData
import ru.tensor.sbis.persons.ContactVM
import ru.tensor.sbis.toolbox_decl.counters.CounterProvider
import ru.tensor.sbis.toolbox_decl.linkopener.data.DocType
import ru.tensor.sbis.toolbox_decl.linkopener.handler.LinkOpenHandler
import java.util.UUID

/**
 * Фасад модуля реестров диалогов и чатов.
 * Предоставляет фичи [ThemesRegistryFeature] и зависимости [ThemesRegistryDependency] модуля.
 *
 * @author da.zhukov
 */
internal object ThemesRegistryFacade : ThemesRegistryFeature,
    ThemesRegistryDependency.Provider,
    ThemesRegistryFragmentFactory by ThemeFragment.Companion,
    AddChatParticipantsIntentFactory by ChatRecipientSelectionActivity.Companion,
    ConversationParticipantsFactory by ConversationParticipantsActivity.Companion,
    ChatSettingsIntentFactory by ChatSettingsActivity.Companion,
    CommunicatorCounterProviderFactory,
    LinkOpenHandler.Provider,
    ConversationInformationFactory {

    override lateinit var themesRegistryDependency: ThemesRegistryDependency

    override val communicatorCounterProvider: CounterProvider<CommunicatorCounterModel> by lazy {
        CommunicatorCounterProvider(CommunicatorCounterRepository(DependencyProvider.create(UnreadCountersController::instance)))
    }

    override val communicatorNavCounters: CommunicatorNavCounters by lazy {
        CommunicatorNavCountersImpl(communicatorCounterProvider)
    }

    fun configure(dependency: ThemesRegistryDependency) {
        themesRegistryDependency = dependency
    }

    override fun createThemeHostFragment(registryType: CommunicatorRegistryType, action: DeeplinkAction?) =
        themesRegistryDependency.createCommunicatorHostFragment(registryType, action)

    override fun getLinkOpenHandler(): LinkOpenHandler =
        themesRegistryDependency.run {
            linkOpenerHandlerCreator.createSingleForRouter(
                DocType.OPEN_CHAT,
                DocType.OPEN_DIALOG
            ) { preview, _ ->
                val uri = Uri.parse(preview.fullUrl)
                val messageUuid = UUIDUtils.fromString(uri.getQueryParameter(QUERY_MESSAGE_UUID_KEY))
                val dialogUuid = UUIDUtils.validateUuid(preview.guid)
                val isChat = preview.docType == DocType.OPEN_CHAT
                val isValidUuid = dialogUuid != UUIDUtils.NIL_UUID
                when {
                    !isValidUuid -> null
                    preview.isIntentSource ->
                        getMainActivityIntent()
                            .putExtra(
                                DeeplinkActionNode.EXTRA_DEEPLINK_ACTION,
                                OpenConversationDeeplinkAction(dialogUuid, messageUuid, isChat = isChat)
                            )
                            .putExtra(IntentAction.Extra.NAVIGATION_MENU_POSITION, MenuNavigationItemType.MESSAGES)

                    else ->
                        getConversationActivityIntent(
                            dialogUuid = dialogUuid,
                            messageUuid = messageUuid,
                            folderUuid = null,
                            participantsUuids = null,
                            files = null,
                            text = null,
                            document = null,
                            type = ConversationType.REGULAR,
                            isChat = isChat,
                            archivedDialog = false
                        )
                }
            }
        }

    override fun createConversationInformationFragment(
        conversationUuid: UUID,
        subtitle: String,
        isNewDialog: Boolean,
        isChat: Boolean,
        conversationName: String,
        permissions: Permissions,
        photoData: List<PhotoData>,
        isGroupConversation: Boolean,
        singleParticipant: ContactVM?
    ): Fragment {
        val args = Bundle().apply {
            putSerializable(CONVERSATION_UUID_KEY, conversationUuid)
            putString(CONVERSATION_PARTICIPANTS_SUBTITLE, subtitle)
            putBoolean(CONVERSATION_IS_NEW_KEY, isNewDialog)
            putBoolean(CONVERSATION_IS_CHAT_KEY, isChat)
            putString(CONVERSATION_TITLE_KEY, conversationName)
            putParcelable(CONVERSATION_CHAT_PERMISSION_KEY, permissions)
            putParcelableArrayList(CONVERSATION_PHOTO_DATA_KEY, photoData.asArrayList())
            putBoolean(CONVERSATION_IS_GROUP_KEY, isGroupConversation)
            putParcelable(CONVERSATION_SINGLE_PARTICIPANT, singleParticipant)
        }
        return ConversationInformationFragment.newInstance(args)
    }

    private const val QUERY_MESSAGE_UUID_KEY = "message"
}

package ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.data

import ru.tensor.sbis.communicator.generated.Permissions
import ru.tensor.sbis.communicator.themes_registry.ThemesRegistryFacade
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.data.toolbar.ConversationInformationToolbarData
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.store.ConversationInformationStore
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.utils.getParticipantViewData
import ru.tensor.sbis.design.profile_decl.person.PhotoData
import ru.tensor.sbis.persons.ContactVM
import java.util.UUID

/**
 * Данные, необходимые для отображения экрана информации диалога/канала.
 *
 * @param conversationUuid uuid переписки.
 * @param title название переписки.
 * @param subtitle подзаголовок.
 * @param photoDataList данные фото персон для постройки коллажа тулбара.
 * @param isChat переписка является чатом.
 * @param chatPermissions разрешения чата.
 * @param isNewConversation новая ли переписка.
 * @param isGroupConversation групповой ли диалог/канал.
 *
 * @author dv.baranov
 */
data class ConversationInformationData(
    val conversationUuid: UUID,
    val title: CharSequence,
    val subtitle: CharSequence,
    val photoDataList: List<PhotoData>,
    val isChat: Boolean,
    val chatPermissions: Permissions,
    val isNewConversation: Boolean,
    val isGroupConversation: Boolean,
    val singleParticipant: ContactVM?
) {

    /** @SelfDocumented */
    val canChangeTitle: Boolean
        get() = !isChat || chatPermissions.canChangeName

    /** @SelfDocumented */
    val canAddParticipants: Boolean
        get() = !isChat || chatPermissions.canAddParticipant
}

internal fun ConversationInformationData.mapToScreenState(): ConversationInformationStore.State {
    val availableTabs = getAvailableTabs(
        chatPermissions,
        isChat,
        isGroupConversation
    )
    val selectedTab = if (availableTabs.isEmpty()) ConversationInformationTab.DEFAULT else availableTabs.first()
    return ConversationInformationStore.State(
        toolbarData = ConversationInformationToolbarData(
            title = title,
            subtitle = subtitle,
            photoDataList = photoDataList,
            isChat = isChat,
            isGroup = isGroupConversation
        ),
        tabsViewState = ConversationInformationTabsViewState(
            selectedTab = selectedTab,
            availableTabs = availableTabs
        ),
        isGroupConversation = isGroupConversation,
        participantViewData = singleParticipant?.getParticipantViewData(),
        callRunning = ThemesRegistryFacade.themesRegistryDependency.callStateProviderFeature?.isCallRunning() ?: false
    )
}
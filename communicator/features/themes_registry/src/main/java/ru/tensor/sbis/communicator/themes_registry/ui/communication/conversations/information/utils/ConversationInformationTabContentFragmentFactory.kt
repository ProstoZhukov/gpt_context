package ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.utils

import android.os.Bundle
import androidx.fragment.app.Fragment
import ru.tensor.sbis.communicator.themes_registry.ThemesRegistryFacade.themesRegistryDependency
import ru.tensor.sbis.communicator.themes_registry.ThemesRegistryPlugin.communicatorFilesFragmentFactory
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.ConversationInformationFragment.Companion.KEY_NEW_INFORMATION_CONVERSATION_SCREEN
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.data.ConversationInformationData
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.data.ConversationInformationTab
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.data.toAttachmentTab
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.ui.links_list.ConversationLinksListFragment
import ru.tensor.sbis.communicator.themes_registry.ui.conversationparticipants.chatparticipants.presentation.view.ChatParticipantsHostFragment
import ru.tensor.sbis.communicator.themes_registry.ui.conversationparticipants.dialogparticipants.presentation.view.DialogParticipantsFragment
import ru.tensor.sbis.tasks.feature.TasksFeature.TasksListArgs.Dialog

/**
 * Фабрика создания фрагментов, являющихся контентом разделов экрана информации диалога/канала.
 *
 * @author dv.baranov
 */
internal class ConversationInformationTabContentFragmentFactory(
    private val parentFragmentArguments: Bundle? = null,
    private val conversationInformationData: ConversationInformationData,
) {

    /** Создать фрагмент по переданному id раздела. */
    fun createTabContentFragment(tabId: String): Fragment = when (tabId.toAttachmentTab()) {
        ConversationInformationTab.PARTICIPANTS -> {
            parentFragmentArguments?.apply {
                putBoolean(KEY_NEW_INFORMATION_CONVERSATION_SCREEN, true)
            }
            if (conversationInformationData.isChat) {
                ChatParticipantsHostFragment.newInstance(parentFragmentArguments)
            } else {
                DialogParticipantsFragment.newInstance(parentFragmentArguments)
            }
        }
        ConversationInformationTab.FILES ->
            // TODO https://online.sbis.ru/opendoc.html?guid=1b7ec88b-5da3-4615-85f4-b543cd7a78d9&client=3
            communicatorFilesFragmentFactory?.get()?.createCommunicatorFilesListFragment(
                conversationInformationData.conversationUuid
            ) ?: Fragment()
        ConversationInformationTab.TASKS ->
            themesRegistryDependency.tasksFeature?.createTasksListFragment(
                Dialog(conversationInformationData.conversationUuid)
            ) ?: Fragment()
        ConversationInformationTab.LINKS ->
            ConversationLinksListFragment.newInstance(
                conversationInformationData.conversationUuid
            )
        ConversationInformationTab.DEFAULT -> Fragment()
    }
}

package ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import org.apache.commons.lang3.StringUtils
import ru.tensor.sbis.communicator.design.R
import ru.tensor.sbis.communicator.generated.Permissions
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.data.ConversationInformationTab.DEFAULT
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.data.ConversationInformationTab.FILES
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.data.ConversationInformationTab.LINKS
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.data.ConversationInformationTab.PARTICIPANTS
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.data.ConversationInformationTab.TASKS
import ru.tensor.sbis.design.theme.res.PlatformSbisString
import timber.log.Timber

/**
 * Разделы на экране информации диалога/канала.
 *
 * @author dv.baranov
 */
internal enum class ConversationInformationTab(val id: String, val text: PlatformSbisString) {

    /** Участники. */
    PARTICIPANTS(PARTICIPANTS_TAB_ID, PlatformSbisString.Res(R.string.communicator_conversation_information_participants)),

    /** Файлы. */
    FILES(FILES_TAB_ID, PlatformSbisString.Res(R.string.communicator_conversation_information_files)),

    /** Задачи. */
    TASKS(TASKS_TAB_ID, PlatformSbisString.Res(R.string.communicator_conversation_information_tasks)),

    /** Ссылки. */
    LINKS(LINKS_TAB_ID, PlatformSbisString.Res(R.string.communicator_conversation_information_links)),

    /** Дефолтный таб, нужен для сценария отсутствия табов. */
    DEFAULT(DEFAULT_TAB_ID, PlatformSbisString.Value(StringUtils.EMPTY))
}

/**
 * Состояние раздела.
 *
 * @author dv.baranov
 */
@Parcelize
internal data class ConversationInformationTabsViewState(
    val selectedTab: ConversationInformationTab = DEFAULT,
    val availableTabs: List<ConversationInformationTab> = getAllTabs()
) : Parcelable

// TODO: вернуть вкладку задач в список.
private fun getAllTabs(): List<ConversationInformationTab> = listOf(PARTICIPANTS, FILES, LINKS)

/** Получить доступные вкладки исходя из прав. */
internal fun getAvailableTabs(
    permissions: Permissions,
    isChat: Boolean,
    isGroup: Boolean
) = buildList {
    val participantsAvailable = (permissions.canViewMembers || !isChat) && isGroup
    val documentsAvailable = permissions.canViewDocuments || !isChat
    // val tasksAvailable = documentsAvailable && themesRegistryDependency.tasksFeature != null
    if (participantsAvailable) add(PARTICIPANTS)
    if (documentsAvailable) add(FILES)
    // TODO: вернуть вкладку задач.
    //  Будет выполнено по задаче https://online.sbis.ru/opendoc.html?guid=e2c6b3c6-1f23-4fa6-87d9-1869d9f3a475&client=3
    // if (tasksAvailable) add(TASKS)
    if (documentsAvailable) add(LINKS)
}

/** Получить вкладку по её id. */
internal fun String.toAttachmentTab(): ConversationInformationTab = when (this) {
    PARTICIPANTS.id -> PARTICIPANTS
    FILES.id -> FILES
    TASKS.id -> TASKS
    LINKS.id -> LINKS
    DEFAULT.id -> DEFAULT
    else -> {
        Timber.e("Unexpected attachmentTabId")
        DEFAULT
    }
}

private const val PARTICIPANTS_TAB_ID = "participants_tab_id"
private const val FILES_TAB_ID = "files_tab_id"
private const val TASKS_TAB_ID = "tasks_tab_id"
private const val LINKS_TAB_ID = "links_tab_id"
private const val DEFAULT_TAB_ID = "default_tab_id"
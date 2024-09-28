package ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.store

import android.os.Parcelable
import com.arkivanov.mvikotlin.core.store.Store
import kotlinx.parcelize.Parcelize
import org.apache.commons.lang3.StringUtils
import ru.tensor.sbis.communication_decl.conversation_information.ConversationInformationFilesFilter.Companion.allFilters
import ru.tensor.sbis.communication_decl.conversation_information.ConversationInformationFilter
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.data.ConversationInformationTabsViewState
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.data.fab_options.ConversationInformationFabOption
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.data.toolbar.ConversationInformationOption
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.data.toolbar.ConversationInformationToolbarData
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.store.ConversationInformationStore.Intent
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.store.ConversationInformationStore.Label
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.store.ConversationInformationStore.State
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.ui.participant_view.ConversationInformationParticipantViewData
import ru.tensor.sbis.design.files_picker.decl.SbisPickedItem
import java.util.UUID

/**
 * Описывает действия ([Intent]), состояния ([State]) и сайд-эффекты ([Label]) для экрана информации диалога/канала.
 *
 * @author dv.baranov
 */
internal interface ConversationInformationStore :
    Store<Intent, State, Label> {

    /** @SelfDocumented */
    sealed interface Intent {

        /** Сменить вкладку. */
        data class TabSelected(val id: String) : Intent

        /** Навигация назад для событий приложения. */
        object NavigateBack : Intent

        /** Навигация назад при клике на системную кнопку. */
        object OnBackPressed : Intent

        /** Действие редактирования названия диалога/канала. */
        sealed interface EditTitle : Intent {

            /** Начать редактирование. */
            object Start : Intent

            /** Отменить редактирование. */
            object Cancel : Intent

            /** Завершить редактирование, сохранив название. */
            data class End(val newTitle: CharSequence?) : Intent
        }

        /** Действия поиска. */
        sealed interface Search : Intent {

            /** Изменить строку поиска. */
            data class QueryChanged(val query: String) : Search

            /** Открыть строку поиска. */
            object Open : Search

            /** Закрыть строку поиска. */
            object Close : Search
        }

        data class OpenMenu(val onOptionSelected: (option: ConversationInformationOption) -> Unit) : Intent

        data class ShowFabMenu(val optionAction: (option: ConversationInformationFabOption) -> Unit) : Intent

        data class MenuOptionSelected(val option: ConversationInformationOption) : Intent

        data class FabMenuOptionSelected(val option: ConversationInformationFabOption) : Intent

        data class CreateFolder(val folderName: String) : Intent

        object OpenFilter : Intent

        data class FilterSelected(val filters: List<ConversationInformationFilter>) : Intent

        data class StartCall(val isVideo: Boolean) : Intent

        object AddButtonClicked : Intent

        data class OpenProfile(val profileUuid: UUID) : Intent
    }

    /** @SelfDocumented */
    sealed interface Label {

        /** @SelfDocumented. */
        object NavigateBack : Label

        data class TabSelected(val id: String) : Label

        data class OpenFilter(val currentFilter: List<ConversationInformationFilter>) : Label

        data class CreateFolder(val folderName: String) : Label

        data class StartCall(val participants: List<UUID>, val isVideo: Boolean) : Label

        object AddButtonClicked : Label

        data class CopyLink(val url: String) : Label

        data class OpenParticipantSelection(val uuids: List<UUID>) : Label

        object OpenLinkAddition : Label

        data class OpenProfile(val profileUuid: UUID) : Label

        data class OpenMenu(val onOptionSelected: (option: ConversationInformationOption) -> Unit) : Label

        data class ShowFabMenu(val optionAction: (option: ConversationInformationFabOption) -> Unit) : Label

        object ShowFolderCreationDialog : Label

        object ShowFilesPicker : Label

        data class AddFiles(val selectedFiles: List<SbisPickedItem>, val compressImages: Boolean) : Label
    }

    /** @SelfDocumented */
    @Parcelize
    data class State(
        val toolbarData: ConversationInformationToolbarData = ConversationInformationToolbarData(),
        val searchQuery: String = StringUtils.EMPTY,
        val tabsViewState: ConversationInformationTabsViewState = ConversationInformationTabsViewState(),
        val isGroupConversation: Boolean = false,
        val callRunning: Boolean = false,
        val participantViewData: ConversationInformationParticipantViewData? = null,
        val filesFilter: List<ConversationInformationFilter> = allFilters,
    ) : Parcelable
}
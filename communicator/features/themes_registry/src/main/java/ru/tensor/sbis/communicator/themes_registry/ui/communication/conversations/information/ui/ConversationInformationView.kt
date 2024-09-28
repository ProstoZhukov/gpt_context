package ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.ui

import android.os.Bundle
import android.view.View
import com.arkivanov.mvikotlin.core.view.MviView
import org.apache.commons.lang3.StringUtils
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.data.ConversationInformationTabsViewState
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.data.toolbar.ConversationInformationOption
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.data.toolbar.ConversationInformationToolbarData
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.store.ConversationInformationStore.Intent
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.ui.participant_view.ConversationInformationParticipantViewData
import java.util.UUID

/**
 * Контракт view компонента MVI содержимого экрана информации диалога/канала.
 *
 * @author dv.baranov
 */
internal interface ConversationInformationView : MviView<ConversationInformationView.Model, ConversationInformationView.Event> {

    fun onSaveInstanceState(outState: Bundle): Bundle

    fun onViewStateRestored(savedInstanceState: Bundle?)

    /** @SelfDocumented */
    sealed interface Event {

        /** @SelfDocumented */
        fun toIntent(): Intent

        /** События редактирования названия диалога/канала. */
        sealed interface EditTitle : Event {

            /** Редактирование начали. */
            object Start : Event {

                override fun toIntent(): Intent = Intent.EditTitle.Start
            }

            /** Отмена редактирования. */
            object Cancel : Event {

                override fun toIntent(): Intent = Intent.EditTitle.Cancel
            }

            /** Завершение редактирования с сохранением названия. */
            data class End(val newTitle: CharSequence?) : Event {

                override fun toIntent(): Intent = Intent.EditTitle.End(newTitle)
            }
        }

        /** События поиска. */
        sealed interface Search : Event {

            /** Изменилась строка поиска. */
            data class QueryChanged(val query: String) : Event {
                override fun toIntent(): Intent = Intent.Search.QueryChanged(query)
            }

            /** Открытие строки поиска. */
            object Open : Event {
                override fun toIntent(): Intent = Intent.Search.Open
            }

            /** Закрыли строку поиска. */
            object Close : Event {
                override fun toIntent(): Intent = Intent.Search.Close
            }
        }

        /** Смена вкладки. */
        data class TabSelected(val id: String) : Event {
            override fun toIntent(): Intent = Intent.TabSelected(id)
        }

        /** @SelfDocumented */
        object NavigateBack : Event {

            override fun toIntent(): Intent = Intent.NavigateBack
        }

        data class OpenMenu(val onOptionSelected: (option: ConversationInformationOption) -> Unit) : Event {
            override fun toIntent(): Intent = Intent.OpenMenu(onOptionSelected)
        }

        data class MenuOptionSelected(val option: ConversationInformationOption) : Event {
            override fun toIntent(): Intent = Intent.MenuOptionSelected(option)
        }

        object OpenFilter : Event {
            override fun toIntent(): Intent = Intent.OpenFilter
        }

        data class StartCall(val isVideo: Boolean) : Event {

            override fun toIntent(): Intent = Intent.StartCall(isVideo)
        }

        object AddButtonClicked : Event {

            override fun toIntent(): Intent = Intent.AddButtonClicked
        }

        data class OpenProfile(val profileUuid: UUID) : Event {

            override fun toIntent(): Intent = Intent.OpenProfile(profileUuid)
        }
    }

    /** @SelfDocumented */
    data class Model(
        val toolbarData: ConversationInformationToolbarData = ConversationInformationToolbarData(),
        val searchQuery: String = StringUtils.EMPTY,
        val tabsViewState: ConversationInformationTabsViewState = ConversationInformationTabsViewState(),
        val isGroupConversation: Boolean = false,
        val participantViewData: ConversationInformationParticipantViewData? = null,
        val callRunning: Boolean = false
    ) {
        val isParticipantViewVisible
            get() = !isGroupConversation

        val isCallButtonsVisible = false
        // TODO: после выполнения задачи https://online.sbis.ru/opendoc.html?guid=d7094bbf-853f-43b0-a90f-d411c4b70f09&client=3
        //  вернуть возможность групповых звонков: убрать комментирование строки ниже, поменять вызывваемый метод звонка
        // get() = !callRunning && tabsViewState.selectedTab == PARTICIPANTS
    }

    /** @SelfDocumented */
    fun interface Factory : (View) -> ConversationInformationView
}
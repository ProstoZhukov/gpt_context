package ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.ui.links_list.store

import android.os.Parcelable
import android.view.View
import com.arkivanov.mvikotlin.core.store.Store
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.communicator.generated.LinkViewModel
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.ui.links_list.data.ConversationLinkOption
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.ui.links_list.store.ConversationLinksListStore.Intent
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.ui.links_list.store.ConversationLinksListStore.Label
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.ui.links_list.store.ConversationLinksListStore.State
import java.util.UUID

/**
 * Описывает действия ([Intent]), состояния ([State]) и сайд-эффекты ([Label]) для
 * экрана списка ссылок для информации о диалоге/канале.
 *
 * @author dv.baranov
 */
internal interface ConversationLinksListStore :
    Store<Intent, State, Label> {

    /** @SelfDocumented */
    sealed interface Intent {

        /** Показать меню опций при лонглике на ссылку. */
        data class ShowLinkMenu(val model: LinkViewModel, val anchor: View) : Intent

        /** Обработать выбор опции меню. */
        data class MenuOptionSelected(
            val option: ConversationLinkOption,
            val model: LinkViewModel,
            val anchor: View
        ) : Intent

        /** Обработать изменения в поисковой строке. */
        data class SearchQueryChanged(val query: String) : Intent
    }

    /** @SelfDocumented */
    sealed interface Label {

        /** Показать меню опций при лонглике на ссылку. */
        data class ShowLinkMenu(val model: LinkViewModel, val anchor: View) : Label

        /** Перейти к сообщению, к которому прикреплена ссылка. */
        data class GoToMessage(val uuid: UUID?) : Label
    }

    /** @SelfDocumented */
    @Parcelize
    class State : Parcelable
}
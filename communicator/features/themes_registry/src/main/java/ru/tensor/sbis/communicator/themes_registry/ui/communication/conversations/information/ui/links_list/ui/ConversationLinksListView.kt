package ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.ui.links_list.ui

import android.view.View
import com.arkivanov.mvikotlin.core.view.MviView
import ru.tensor.sbis.communicator.generated.LinkViewModel
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.ui.links_list.store.ConversationLinksListStore.Intent

/**
 * Контракт view компонента MVI содержимого экрана списка ссылок для информации о диалоге/канале.
 *
 * @author dv.baranov
 */
internal interface ConversationLinksListView : MviView<ConversationLinksListView.Model, ConversationLinksListView.Event> {

    /** @SelfDocumented */
    sealed interface Event {

        fun toIntent(): Intent

        /** Показать меню опций при лонглике на ссылку. */
        data class ShowLinkMenu(val model: LinkViewModel, val anchor: View) : Event {
            override fun toIntent(): Intent = Intent.ShowLinkMenu(model, anchor)
        }
    }

    /** @SelfDocumented */
    class Model

    /** @SelfDocumented */
    fun interface Factory : (View) -> ConversationLinksListView
}
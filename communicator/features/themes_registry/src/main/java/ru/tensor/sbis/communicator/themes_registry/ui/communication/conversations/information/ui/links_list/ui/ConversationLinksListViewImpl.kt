package ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.ui.links_list.ui

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.arkivanov.mvikotlin.core.view.BaseMviView
import kotlinx.coroutines.launch
import ru.tensor.sbis.communicator.themes_registry.databinding.CommunicatorFragmentConversationLinksListBinding
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.ui.links_list.ui.helpers.LinkItemLongClickHandler

/**
 * Реализация View содержимого экрана списка ссылок для информации о диалоге/канале.
 *
 * @author dv.baranov
 */
internal class ConversationLinksListViewImpl(
    binding: CommunicatorFragmentConversationLinksListBinding,
    listComponentFactory: ConversationLinksListComponentFactory,
    private val linkItemLongClickHandler: LinkItemLongClickHandler,
) : BaseMviView<ConversationLinksListView.Model, ConversationLinksListView.Event>(),
    ConversationLinksListView {

    init {
        listComponentFactory.create(binding.communicatorConversationLinksList)
        val lifecycleOwner: LifecycleOwner? = binding.root.findViewTreeLifecycleOwner()
        val scope = lifecycleOwner?.lifecycleScope
        scope?.launch {
            lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                scope.launch {
                    linkItemLongClickHandler.onLongItemClick.collect {
                        dispatch(ConversationLinksListView.Event.ShowLinkMenu(it.first, it.second))
                    }
                }
            }
        }
    }
}

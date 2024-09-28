package ru.tensor.sbis.communicator.themes_registry.ui.conversationparticipants.chatparticipants.presentation.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.tensor.sbis.base_components.BaseFragment
import ru.tensor.sbis.base_components.fragment.FragmentBackPress
import ru.tensor.sbis.communication_decl.conversation_information.ConversationInformationSearchableContent
import ru.tensor.sbis.communicator.common.util.castTo
import ru.tensor.sbis.communicator.themes_registry.R

/**
 * Фрагмент для хостинга экрана участников канала.
 *
 * @author dv.baranov
 */
internal class ChatParticipantsHostFragment : BaseFragment(), ConversationInformationSearchableContent {

    companion object {
        /** @SelfDocumented */
        fun newInstance(args: Bundle?) = ChatParticipantsHostFragment().apply {
            arguments = args
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? = inflater.inflate(R.layout.communicator_chat_participants_host_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (savedInstanceState == null) {
            openChatParticipantsFragment()
        }
    }

    private fun openChatParticipantsFragment() {
        if (childFragmentManager.fragments.isNotEmpty()) return
        val fragment = ChatParticipantsFragment.newInstance(this.arguments)
        childFragmentManager.beginTransaction()
            .add(R.id.communicator_chat_participants_content_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onBackPressed(): Boolean =
        childFragmentManager.fragments.lastOrNull()?.castTo<FragmentBackPress>()?.onBackPressed() ?: false

    override fun setSearchQuery(query: String) {
        childFragmentManager.fragments.lastOrNull()
            ?.castTo<ConversationInformationSearchableContent>()?.setSearchQuery(query)
    }

    /** @SelfDocumented */
    fun refreshParticipantsList() {
        childFragmentManager.fragments.filterIsInstance<ChatParticipantsFragment>().forEach {
            it.refreshParticipantsList()
        }
    }
}

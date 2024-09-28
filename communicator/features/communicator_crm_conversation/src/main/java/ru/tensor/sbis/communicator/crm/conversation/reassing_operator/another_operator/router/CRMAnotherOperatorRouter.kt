package ru.tensor.sbis.communicator.crm.conversation.reassing_operator.another_operator.router

import androidx.fragment.app.Fragment
import ru.tensor.sbis.communication_decl.crm.CrmChannelFilterType
import ru.tensor.sbis.communication_decl.crm.CrmChannelListCase
import ru.tensor.sbis.communicator.crm.conversation.CRMConversationPlugin.crmChannelsFragmentFactoryProvider
import ru.tensor.sbis.mvi_extension.router.Router
import ru.tensor.sbis.mvi_extension.router.fragment.FragmentRouter
import ru.tensor.sbis.communicator.crm.conversation.R

internal class CRMAnotherOperatorRouter : FragmentRouter(),
    Router<Fragment> {

    private val containerId: Int = R.id.crm_another_operator_overlay_container

    fun openChannelsForFilter() = execute {
        val crmChannelsFragmentFactory = crmChannelsFragmentFactoryProvider?.get() ?: return@execute
        val fragment = crmChannelsFragmentFactory.createCrmChannelListFragment(
            CrmChannelListCase.CrmChannelFilterCase(CrmChannelFilterType.OPERATOR)
        )
        childFragmentManager
            .beginTransaction()
            .add(containerId, fragment, fragment.tag)
            .addToBackStack(fragment.tag)
            .commit()
    }

    fun onBackPressed() = execute {
        this.requireActivity().onBackPressedDispatcher.onBackPressed()
    }
}
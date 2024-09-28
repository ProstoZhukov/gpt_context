package ru.tensor.sbis.communicator.communicator_crm_chat_list.filter.channels.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import ru.tensor.sbis.base_components.BaseFragment
import ru.tensor.sbis.base_components.fragment.FragmentBackPress
import ru.tensor.sbis.common.util.withArgs
import ru.tensor.sbis.communication_decl.crm.CrmChannelListCase
import ru.tensor.sbis.communication_decl.crm.CrmChannelListFragmentFactory
import ru.tensor.sbis.communicator.common.util.castTo
import ru.tensor.sbis.communicator.communicator_crm_chat_list.CRMChatListPlugin.commonSingletonComponentProvider
import ru.tensor.sbis.communicator.communicator_crm_chat_list.R
import ru.tensor.sbis.communicator.communicator_crm_chat_list.filter.CrmChatFilterContentContract
import ru.tensor.sbis.communicator.communicator_crm_chat_list.filter.channels.di.DaggerCRMChannelsComponent
import ru.tensor.sbis.communicator.communicator_crm_chat_list.ui.CRMChatListNotificationHelper

/**
 * Фрагмент перезначения чата CRM.
 *
 * @author da.zhukov
 */
internal class CRMChannelsFragment :
    BaseFragment(),
    FragmentBackPress,
    CrmChannelsListSectionClickDelegate,
    CrmChatFilterContentContract {

    companion object : CrmChannelListFragmentFactory {

        override fun createCrmChannelListFragment(case: CrmChannelListCase): Fragment {
            return CRMChannelsFragment().withArgs {
                putSerializable(CHANNEL_CASE, case)
            }
        }
    }

    private var controller: CRMChannelsController? = null

    private val case by lazy { arguments?.getSerializable(CHANNEL_CASE) as CrmChannelListCase }

    private val selectedItems by lazy {
        MutableLiveData(
            case.castTo<CrmChannelListCase.CrmChannelFilterCase>()?.currentFilter?.first?.toList() ?: emptyList()
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DaggerCRMChannelsComponent.factory().create(
            scope = this.lifecycleScope,
            viewModelStoreOwner = this,
            commonSingletonComponent = commonSingletonComponentProvider.get(),
            listCase = case,
            clickDelegate = this,
            selectedItems = selectedItems,
            crmChatListNotificationHelper = CRMChatListNotificationHelper(requireContext())
        ).also {
            controller = it.injector().inject(this, it.viewFactory)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.communicator_crm_channels_fragment, container, false)
    }

    override fun onDestroy() {
        super.onDestroy()
        controller = null
    }

    override fun onBackPressed(): Boolean {
        childFragmentManager.run {
            if (backStackEntryCount == 0) return super.onBackPressed()
            if (fragments.last().castTo<FragmentBackPress>()?.onBackPressed() == false) {
                popBackStack()
                return true
            }
        }
        return false
    }

    override fun onSectionClick() {
        controller?.onNotChoseClick()
    }

    override fun onResetButtonClick() {
        controller?.onResetButtonClick()
    }
}

private const val CHANNEL_CASE = "CHANNEL_CASE"

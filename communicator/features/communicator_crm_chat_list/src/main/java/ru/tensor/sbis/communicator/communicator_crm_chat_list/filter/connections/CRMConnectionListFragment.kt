package ru.tensor.sbis.communicator.communicator_crm_chat_list.filter.connections

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import ru.tensor.sbis.base_components.BaseFragment
import ru.tensor.sbis.base_components.fragment.FragmentBackPress
import ru.tensor.sbis.common.util.withArgs
import ru.tensor.sbis.communicator.common.util.castTo
import ru.tensor.sbis.communicator.common.util.requireCastTo
import ru.tensor.sbis.communicator.communicator_crm_chat_list.CRMChatListPlugin
import ru.tensor.sbis.communicator.communicator_crm_chat_list.filter.connections.di.DaggerCRMConnectionListComponent
import ru.tensor.sbis.communicator.communicator_crm_chat_list.R
import ru.tensor.sbis.communicator.communicator_crm_chat_list.filter.CrmChatFilterContentContract
import ru.tensor.sbis.communicator.communicator_crm_chat_list.filter.connections.ui.CRMConnectionListController
import java.util.UUID

/**
 * Фрагмент источников CRM.
 *
 * @author da.zhukov
 */
internal class CRMConnectionListFragment : BaseFragment(),
    CrmChatFilterContentContract {

    companion object {
        private const val SELECTED_ITEMS = "SELECTED_ITEMS"
        /** @SelfDocumented */
        fun newInstance(selectedItems: ArrayList<UUID> = arrayListOf()): CRMConnectionListFragment =
            CRMConnectionListFragment().withArgs {
                putSerializable(SELECTED_ITEMS, selectedItems)
            }
    }

    private var controller: CRMConnectionListController? = null

    private val selectedItems by lazy {
        MutableLiveData(arguments?.getSerializable(SELECTED_ITEMS)?.castTo<List<UUID>>() ?: emptyList())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DaggerCRMConnectionListComponent.factory().create(
            fragment = this,
            viewModelStoreOwner = this,
            scope = this.lifecycleScope,
            commonSingletonComponent = CRMChatListPlugin.commonSingletonComponentProvider.get(),
            selectedItems = selectedItems
        ).also {
            controller = it.injector().inject(this@CRMConnectionListFragment, it.viewFactory)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.communicator_crm_connection_fragment, container, false)
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

    override fun onResetButtonClick() {
        controller?.onResetButtonClick()
    }
}
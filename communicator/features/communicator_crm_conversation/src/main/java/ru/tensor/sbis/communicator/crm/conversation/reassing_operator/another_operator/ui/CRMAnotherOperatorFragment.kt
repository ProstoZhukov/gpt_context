package ru.tensor.sbis.communicator.crm.conversation.reassing_operator.another_operator.ui

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import ru.tensor.sbis.base_components.BaseFragment
import ru.tensor.sbis.base_components.fragment.FragmentBackPress
import ru.tensor.sbis.common.util.withArgs
import ru.tensor.sbis.communication_decl.crm.CrmChannelListFragmentFactory
import ru.tensor.sbis.communication_decl.crm.CrmChannelListFragmentFactory.Companion.CRM_CHANNEL_OPERATOR_RESULT
import ru.tensor.sbis.communicator.common.util.castTo
import ru.tensor.sbis.communicator.crm.conversation.R
import ru.tensor.sbis.communicator.crm.conversation.reassing_operator.another_operator.di.DaggerCRMAnotherOperatorComponent
import ru.tensor.sbis.communicator.crm.conversation.CRMConversationPlugin.commonSingletonComponentProvider
import ru.tensor.sbis.communicator.declaration.crm.model.CRMChatFilterModel
import ru.tensor.sbis.communicator.declaration.crm.providers.CRMAnotherOperatorFragmentFactory
import ru.tensor.sbis.communicator.declaration.crm.providers.CRMAnotherOperatorParams
import java.util.UUID

/**
 * Фрагмент перезначения чата CRM.
 *
 * @author da.zhukov
 */
internal class CRMAnotherOperatorFragment : BaseFragment(),
    FragmentBackPress
{
    companion object : CRMAnotherOperatorFragmentFactory {
        private const val OPEN_PARAMS = "OPEN_PARAMS"

        override fun createCRMAnotherOperatorFragment(
            params: CRMAnotherOperatorParams
        ): Fragment {
            return CRMAnotherOperatorFragment().withArgs {
                putParcelable(OPEN_PARAMS, params)
            }
        }
    }

    private var controller: CRMAnotherOperatorController? = null

    private val openParams by lazy { if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        requireArguments().getParcelable(OPEN_PARAMS, CRMAnotherOperatorParams::class.java)!!
    } else {
        requireArguments().getParcelable(OPEN_PARAMS)!!
    } }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DaggerCRMAnotherOperatorComponent.factory().create(
            scope = this.lifecycleScope,
            viewModelStoreOwner = this,
            commonSingletonComponent = commonSingletonComponentProvider.get(),
            params = openParams
        ).also {
            controller = it.injector().inject(this, it.viewFactory)
            childFragmentManager.setFragmentResultListener(
                CRM_CHANNEL_OPERATOR_RESULT,
                this@CRMAnotherOperatorFragment,
                controller!!
            )
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.communicator_crm_another_operator_fragment, container, false)
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
}
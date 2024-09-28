package ru.tensor.sbis.communicator.communicator_crm_chat_list.filter

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.base_components.BaseFragment
import ru.tensor.sbis.base_components.fragment.FragmentBackPress
import ru.tensor.sbis.clients_feature.ClientsMultiSelectionContract
import ru.tensor.sbis.clients_feature.data.CrmClient
import ru.tensor.sbis.common.util.withArgs
import ru.tensor.sbis.communicator.common.util.castTo
import ru.tensor.sbis.communicator.communicator_crm_chat_list.R
import ru.tensor.sbis.communicator.communicator_crm_chat_list.databinding.CommunicatorCrmChatFilterFragmentBinding
import ru.tensor.sbis.communicator.communicator_crm_chat_list.filter.di.DaggerCrmChatFilterComponent
import ru.tensor.sbis.communicator.declaration.crm.model.CRMChatFilterModel
import ru.tensor.sbis.design_dialogs.dialogs.content.Content
import ru.tensor.sbis.design_dialogs.dialogs.content.ContentCreatorParcelable
import ru.tensor.sbis.design_dialogs.movablepanel.MovablePanelPeekHeight
import ru.tensor.sbis.modalwindows.movable_container.ContainerMovableDialogFragment

/**
 * Экран фильтров crm чата.
 *
 * @author da.zhukov
 */
internal class CrmChatFilterFragment : BaseFragment(), Content,
    FragmentBackPress,
    ClientsMultiSelectionContract {

    /**
     * Реализация создателя экземпляра фрагмента.
     */
    @Parcelize
    private class ContentCreator(val filterModel: CRMChatFilterModel) : ContentCreatorParcelable {

        override fun createFragment() =
            CrmChatFilterFragment().withArgs {
                putParcelable(CRM_CHAT_FILTER_MODEL_ARG, filterModel)
            }
    }

    companion object {

        private const val CRM_CHAT_FILTER_MODEL_ARG = "CRM_CHAT_FILTER_MODEL_ARG"

        /**
         * Запрос результата.
         */
        const val REQUEST = "REQUEST"

        /**
         * Ключ для доступа к результату .
         */
        const val RESULT_UUIDS = "RESULT_UUIDS"

        /**
         * Ключ для доступа к результату .
         */
        const val RESULT_NAMES = "RESULT_NAMES"


        /**
         * Создаёт фрагмент фильтра.
         */
        fun newInstance(filterModel: CRMChatFilterModel) =
            ContainerMovableDialogFragment.Builder()
                .setContentCreator(ContentCreator(filterModel))
                .setExpandedPeekHeight(MovablePanelPeekHeight.FitToContent())
                .setDefaultHeaderPaddingEnabled(true)
                .build()

    }

    private lateinit var crmChatFilterView: CrmChatFilterView
    private var controller: CrmChatFilterController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DaggerCrmChatFilterComponent.factory().create(
            viewFactory = {
                val root: View = it.findViewById(R.id.crm_chat_filters_root)
                CrmChatFilterViewImpl(
                    this,
                    CommunicatorCrmChatFilterFragmentBinding.bind(root)
                ).also { view ->
                    crmChatFilterView = view
                    childFragmentManager.setFragmentResultListener(
                        REQUEST,
                        this@CrmChatFilterFragment,
                        view
                    )
                }
            },
            filterModel = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requireArguments().getParcelable(CRM_CHAT_FILTER_MODEL_ARG, CRMChatFilterModel::class.java)!!
            } else {
                requireArguments().getParcelable(CRM_CHAT_FILTER_MODEL_ARG)!!
            },
            context = requireContext()
        ).also {
            controller = it.injector().inject(this@CrmChatFilterFragment)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.communicator_crm_chat_filter_fragment, container, false)

    override fun onReturnClients(clients: List<CrmClient.Client>) {
        childFragmentManager.setFragmentResult(
            REQUEST,
            bundleOf(
                RESULT_UUIDS to clients.mapTo(ArrayList(clients.size)) { it.uuid },
                RESULT_NAMES to clients.mapTo(ArrayList(clients.size)) { it.name }
            )
        )
    }

    override fun onClientsChanged(clients: Set<CrmClient.Client>) {
        childFragmentManager.setFragmentResult(
            REQUEST,
            bundleOf(
                RESULT_UUIDS to clients.mapTo(ArrayList(clients.size)) { it.uuid },
                RESULT_NAMES to clients.mapTo(ArrayList(clients.size)) { it.name }
            )
        )
    }

    override fun onBackPressed(): Boolean {
        childFragmentManager.run {
            if (backStackEntryCount == 0) return super.onBackPressed()
            if (fragments.last().castTo<FragmentBackPress>()?.onBackPressed() == false) {
                popBackStack()
                controller?.onBackPressed()
                return true
            }
        }
        return false
    }
}
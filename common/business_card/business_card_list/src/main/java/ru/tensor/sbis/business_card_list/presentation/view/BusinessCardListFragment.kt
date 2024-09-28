package ru.tensor.sbis.business_card_list.presentation.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import ru.tensor.sbis.android_ext_decl.getSerializableUniversally
import ru.tensor.sbis.base_components.BaseFragment
import ru.tensor.sbis.business_card_list.BusinessCardListPlugin
import ru.tensor.sbis.business_card_list.R
import ru.tensor.sbis.business_card_list.databinding.BusinessCardListFragmentBinding
import ru.tensor.sbis.business_card_list.di.view.BusinessCardListInjector
import ru.tensor.sbis.business_card_list.di.view.BusinessCardListViewComponent
import ru.tensor.sbis.business_card_list.di.view.DaggerBusinessCardListViewComponent
import ru.tensor.sbis.business_card_list.presentation.controller.BusinessCardListController
import ru.tensor.sbis.common.util.withArgs
import java.util.UUID
import javax.inject.Inject

/** Fragment реестра визиток */
internal class BusinessCardListFragment : BaseFragment() {

    companion object {
        /**@SelfDocumented*/
        fun newInstance(personUuid: UUID): BusinessCardListFragment =
            BusinessCardListFragment().withArgs {
                putSerializable(BUSINESS_CARD_LIST_PARAMS, personUuid)
                putBoolean(ARG_ADD_PADDING, true)
            }

        private const val BUSINESS_CARD_LIST_PARAMS = "BUSINESS_CARD_LIST_PARAMS"
    }

    private val personUuid: UUID by lazy { requireArguments().getSerializableUniversally(BUSINESS_CARD_LIST_PARAMS)!! }

    /**@SelfDocumented*/
    @Inject
    lateinit var controllerInjector: BusinessCardListInjector

    override var rootContainerForTopPadding: Int? = R.id.business_card_list_toolbar

    private lateinit var businessCardListView: BusinessCardListView

    private var controller: BusinessCardListController? = null

    private var _binding: BusinessCardListFragmentBinding? = null
    private val binding: BusinessCardListFragmentBinding
        get() = _binding!!

    private val component: BusinessCardListViewComponent by lazy {
        DaggerBusinessCardListViewComponent.factory().create(
            BusinessCardListPlugin.singletonComponent,
            this,
            lifecycleScope,
            R.id.business_card_list_fragment_container,
            personUuid
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        controller = component.injector().inject(this) {
            BusinessCardListView(binding.root) { view ->
                component.inject(view)
                businessCardListView = view
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = BusinessCardListFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

}
package ru.tensor.sbis.business_card.presentation.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.tensor.sbis.android_ext_decl.getParcelableUniversally
import ru.tensor.sbis.base_components.BaseFragment
import ru.tensor.sbis.business_card.BusinessCardPlugin
import ru.tensor.sbis.business_card.R
import ru.tensor.sbis.business_card.databinding.BusinessCardFragmentBinding
import ru.tensor.sbis.business_card.di.view.BusinessCardInjector
import ru.tensor.sbis.business_card.di.view.BusinessCardViewComponent
import ru.tensor.sbis.business_card.di.view.DaggerBusinessCardViewComponent
import ru.tensor.sbis.business_card_host_decl.ui.model.BusinessCard
import ru.tensor.sbis.common.util.withArgs
import javax.inject.Inject

/** Fragment карточки визитки */
internal class BusinessCardFragment : BaseFragment() {

    companion object {
        /**@SelfDocumented*/
        fun newInstance(params: BusinessCard): BusinessCardFragment =
            BusinessCardFragment().withArgs {
                putParcelable(BUSINESS_CARD_PARAMS, params)
                putBoolean(ARG_ADD_PADDING, true)
            }

        private const val BUSINESS_CARD_PARAMS = "BUSINESS_CARD_PARAMS"
    }

    /**@SelfDocumented*/
    @Inject
    lateinit var controllerInjector: BusinessCardInjector

    override var rootContainerForTopPadding: Int? = R.id.business_card_toolbar

    private val params: BusinessCard by lazy { requireArguments().getParcelableUniversally(BUSINESS_CARD_PARAMS)!! }

    private var _binding: BusinessCardFragmentBinding? = null
    private val binding: BusinessCardFragmentBinding
        get() = _binding!!

    private val component: BusinessCardViewComponent by lazy {
        DaggerBusinessCardViewComponent.factory().create(
            BusinessCardPlugin.singletonComponent,
            this,
            R.id.business_card_fragment_container
        )
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        component.inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        params.let {
            controllerInjector.inject(this@BusinessCardFragment) {
                BusinessCardView(binding, params)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = BusinessCardFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
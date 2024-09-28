package ru.tensor.sbis.business_card_host.presentation.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.fragment.app.FragmentContainerView
import androidx.lifecycle.lifecycleScope
import ru.tensor.sbis.android_ext_decl.getSerializableUniversally
import ru.tensor.sbis.base_components.BaseFragment
import ru.tensor.sbis.business_card_host.BusinessCardHostPlugin
import ru.tensor.sbis.business_card_host.R
import ru.tensor.sbis.business_card_host.di.view.BusinessCardHostInjector
import ru.tensor.sbis.business_card_host.di.view.BusinessCardHostViewComponent
import ru.tensor.sbis.business_card_host.di.view.DaggerBusinessCardHostViewComponent
import ru.tensor.sbis.business_card_host.presentation.controller.BusinessCardHostController
import ru.tensor.sbis.common.util.withArgs
import java.util.UUID
import javax.inject.Inject

/** Fragment хоста визиток */
internal class BusinessCardHostFragment : BaseFragment() {

    companion object {
        /**@SelfDocumented*/
        fun newInstance(personUuid: UUID): BusinessCardHostFragment =
            BusinessCardHostFragment().withArgs {
                putSerializable(BUSINESS_CARD_HOST_PERSON_UUID, personUuid)
            }

        private const val BUSINESS_CARD_HOST_PERSON_UUID = "BUSINESS_CARD_HOST_PERSON_UUID"
    }

    private lateinit var rootView: FragmentContainerView

    private val personUuid: UUID by lazy { requireArguments().getSerializableUniversally(BUSINESS_CARD_HOST_PERSON_UUID)!! }

    /**@SelfDocumented*/
    @Inject
    lateinit var controllerInjector: BusinessCardHostInjector

    private var controller: BusinessCardHostController? = null

    private val component: BusinessCardHostViewComponent by lazy {
        DaggerBusinessCardHostViewComponent.factory().create(
            BusinessCardHostPlugin.singletonComponent,
            this,
            lifecycleScope,
            R.id.business_card_host_fragment_container_id,
            personUuid
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        controller = component.injector().inject(this) { view ->
            component.inject(this)
            this
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        rootView = FragmentContainerView(requireContext()).apply {
            id = R.id.business_card_host_fragment_container_id

            layoutParams = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT
            )
        }
        return rootView
    }

}
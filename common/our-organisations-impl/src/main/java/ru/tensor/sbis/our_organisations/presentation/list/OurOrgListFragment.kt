package ru.tensor.sbis.our_organisations.presentation.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ru.tensor.sbis.android_ext_decl.getParcelableUniversally
import ru.tensor.sbis.android_ext_decl.getSerializableUniversally
import ru.tensor.sbis.base_components.BaseFragment
import ru.tensor.sbis.base_components.fragment.FragmentBackPress
import ru.tensor.sbis.common.util.withArgs
import ru.tensor.sbis.our_organisations.OurOrgSingletonComponentProvider
import ru.tensor.sbis.our_organisations.R
import ru.tensor.sbis.our_organisations.data.OurOrgItemType
import ru.tensor.sbis.our_organisations.databinding.OurOrgFragmentListBinding
import ru.tensor.sbis.our_organisations.feature.OurOrgUnnecessaryChoiceHostContract
import ru.tensor.sbis.our_organisations.feature.data.OurOrgParams
import ru.tensor.sbis.our_organisations.presentation.list.ui.OurOrgListController
import ru.tensor.sbis.our_organisations.presentation.list.ui.OurOrgListViewImpl

/**
 * Фрагмент со списком организаций.
 *
 * @author mv.ilin
 */
internal class OurOrgListFragment :
    BaseFragment(),
    OurOrgUnnecessaryChoiceHostContract.ActionHandler,
    FragmentBackPress {

    companion object {

        const val RESULT_KEY = "RESULT_KEY"
        const val ARG_REQUEST_KEY = "REQUEST_KEY"
        private const val ARG_INIT_PARAMS = "INIT_PARAMS"
        private const val ARG_OUR_ORG_ITEM_TYPE = "OUR_ORG_ITEM_TYPE"
        private const val ARG_DISCARD_RESULT_AFTER_CLICK = "ARG_DISCARD_RESULT_AFTER_CLICK"

        fun newInstance(
            requestKey: String,
            initParams: OurOrgParams,
            ourOrgItemType: OurOrgItemType,
            discardResultAfterClick: Boolean
        ): Fragment {
            return OurOrgListFragment().withArgs {
                putString(ARG_REQUEST_KEY, requestKey)
                putParcelable(ARG_INIT_PARAMS, initParams)
                putSerializable(ARG_OUR_ORG_ITEM_TYPE, ourOrgItemType)
                putBoolean(ARG_DISCARD_RESULT_AFTER_CLICK, discardResultAfterClick)
            }
        }

        fun extractResult(data: Bundle) =
            data.getParcelableUniversally<OurOrgListContract.OurOrgListResult>(RESULT_KEY)!!
    }

    private lateinit var viewController: OurOrgListController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val initParams = requireArguments().getParcelableUniversally<OurOrgParams>(ARG_INIT_PARAMS)!!
        val ourOrgItemType = requireArguments().getSerializableUniversally<OurOrgItemType>(ARG_OUR_ORG_ITEM_TYPE)!!
        val discardResultAfterClick = requireArguments().getBoolean(ARG_DISCARD_RESULT_AFTER_CLICK)

        viewController = OurOrgSingletonComponentProvider.get()
            .ourOrgListModuleNew()
            .create(
                initParams = initParams,
                discardResultAfterClick = discardResultAfterClick,
                viewFactory = {
                    OurOrgListViewImpl(OurOrgFragmentListBinding.bind(it), initParams, ourOrgItemType)
                }
            ).injector().inject(this)
    }

    override fun onReset() {
        viewController.onReset()
    }

    override fun onApply() {
        viewController.onApply()
    }

    override fun onStart() {
        super.onStart()
        viewController.onStart()
    }

    override fun onStop() {
        viewController.onStop()
        super.onStop()
    }

    override fun onDestroy() {
        viewController.cancel()
        super.onDestroy()
    }

    override fun onBackPressed(): Boolean {
        return viewController.onBackPressed()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.our_org_fragment_list, container, false)
    }
}

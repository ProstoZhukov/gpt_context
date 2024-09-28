package ru.tensor.sbis.our_organisations.presentation.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.setFragmentResult
import ru.tensor.sbis.android_ext_decl.getParcelableUniversally
import ru.tensor.sbis.base_components.fragment.FragmentBackPress
import ru.tensor.sbis.common.util.withArgs
import ru.tensor.sbis.our_organisations.R
import ru.tensor.sbis.our_organisations.data.OurOrgItemType
import ru.tensor.sbis.our_organisations.databinding.OurOrgFragmentUnnecessaryChoiceBinding
import ru.tensor.sbis.our_organisations.feature.OurOrgNecessaryFragmentResult
import ru.tensor.sbis.our_organisations.feature.data.Organisation
import ru.tensor.sbis.our_organisations.feature.data.OurOrgParams
import ru.tensor.sbis.our_organisations.presentation.contract.register
import ru.tensor.sbis.our_organisations.presentation.list.OurOrgListContract
import ru.tensor.sbis.our_organisations.presentation.view.BaseOurOrgChoiceFragment.Companion.OUR_ORG_ITEM_TYPE_KEY
import ru.tensor.sbis.our_organisations.presentation.view.BaseOurOrgChoiceFragment.Companion.OUR_ORG_PARAMS_KEY

/**
 * Фрагмент с обязательным выбором из списка организаций.
 *
 * @author mv.ilin
 */
internal class OurOrgNecessaryChoiceFragment :
    Fragment(),
    BaseOurOrgChoiceFragment,
    FragmentBackPress {

    internal companion object {
        private const val RESULT_KEY = "RESULT_KEY"
        private const val ARG_REQUEST_KEY = "REQUEST_KEY"

        fun createInstance(
            requestKey: String,
            ourOrgParams: OurOrgParams,
            ourOrgItemType: OurOrgItemType
        ) = OurOrgNecessaryChoiceFragment().withArgs {
            putString(ARG_REQUEST_KEY, requestKey)
            putSerializable(OUR_ORG_ITEM_TYPE_KEY, ourOrgItemType)
            putParcelable(OUR_ORG_PARAMS_KEY, ourOrgParams)
        }

        fun extractResult(data: Bundle) = data.getParcelableUniversally<OurOrgNecessaryFragmentResult>(RESULT_KEY)!!
    }

    override val baseFragmentManager: FragmentManager by lazy { childFragmentManager }
    override val containerViewId: Int = R.id.body
    override val discardResultAfterClick: Boolean = true
    override val ourOrgListContract: OurOrgListContract.Factory =
        OurOrgListContract().register(this, ::handleOurOrgListResult)

    override fun onShowContent() {
        setFragmentResult(
            requireArguments().getString(ARG_REQUEST_KEY)!!,
            bundleOf(RESULT_KEY to OurOrgNecessaryFragmentResult.OnShowContent)
        )
    }

    override fun onClickOrganisation(organisations: List<Organisation>) {
        setFragmentResult(
            requireArguments().getString(ARG_REQUEST_KEY)!!,
            bundleOf(RESULT_KEY to OurOrgNecessaryFragmentResult.OrganizationChanged(organisations.first()))
        )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return OurOrgFragmentUnnecessaryChoiceBinding.inflate(inflater, container, false).root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        createOurOrgListFragment()
    }

    override fun onBackPressed(): Boolean {
        val fragment = getCurrentFragment()
        return fragment is FragmentBackPress && fragment.onBackPressed()
    }
}

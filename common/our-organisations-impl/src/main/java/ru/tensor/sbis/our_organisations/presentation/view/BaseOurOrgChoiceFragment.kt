package ru.tensor.sbis.our_organisations.presentation.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import ru.tensor.sbis.android_ext_decl.getParcelableUniversally
import ru.tensor.sbis.android_ext_decl.getSerializableUniversally
import ru.tensor.sbis.our_organisations.data.OurOrgItemType
import ru.tensor.sbis.our_organisations.feature.data.Organisation
import ru.tensor.sbis.our_organisations.feature.data.OurOrgParams
import ru.tensor.sbis.our_organisations.presentation.list.OurOrgListContract

/**
 * Базовый класс для фрагмента списка организаций.
 *
 * @author mv.ilin
 */
internal interface BaseOurOrgChoiceFragment {

    companion object {
        private const val OUR_ORG_FRAGMENT_LIST_KEY = "OUR_ORG_FRAGMENT_LIST_KEY"
        const val OUR_ORG_PARAMS_KEY = "OUR_ORG_PARAMS_KEY"
        const val OUR_ORG_ITEM_TYPE_KEY = "OUR_ORG_ITEM_TYPE_KEY"
    }

    fun requireArguments(): Bundle

    val baseFragmentManager: FragmentManager
    val containerViewId: Int
    val discardResultAfterClick: Boolean

    val ourOrgListContract: OurOrgListContract.Factory

    fun onShowContent() {}
    fun onClickOrganisation(organisations: List<Organisation>) {}
    fun onReturnOrganisation(organisations: List<Organisation>) {}

    fun getCurrentFragment(): Fragment? = baseFragmentManager.findFragmentByTag(OUR_ORG_FRAGMENT_LIST_KEY)

    fun createOurOrgListFragment() {
        getCurrentFragment().also { fragment ->
            if (fragment != null) return

            val arguments = requireArguments()
            val ourOrgItemType =
                arguments.getSerializableUniversally<OurOrgItemType>(OUR_ORG_ITEM_TYPE_KEY) as OurOrgItemType
            val ourOrgParams =
                arguments.getParcelableUniversally<OurOrgParams>(OUR_ORG_PARAMS_KEY) ?: OurOrgParams(listOf())

            baseFragmentManager.beginTransaction()
                .add(
                    containerViewId,
                    ourOrgListContract.create(
                        params = ourOrgParams,
                        ourOrgItemType = ourOrgItemType,
                        discardResultAfterClick = discardResultAfterClick
                    ),
                    OUR_ORG_FRAGMENT_LIST_KEY
                )
                .commit()
        }
    }

    fun handleOurOrgListResult(result: OurOrgListContract.OurOrgListResult) = when (result) {
        is OurOrgListContract.OurOrgListResult.OrganizationChanged -> onClickOrganisation(result.organisations)
        is OurOrgListContract.OurOrgListResult.OnReturnOrganisation -> onReturnOrganisation(result.organisations)
        is OurOrgListContract.OurOrgListResult.OnShowContent -> onShowContent()
    }
}

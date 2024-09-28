package ru.tensor.sbis.our_organisations.presentation.list

import android.os.Bundle
import android.os.Parcelable
import androidx.fragment.app.Fragment
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.our_organisations.data.OurOrgItemType
import ru.tensor.sbis.our_organisations.feature.data.Organisation
import ru.tensor.sbis.our_organisations.feature.data.OurOrgParams
import ru.tensor.sbis.our_organisations.presentation.contract.DefaultFragmentContract
import ru.tensor.sbis.our_organisations.presentation.list.OurOrgListContract.Factory
import ru.tensor.sbis.our_organisations.presentation.list.OurOrgListContract.OurOrgListResult

private const val DEFAULT_REQUEST_KEY = "OUR_ORG_LIST_REQUEST_KEY"

/**
 * Контракт списка организаций
 * @author mv.ilin
 */
internal class OurOrgListContract(
    override val requestKey: String = DEFAULT_REQUEST_KEY
) : DefaultFragmentContract<Factory, OurOrgListResult>() {

    companion object {
        const val ITEMS_PAGE = 30
    }

    fun interface Factory {
        fun create(
            params: OurOrgParams,
            ourOrgItemType: OurOrgItemType,
            discardResultAfterClick: Boolean
        ): Fragment
    }

    sealed interface OurOrgListResult : Parcelable {
        @Parcelize
        class OrganizationChanged(val organisations: List<Organisation>) : OurOrgListResult

        @Parcelize
        class OnReturnOrganisation(val organisations: List<Organisation>) : OurOrgListResult

        @Parcelize
        object OnShowContent : OurOrgListResult
    }

    override fun extractResult(data: Bundle) = OurOrgListFragment.extractResult(data)

    override fun getFactory() = Factory { params, ourOrgItemType, discardResultAfterClick ->
        OurOrgListFragment.newInstance(requestKey, params, ourOrgItemType, discardResultAfterClick)
    }
}

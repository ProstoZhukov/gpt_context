package ru.tensor.sbis.our_organisations.presentation.contract

import android.os.Bundle
import ru.tensor.sbis.our_organisations.data.OurOrgItemType
import ru.tensor.sbis.our_organisations.feature.OurOrgFragmentFactory
import ru.tensor.sbis.our_organisations.feature.OurOrgNecessaryFragmentResult
import ru.tensor.sbis.our_organisations.presentation.view.OurOrgNecessaryChoiceFragment

private const val DEFAULT_REQUEST_KEY = "OUR_ORG_LIST_REQUEST_KEY"

/**
 * Контракт нашей организации с обязательным выбором
 * @author mv.ilin
 */
internal class OurOrgWithNecessaryChoiceFragmentContract(
    private val ourOrgItemType: OurOrgItemType,
    override val requestKey: String = DEFAULT_REQUEST_KEY
) : DefaultFragmentContract<OurOrgFragmentFactory, OurOrgNecessaryFragmentResult>() {

    override fun extractResult(data: Bundle) = OurOrgNecessaryChoiceFragment.extractResult(data)

    override fun getFactory() = OurOrgFragmentFactory { params ->
        OurOrgNecessaryChoiceFragment.createInstance(requestKey, params, ourOrgItemType)
    }
}

package ru.tensor.sbis.our_organisations.presentation.contract

import android.os.Bundle
import ru.tensor.sbis.our_organisations.data.OurOrgItemType
import ru.tensor.sbis.our_organisations.feature.OurOrgFragmentFactory
import ru.tensor.sbis.our_organisations.feature.OurOrgUnnecessaryFragmentResult
import ru.tensor.sbis.our_organisations.presentation.view.OurOrgUnnecessaryChoiceFragment

private const val DEFAULT_REQUEST_KEY = "OUR_ORG_LIST_REQUEST_KEY"

/**
 * Контракт нашей организации с не обязательным выбором
 * @author mv.ilin
 */
internal class OurOrgWithUnnecessaryChoiceFragmentContract(
    private val ourOrgItemType: OurOrgItemType,
    override val requestKey: String = DEFAULT_REQUEST_KEY
) : DefaultFragmentContract<OurOrgFragmentFactory, OurOrgUnnecessaryFragmentResult>() {

    override fun extractResult(data: Bundle) = OurOrgUnnecessaryChoiceFragment.extractResult(data)

    override fun getFactory() = OurOrgFragmentFactory { params ->
        OurOrgUnnecessaryChoiceFragment.createInstance(requestKey, params, ourOrgItemType)
    }
}

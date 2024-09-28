package ru.tensor.sbis.our_organisations.presentation.contract

import android.os.Bundle
import ru.tensor.sbis.design_dialogs.dialogs.container.bottomsheet.ContainerBottomSheet
import ru.tensor.sbis.our_organisations.data.OurOrgItemType
import ru.tensor.sbis.our_organisations.feature.OurOrgSelectionWindowFactory
import ru.tensor.sbis.our_organisations.feature.OurOrgUnnecessaryFragmentResult
import ru.tensor.sbis.our_organisations.presentation.view.OurOrgUnnecessaryChoiceSelectionWindow

private const val DEFAULT_REQUEST_KEY = "OUR_ORG_LIST_REQUEST_KEY"

/**
 * Контракт нашей организации с не обязательным выбором
 * @author mv.ilin
 */
internal class OurOrgWithUnnecessaryChoiceSelectionWindowContract(
    private val ourOrgItemType: OurOrgItemType,
    override val requestKey: String = DEFAULT_REQUEST_KEY
) : DefaultFragmentContract<OurOrgSelectionWindowFactory, OurOrgUnnecessaryFragmentResult>() {

    override fun extractResult(data: Bundle) = OurOrgUnnecessaryChoiceSelectionWindow.extractFragmentResult(data)

    override fun getFactory() = OurOrgSelectionWindowFactory { params ->

        val creator = OurOrgUnnecessaryChoiceSelectionWindow.Creator(
            requestKey = requestKey,
            ourOrgParams = params,
            ourOrgItemType = ourOrgItemType
        )

        ContainerBottomSheet().instant(false)
            .fullScreenInLandscape(false)
            .cancelable(false)
            .setContentCreator(creator)
    }
}

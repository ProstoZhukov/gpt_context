package ru.tensor.sbis.our_organisations.presentation

import androidx.fragment.app.Fragment
import ru.tensor.sbis.our_organisations.data.OurOrgItemType
import ru.tensor.sbis.our_organisations.feature.OurOrgFragmentFactory
import ru.tensor.sbis.our_organisations.feature.OurOrgNecessaryFragmentResult
import ru.tensor.sbis.our_organisations.feature.OurOrgSelectionWindowFactory
import ru.tensor.sbis.our_organisations.feature.OurOrgUnnecessaryFragmentResult
import ru.tensor.sbis.our_organisations.presentation.contract.OurOrgWithNecessaryChoiceFragmentContract
import ru.tensor.sbis.our_organisations.presentation.contract.OurOrgWithNecessaryChoiceSelectionWindowContract
import ru.tensor.sbis.our_organisations.presentation.contract.OurOrgWithUnnecessaryChoiceFragmentContract
import ru.tensor.sbis.our_organisations.presentation.contract.OurOrgWithUnnecessaryChoiceSelectionWindowContract
import ru.tensor.sbis.our_organisations.presentation.contract.register

/**
 * Реализация [OurOrgListModule].
 *
 * @author mv.ilin
 */
internal class OurOrgListModuleImpl : OurOrgListModule {
    override fun ourOrgWithNecessaryChoiceFragmentFactory(
        fragment: Fragment,
        onResult: (OurOrgNecessaryFragmentResult) -> Unit
    ): OurOrgFragmentFactory {
        return OurOrgWithNecessaryChoiceFragmentContract(OurOrgItemType.SIMPLE).register(fragment, onResult)
    }

    override fun ourOrgWithNecessaryChoiceSelectionWindowFactory(
        fragment: Fragment,
        onResult: (OurOrgNecessaryFragmentResult) -> Unit
    ): OurOrgSelectionWindowFactory {
        return OurOrgWithNecessaryChoiceSelectionWindowContract(OurOrgItemType.COMPLEX).register(
            fragment,
            onResult
        )
    }

    override fun ourOrgWithUnnecessaryChoiceFragmentFactory(
        fragment: Fragment,
        onResult: (OurOrgUnnecessaryFragmentResult) -> Unit
    ): OurOrgFragmentFactory {
        return OurOrgWithUnnecessaryChoiceFragmentContract(OurOrgItemType.SIMPLE).register(fragment, onResult)
    }

    override fun ourOrgWithUnnecessaryChoiceSelectionWindowFactory(
        fragment: Fragment,
        onResult: (OurOrgUnnecessaryFragmentResult) -> Unit
    ): OurOrgSelectionWindowFactory {
        return OurOrgWithUnnecessaryChoiceSelectionWindowContract(OurOrgItemType.COMPLEX).register(
            fragment,
            onResult
        )
    }
}

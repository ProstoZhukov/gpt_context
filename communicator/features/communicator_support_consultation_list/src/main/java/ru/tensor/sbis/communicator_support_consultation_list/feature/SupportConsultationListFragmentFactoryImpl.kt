package ru.tensor.sbis.communicator_support_consultation_list.feature

import ru.tensor.sbis.communicator_support_consultation_list.presentation.SupportConsultationListFragment
import ru.tensor.sbis.consultations.generated.SupportRegistryDataConsultations

/**
 * Реализация SupportRequestsListFragmentFactory
 * @see SupportConsultationListFragmentFactory
 */
internal class SupportConsultationListFragmentFactoryImpl :
    SupportConsultationListFragmentFactory {

    override fun getSupportRequestsListFeatureContract(
        supportRegistryDataConsultations: SupportRegistryDataConsultations,
        needBackBtn: Boolean,
        sabyGetConfig: SabyGetConfig?,
        isSingleChannel: Boolean
    ): SupportConsultationListFeatureContract {
        return SupportConsultationListFragment.newInstance(
            supportRegistryDataConsultations,
            needBackBtn,
            sabyGetConfig,
            isSingleChannel
        )
    }
}
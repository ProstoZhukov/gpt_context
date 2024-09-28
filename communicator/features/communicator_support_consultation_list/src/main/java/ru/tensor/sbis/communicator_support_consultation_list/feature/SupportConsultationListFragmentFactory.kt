package ru.tensor.sbis.communicator_support_consultation_list.feature

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.consultations.generated.SupportRegistryDataConsultations
import ru.tensor.sbis.plugin_struct.feature.Feature
import java.util.UUID

/**
 * Фабрика хост фрагмента реестра обращений в поддержку
 *
 * @author ra.petrov
 */
interface SupportConsultationListFragmentFactory : Feature {

    /**
     * Получить фрагмент
     * @param supportRegistryDataConsultations идентификатор канала
     */
    fun getSupportRequestsListFeatureContract(
        supportRegistryDataConsultations: SupportRegistryDataConsultations,
        needBackBtn: Boolean = true,
        sabyGetConfig: SabyGetConfig? = null,
        isSingleChannel: Boolean = false
    ): SupportConsultationListFeatureContract
}

@Parcelize
data class SabyGetConfig(
    val isBrand: Boolean = false,
    val salePoint: UUID? = null,
    val hasAccordion: Boolean = false
) : Parcelable
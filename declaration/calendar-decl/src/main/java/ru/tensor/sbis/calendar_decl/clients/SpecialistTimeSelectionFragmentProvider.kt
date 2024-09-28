package ru.tensor.sbis.calendar_decl.clients

import ru.tensor.sbis.design_dialogs.dialogs.content.ContentCreatorParcelable
import ru.tensor.sbis.plugin_struct.feature.Feature
import java.util.Date

/** Провайдер экрана слотов времени для записи к специалисту */
interface SpecialistTimeSelectionFragmentProvider : Feature {

    /**
     * Получить экран слотов времени для записи к специалисту для показа в шторке
     * @param clientId Аккаунт облака
     * @param salePointId Идентификатор точки продаж
     * @param queueId id очереди
     * @param initialDate дата для первоначального подскролла
     * @param period временные рамки события записи
     */
    fun getSpecialistTimeSelectionFragmentContentCreator(
        clientId: Int,
        salePointId: Int,
        queueId: Int?,
        initialDate: Date? = null,
        period: SpecialistTimeSelectionPeriod,
    ): ContentCreatorParcelable
}

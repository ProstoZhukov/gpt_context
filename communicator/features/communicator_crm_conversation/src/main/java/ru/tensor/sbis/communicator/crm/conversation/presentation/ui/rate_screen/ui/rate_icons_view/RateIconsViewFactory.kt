package ru.tensor.sbis.communicator.crm.conversation.presentation.ui.rate_screen.ui.rate_icons_view

import ru.tensor.sbis.design.message_view.content.crm_views.rate_view.ConsultationRateType

/**
 * Интерфейс фабрики создания RateIconsView в зависимости от типа оценки (звезды, смайлы или пальцы).
 *
 * @author dv.baranov
 */
internal interface RateIconsViewFactory {

    /** @SelfDocumented */
    fun createIconsView(
        consultationRateType: ConsultationRateType,
    ): RateIconsView
}

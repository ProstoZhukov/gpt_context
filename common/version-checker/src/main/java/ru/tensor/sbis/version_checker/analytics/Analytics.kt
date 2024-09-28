package ru.tensor.sbis.version_checker.analytics

import ru.tensor.sbis.statistic.StatisticService
import ru.tensor.sbis.statistic.model.StatisticEvent
import javax.inject.Inject

/**
 * Сборщик статистики по использованию функционала версионирования.
 */
internal class Analytics @Inject constructor() {

    /**
     * Публикует событие по использованию функционала версионирования.
     */
    fun send(event: AnalyticsEvent, updateMarket: String? = null, isGooglePlayAvailable: Boolean? = null) {
        event.updateMarket = updateMarket
        event.isGooglePlayAvailable = isGooglePlayAvailable
        StatisticService.report(StatisticEvent(FUNCTIONAL, CONTEXT, event.getActionNaming()))
    }

    private companion object {
        const val FUNCTIONAL = "versioning"
        const val CONTEXT = "versioning_screen"
    }
}
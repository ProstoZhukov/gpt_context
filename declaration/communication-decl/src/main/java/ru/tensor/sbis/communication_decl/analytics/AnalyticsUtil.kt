package ru.tensor.sbis.communication_decl.analytics

import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Интерфейс утилиты для отправки аналитики.
 *
 * @author dv.baranov
 */
interface AnalyticsUtil {

    /**
     * Отправить событие аналитики.
     */
    fun sendAnalytics(analyticsEvent: AnalyticsEvent)

    /**
     * Поставщик [AnalyticsUtil].
     */
    fun interface Provider : Feature {

        fun getAnalyticsUtil(): AnalyticsUtil
    }
}

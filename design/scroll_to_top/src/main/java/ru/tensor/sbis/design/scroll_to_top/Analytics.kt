package ru.tensor.sbis.design.scroll_to_top

import android.content.Context
import ru.tensor.sbis.statistic.StatisticService
import ru.tensor.sbis.statistic.model.StatisticEvent

/**
 * Объект для сбора ститистики по использованию функционала ССВ
 *
 * @author ma.kolpakov
 */
@Deprecated("Отказываемся от ScrollToTop")
internal class Analytics(appContext: Context) {

    /**
     * Публикует событие для отслеживания частоты использвоания ССВ. Аналитика изначально добавлена
     * для проверки гипотезы о бесполезности функционала
     */
    fun reportScrollToTopClicked() {
        // событие общее с iOS платформой для построения сравнительных графиков
        val event = StatisticEvent("ScrollToTop", "", "scroll_to_top_tapped")
        StatisticService.report(event)
    }
}
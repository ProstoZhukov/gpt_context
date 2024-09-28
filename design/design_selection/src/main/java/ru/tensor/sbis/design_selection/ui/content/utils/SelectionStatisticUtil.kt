package ru.tensor.sbis.design_selection.ui.content.utils

import ru.tensor.sbis.statistic.StatisticService
import ru.tensor.sbis.statistic.model.StatisticEvent
import ru.tensor.sbis.toolbox_decl.selection_statistic.SelectionStatisticEvent

/**
 * Утилита для сборка статистики по кейсам выбора адресатов.
 *
 * @author dv.baranov
 */
object SelectionStatisticUtil {

    /**
     * Отправить событие статистики.
     */
    fun sendStatistic(event: SelectionStatisticEvent) {
        with(event) {
            StatisticService.report(StatisticEvent(functional, context, action))
        }
    }
}
package ru.tensor.sbis.communicator.common.analytics

import ru.tensor.sbis.communication_decl.analytics.AnalyticsEvent
import ru.tensor.sbis.communication_decl.analytics.AnalyticsUtil
import ru.tensor.sbis.communicator.common.util.doIf
import ru.tensor.sbis.statistic.StatisticService
import ru.tensor.sbis.statistic.model.StatisticEvent

/**
 * Реализация утилиты отправки анатлитики для диалогов, каналов и контактов.
 *
 * @author dv.baranov
 */
class CommunicatorAnalyticsUtil : AnalyticsUtil {

    override fun sendAnalytics(analyticsEvent: AnalyticsEvent) {
        StatisticService.report(analyticsEvent.toStatisticEvent())
    }

    private fun AnalyticsEvent.toStatisticEvent(): StatisticEvent = StatisticEvent(
        functional = functional,
        context = analyticContext,
        action = getAction(),
    )

    private fun AnalyticsEvent.getAction(): String = StringBuilder()
        .append(event)
        .doIf(!bundle.isEmpty) { appendLine(bundle.toString()) }
        .toString()
}

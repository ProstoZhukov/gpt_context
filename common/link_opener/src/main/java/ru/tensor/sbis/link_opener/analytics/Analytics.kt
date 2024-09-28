package ru.tensor.sbis.link_opener.analytics

import ru.tensor.sbis.toolbox_decl.linkopener.LinkPreview
import ru.tensor.sbis.link_opener.analytics.AnalyticsEvent.OpenCardType
import ru.tensor.sbis.link_opener.analytics.AnalyticsEvent.OpenBrowserType
import ru.tensor.sbis.link_opener.analytics.AnalyticsEvent.OpenSabylink
import ru.tensor.sbis.link_opener.analytics.AnalyticsEvent.OpenCustomTabsType
import ru.tensor.sbis.link_opener.analytics.AnalyticsEvent.OpenWebViewType
import ru.tensor.sbis.statistic.StatisticService
import ru.tensor.sbis.statistic.model.StatisticEvent
import javax.inject.Inject

/**
 * Сборщик статистики по использованию функционала открытия ссылок.
 *
 * @author al.kropotov
 */
internal class Analytics @Inject constructor() {

    /**
     * Публикует событие [T] по использованию функционала для открытия ссылки [preview].
     */
    inline fun <reified T : AnalyticsEvent> sendEvent(preview: LinkPreview) {
        val event = when (T::class) {
            OpenCardType::class       -> OpenCardType()
            OpenSabylink::class       -> OpenSabylink()
            OpenCustomTabsType::class -> OpenCustomTabsType()
            OpenWebViewType::class    -> OpenWebViewType()
            OpenBrowserType::class    -> OpenBrowserType()
            else ->
                throw IllegalArgumentException("Unknown analytics event type ${T::class.java.simpleName}")
        }
        event.apply {
            docType = "${preview.docType}"
            docSubtype = "${preview.docSubtype}"
        }
        StatisticService.report(StatisticEvent(FUNCTIONAL, event.context, event.getAction()))
    }

    companion object {
        const val FUNCTIONAL = "link_opener"
    }
}

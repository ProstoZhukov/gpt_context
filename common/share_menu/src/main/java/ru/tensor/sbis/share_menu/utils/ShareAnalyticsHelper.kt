package ru.tensor.sbis.share_menu.utils

import ru.tensor.sbis.share_menu.ui.ShareMenuFragment
import ru.tensor.sbis.statistic.StatisticService
import ru.tensor.sbis.statistic.model.StatisticEvent

/**
 * Вспомогательная реализация для аналитики меню шаринга.
 *
 * @author vv.chekurda
 */
internal class ShareAnalyticsHelper {

    /**
     * Отправить в аналитику событие шаринга для названия [name] с типом события [type].
     */
    fun logEvent(name: String, isQuickShare: Boolean) {
        val eventName = StringBuilder()
            .apply { if (isQuickShare) append(DIRECT_SHARE_KEY).append(SNAKE_SPACE) }
            .append(SHARE_KEY).append(SNAKE_SPACE)
            .append(name)
            .toString()
            .take(EVENT_NAME_MAX_SYMBOLS)
        StatisticService.report(
            StatisticEvent(
                functional = ShareMenuFragment::class.java.toString(),
                context = SHARE_SCREEN,
                action = eventName,
            ),
        )
    }
}

private const val DIRECT_SHARE_KEY = "direct"
private const val SHARE_KEY = "share_send"
private const val SNAKE_SPACE = "_"
private const val SEND_EVENT_TYPE = "send"
private const val SHARE_SCREEN = "share_screen"
private const val EVENT_NAME_MAX_SYMBOLS = 40
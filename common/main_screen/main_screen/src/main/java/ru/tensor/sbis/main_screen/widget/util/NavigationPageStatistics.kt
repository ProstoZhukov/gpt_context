package ru.tensor.sbis.main_screen.widget.util

import android.os.Handler
import android.os.Looper
import ru.tensor.sbis.design.navigation.view.model.NavigationItem
import ru.tensor.sbis.statistic.PageStatisticService
import ru.tensor.sbis.statistic.model.StatisticPageEvent
import ru.tensor.sbis.statistic.model.StatisticTraceTyped

/**
 * Инструмент для отправки статистики по открытым страницам.
 *
 * @author us.bessonov
 */
class NavigationPageStatistics {

    private var lastEvent: StatisticPageEvent? = null
    private var pendingEvent: StatisticPageEvent? = null
    private val handler = Handler(Looper.getMainLooper())
    private var postponedEndTraceRunnable: Runnable? = null
    private var trace: StatisticTraceTyped<StatisticPageEvent>? = null

    /**
     * Начать замер открытия страницы.
     * Событие будет отправлено только после вызова [endTrace].
     */
    fun startTrace(itemId: String, skipOldTrace: Boolean = false) {
        if (itemId == lastEvent?.page) return
        postponedEndTraceRunnable?.let {
            it.run()
            handler.removeCallbacks(it)
            postponedEndTraceRunnable = null
        }
        val event = StatisticPageEvent(LOG_EVENT_PAGE, itemId)
            .also { pendingEvent = it }
        if (!skipOldTrace) trace?.stop()
        trace = PageStatisticService.startTrace(event)
    }

    /**
     * Начать замер открытия страницы, либо отменить отложенное событие, и уточнить его, обновив идентификатор и
     * выполнив [endTrace].
     */
    fun startOrOverrideTrace(itemId: String) {
        postponedEndTraceRunnable?.let {
            handler.removeCallbacks(it)
            postponedEndTraceRunnable = null
            pendingEvent?.page = itemId
        } ?: startTrace(itemId)
    }

    /**
     * Зафиксировать событие открытия страницы для статистики.
     * Длительность события отсчитывается с момента последнего вызова [startTrace].
     */
    fun endTrace(navxId: String? = null) {
        if (navxId != null && navxId != pendingEvent?.page) return
        trace?.stop()
        trace = null
        lastEvent = pendingEvent
        pendingEvent = null
    }

    /**
     * Зафиксировать событие открытия страницы для статистики спустя некоторое время.
     * На длительность события задержка не влияет.
     * Либо событие будет отправлено по таймауту, либо его переопределит более позднее (выбор вкладки после открытия экрана).
     */
    fun endTracePostponed() {
        handler.postDelayed(Runnable {
            endTrace()
            postponedEndTraceRunnable = null
        }.also { postponedEndTraceRunnable = it }, POSTPONED_TRACE_TIMEOUT)
    }
}

fun NavigationItem.requireId() = navxId?.ids?.first() ?: persistentUniqueIdentifier

private const val LOG_EVENT_PAGE = "Открыта страница"

private const val POSTPONED_TRACE_TIMEOUT = 500L
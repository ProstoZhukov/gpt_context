package ru.tensor.sbis.statistic.model

import java.util.concurrent.atomic.AtomicBoolean

typealias StatisticTrace = StatisticTraceTyped<StatisticEvent>

/**
 * Модель трассировки события статистики.
 *
 * @property event событие.
 * @property onStop действие, которое будет выполнено при завершении.
 *
 * @author kv.martyshenko
 */
class StatisticTraceTyped<EVENT> internal constructor(
    private val event: EVENT,
    private val onStop: (EVENT) -> Unit
) {
    private var isCompleted = AtomicBoolean(false)

    /**
     * Метод для завершения трассировки.
     */
    fun stop() {
        if (isCompleted.getAndSet(true)) return

        onStop(event)
    }

}
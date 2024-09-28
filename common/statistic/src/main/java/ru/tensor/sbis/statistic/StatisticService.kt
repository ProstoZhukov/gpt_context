package ru.tensor.sbis.statistic

import ru.tensor.sbis.statistic.model.StatisticEvent

/**
 * Сервис отправки статистики.
 *
 * @author kv.martyshenko
 */
object StatisticService : StatisticServiceTyped<StatisticEvent>()

/**
 * Выполнить трассировку события совместно с выполнением действия.
 *
 * @param event событие.
 * @param block требуемое действие.
 *
 * @return результат выполнения [block].
 *
 * ```
 * fun test(uuid: UUID) {
 *     val card = StatisticService.trace(StatisticEvent("f", "c", "a")) {
 *         Controller.instance().getCard(uuid)
 *     }
 * }
 * ```
 *
 * @author kv.martyshenko
 *
 */

fun <T> StatisticService.trace(event: StatisticEvent, block: () -> T): T {
    return with(startTrace(event)) {
        val result = block()
        stop()
        result
    }
}
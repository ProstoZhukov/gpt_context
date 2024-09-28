package ru.tensor.sbis.statistic

import ru.tensor.sbis.statistic.model.StatisticPageEvent

/**
 * Сервис отправки статистики по страницам МП.
 *
 * @author us.bessonov
 */
object PageStatisticService : StatisticServiceTyped<StatisticPageEvent>()
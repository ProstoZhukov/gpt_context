package ru.tensor.sbis.statistic.model

/**
 * Модель события открытия страницы МП.
 *
 * @author us.bessonov
 */
data class StatisticPageEvent(
    val logEvent: String,
    var page: String
)
package ru.tensor.sbis.statistic.model

/**
 * Модель события статистики.
 *
 * Название приложения в структуру EventInfo "зашивать" не требуется, как и платформу.
 *
 * @property functional функционал (Имя модуля, из которого вызывается событие).
 * @property context контекст (Название экрана или flow, в котором находится пользователь).
 * @property action действие (Собственно, само событие, о факте которого вы хотите оставить "след").
 *
 * @author kv.martyshenko
 */
data class StatisticEvent(
    val functional: String,
    val context: String,
    val action: String
)
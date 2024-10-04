package ru.tensor.sbis.calendar.date.view.year

/**
 * Набор для обновления
 * [notifyRecycler] - уведомить recyclerview
 * [list] - список ячеек, который надо обновить
 */
internal data class NotifyList(
    val notifyRecycler: Boolean,
    val list: List<Pair<Int, Boolean>>?
)

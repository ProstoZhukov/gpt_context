package ru.tensor.sbis.design.period_picker.view.models

/**
 * Класс - ключ для основных элементов календарной сетки.
 * Необходим для реализации доступа к элементов календарной сетки.
 *
 * @author mb.kruglova
 */
internal data class CalendarStorageKey constructor(
    val year: Int,
    val month: Int = NOT_SPECIFIED
) {

    companion object {
        const val NOT_SPECIFIED = -1
        fun createMonthKey(year: Int, month: Int) = CalendarStorageKey(year, month)
    }
}

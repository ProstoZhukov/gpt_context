package ru.tensor.sbis.date_picker

/**
 * Класс - ключ для основных элементов календарной сетки (год и месяц).
 * @see ru.tensor.sbis.date_picker.items.CalendarVmStorage.grid
 * Необходим для реализации доступа к элементов календарной сетки вне зависимости от режима ("Год" или "Месяц")
 * Для элементов режима "Год" ключ содержит только год, а для элементов режима "Месяц" - год и месяц
 *
 * @author mb.kruglova
 */
data class PeriodsVMKey constructor(
    val mode: Mode,
    val year: Int,
    val month: Int = NOT_SPECIFIED
) {

    companion object {
        fun createYearKey(year: Int) = PeriodsVMKey(Mode.YEAR, year)

        fun createMonthKey(year: Int, month: Int) = PeriodsVMKey(Mode.MONTH, year, month)
    }
}
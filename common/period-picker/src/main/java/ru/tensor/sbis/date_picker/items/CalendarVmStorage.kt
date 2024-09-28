package ru.tensor.sbis.date_picker.items

import ru.tensor.sbis.date_picker.*
import ru.tensor.sbis.date_picker.month.items.DayVM
import ru.tensor.sbis.date_picker.month.items.MonthLabelVM
import ru.tensor.sbis.date_picker.range.CalendarDayRange
import ru.tensor.sbis.date_picker.year.items.HalfYearVM
import ru.tensor.sbis.date_picker.year.items.MonthVM
import ru.tensor.sbis.date_picker.year.items.QuarterVM
import ru.tensor.sbis.date_picker.year.items.YearPeriodsVM
import java.util.*
import kotlin.collections.HashMap

data class Day(val year: Int, val month: Int, val day: Int)

data class Month(val year: Int, val month: Int)

data class HalfYear(val year: Int, val num: Int)

data class Quarter(val year: Int, val num: Int)

/**
 * Структура - агрегатор элементов календарной сетки (ViewModel-ей).
 * Упрощает доступ к конкретным элементам
 *
 * @author mb.kruglova
 */
class CalendarVmStorage {

    // Для доступа к дням по ключу
    var days = HashMap<Day, DayVM>()
        private set

    // Для доступа к месяцам по ключу
    var months = HashMap<Month, MonthVM>()
        private set

    // Для доступа к меткам месяцев по ключу
    var monthLabels = HashMap<PeriodsVMKey, MonthLabelVM>()
        private set

    // Для доступа к кварталам по ключу
    var quarters = HashMap<Quarter, QuarterVM?>()
        private set

    // Для доступа к полугодиям по ключу
    var halfYears = HashMap<HalfYear, HalfYearVM?>()
        private set

    // Для доступа к перодам лет
    var yearPeriods = HashMap<PeriodsVMKey, YearPeriodsVM>()
        private set

    // Текущая сетка
    var grid = LinkedList<Any>()
        private set

    // Для выравнивания дней в месяцей
    var monthDaysAligned = LinkedHashMap<PeriodsVMKey, Int>()
        private set

    /**
     * Инициализация хранилища с помощью другого хранилища
     * @param otherStorage хранилище для переписывания информации
     */
    fun init(otherStorage: CalendarVmStorage) {
        // Функция слияния вьюмоделей для всех элементов коллекции ключ-значение чтобы не копировать цикл.
        fun Map<*, CalendarGridItemVM>.merge(other: Map<*, CalendarGridItemVM>) {
            for (source in other) {
                val dest = get(source.key) ?: continue
                dest.merge(source.value)
            }
        }

        val oldDays = days
        days = otherStorage.days
        days.merge(oldDays)

        val oldMonths = months
        months = otherStorage.months
        months.merge(oldMonths)

        monthLabels = otherStorage.monthLabels
        quarters = otherStorage.quarters
        yearPeriods = otherStorage.yearPeriods
        halfYears = otherStorage.halfYears
        grid = otherStorage.grid
        monthDaysAligned = otherStorage.monthDaysAligned
    }

    fun concat(otherStorage: CalendarVmStorage, addToEnd: Boolean): CalendarVmStorage {
        days.putAll(otherStorage.days)
        months.putAll(otherStorage.months)
        monthLabels.putAll(otherStorage.monthLabels)
        quarters.putAll(otherStorage.quarters)
        yearPeriods.putAll(otherStorage.yearPeriods)
        halfYears.putAll(otherStorage.halfYears)

        if (addToEnd) {
            grid.addAll(otherStorage.grid)
            monthDaysAligned.putAll(otherStorage.monthDaysAligned)
        } else {
            //TODO strange logic
            grid.addAll(0, otherStorage.grid)
            otherStorage.monthDaysAligned.putAll(monthDaysAligned)
            monthDaysAligned = otherStorage.monthDaysAligned
        }
        return otherStorage
    }

    /**
     * Снимает выделение всех кварталов
     */
    fun deselectQuarters() {
        quarters.forEach {
            it.value?.setNoSelected()
        }
    }

    /**
     * Выделяет определенный квартал, предварительно сбросив выделение предыдущего
     */
    fun selectQuarter(quarter: Quarter) {
        deselectQuarters()
        quarters[quarter]?.setSelected()
    }

    /**
     * Снимает выделение всех полугодий
     */
    fun deselectHalfYears() {
        halfYears.forEach {
            it.value?.setNoSelected()
        }
    }

    /**
     * Выделяет определенное полугодие, предварительно сбросив выделение предыдущего
     */
    fun selectHalfYear(halfYear: HalfYear) {
        deselectHalfYears()
        halfYears[halfYear]?.setSelected()
    }

    /**
     * Отметка диапазона месяцев в календарной сетке режима "Год" согласно выбранного пользователем периода
     */
    fun selectMonths(period: Period) {
        period.monthRange?.let { monthRange ->
            val firstMonth = monthRange.first()
            val lastMonth = monthRange.last()
            monthRange.forEach { calendar ->
                val month = calendar.toMonth()
                val monthVm = months[month]

                when {
                    period == Period.fromMonth(month.year, month.month) -> monthVm?.setStartEndSelectionMarker()
                    calendar == firstMonth -> monthVm?.setStartSelectionMarker()
                    calendar == lastMonth -> monthVm?.setEndSelectionMarker()
                    else -> monthVm?.setSelectionMarker()
                }
            }
        }
    }

    /**
     * Сброс диапазона месяцев в календарной сетке режима "Год"
     */
    fun deselectMonths(period: Period) {
        period.monthRange?.let { dayRange ->
            dayRange.forEach { calendar ->
                val month = calendar.toMonth()
                val monthVm = months[month]
                monthVm?.resetSelectionMarker()
            }
        }
    }

    /**
     * Отметка диапазона дней в календарной сетке режима "Месяц" согласно выбранного пользователем периода
     */
    fun selectDays(period: Period, dateSelection: Boolean) {
        period.dayRange?.let { dayRange ->
            val firstDay = dayRange.first()
            val lastDay = dayRange.last()
            dayRange.forEach { calendar ->
                val day = calendar.toDay()
                val dayVm = days[day]
                when {
                    dateSelection -> dayVm?.setStartEndSelectionMarker()
                    period == Period.fromDay(day.year, day.month, day.day) -> dayVm?.setStartEndSelectionMarker()
                    calendar == firstDay -> dayVm?.setStartSelectionMarker()
                    calendar == lastDay -> dayVm?.setEndSelectionMarker()
                    else -> dayVm?.setSelectionMarker()
                }
            }
        }
    }

    /**
     * Сброс диапазона дней в календарной сетке режима "Месяц"
     */
    fun deselectDays(period: Period) {
        period.dayRange?.let { dayRange ->
            dayRange.forEach { calendar ->
                val day = calendar.toDay()
                val dayVm = days[day]
                dayVm?.resetSelectionMarker()
            }
        }
    }

    /**
     * Возвращает интервал дней по начальной и конечной позиции в сетке календаря.
     * Важно: работает только для месячной календарной сетки.
     *
     * @param startPosition позиция первой видимой ячейки в календаре
     * @param endPosition позиция последней видимой ячейки в календаре
     * @param totalCount общее количество ячеек в календаре - должно совпадать с текущей календарной сеткой
     */
    fun dayRangeForMonthGrid(startPosition: Int, endPosition: Int, totalCount: Int): CalendarDayRange? {
        val monthGrid = grid
        if (monthGrid.size == totalCount && startPosition <= endPosition &&
            startPosition in 0 until monthGrid.size && endPosition in 0 until monthGrid.size
        ) {
            val startDay = getNearestDay(monthGrid, startPosition) ?: return null
            val endDay = getNearestDay(monthGrid, endPosition) ?: return null
            return CalendarDayRange(startDay.source.toCalendar(), endDay.source.toCalendar())
        }
        return null
    }

    /**
     * Ищет в календарной сетке ближайший к заданной позиции день
     * так как помимо дней там могут быть оглавления месяцев, пустые дни и т.д.
     */
    private fun getNearestDay(monthGrid: List<Any>, index: Int): DayVM? {
        return monthGrid[index] as? DayVM
            ?: monthGrid.subList(0, index).findLast { it is DayVM } as? DayVM
            ?: monthGrid.subList(index, monthGrid.size).find { it is DayVM } as? DayVM
    }
}
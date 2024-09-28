package ru.tensor.sbis.date_picker.items

import androidx.databinding.ObservableBoolean
import ru.tensor.sbis.common.util.ResourceProvider
import ru.tensor.sbis.date_picker.*
import ru.tensor.sbis.date_picker.month.items.DayVM
import ru.tensor.sbis.date_picker.month.items.EmptyVM
import ru.tensor.sbis.date_picker.month.items.MonthLabelVM
import ru.tensor.sbis.date_picker.selection.SelectionStrategy
import ru.tensor.sbis.date_picker.year.items.HalfYearVM
import ru.tensor.sbis.date_picker.year.items.MonthVM
import ru.tensor.sbis.date_picker.year.items.QuarterVM
import ru.tensor.sbis.date_picker.year.items.YearPeriodsVM
import java.util.Calendar
import java.util.GregorianCalendar
import ru.tensor.sbis.common.R as RCommon
import ru.tensor.sbis.design.R as RDesign

/**
 * @author mb.kruglova
 */
class CalendarVmFactory(private val resourceProvider: ResourceProvider) {

    //region Years
    /**
     * Получение списка элементов режима "Год"
     * @param min минимальное значение (дата) текущей календарной сетки
     * @param max максимальное значение (дата) текущей календарной сетки
     * @param needQuartersAndHalfYears флаг необходимости показывать кварталы и полугодия
     * @param needFixedIndicators флаг необходимости показывать индикатор зафиксированных/незафиксированных месяцев
     * @param selectionStrategy стратегия выбора периода
     * @return хранилище ViewModel-ей для RecyclerView
     */
    fun generateYearPeriods(
        min: Calendar,
        max: Calendar,
        needQuartersAndHalfYears: Boolean,
        needFixedIndicators: Boolean,
        yearLabelsClickable: Boolean,
        selectionStrategy: SelectionStrategy
    ): CalendarVmStorage {
        val storage = CalendarVmStorage()
        for (year in min.year..max.year) {
            val months = prepareMonths(year, needFixedIndicators)
            storage.months.putAll(months)

            var halfYears: Map<HalfYear, HalfYearVM>? = null
            var quarters: Map<Quarter, QuarterVM>? = null
            if (needQuartersAndHalfYears) {
                halfYears = prepareHalfYears(year)
                quarters = prepareQuarters(year)
                storage.halfYears.putAll(halfYears)
                storage.quarters.putAll(quarters)
            }

            val key = PeriodsVMKey.createYearKey(year)
            val yearPeriodsVM = YearPeriodsVM(year.toString(),
                months.values.toList(),
                ObservableBoolean(yearLabelsClickable),
                halfYears?.values?.toList(),
                quarters?.values?.toList(),
                { namedItem -> selectionStrategy.yearClicked?.invoke(key, namedItem) },
                { halfYear, namedItem -> selectionStrategy.halfYearClicked?.invoke(key, halfYear, namedItem) },
                { quarter, namedItem -> selectionStrategy.quarterClicked?.invoke(key, quarter, namedItem) },
                { month -> selectionStrategy.monthClicked?.invoke(key, month) })
            storage.yearPeriods[key] = yearPeriodsVM
            storage.grid.add(yearPeriodsVM)
        }
        return storage
    }

    /**
     * Подготовка списка месяцев для заданного года
     * @param year заданный год
     * @param needFixedIndicators флаг необходимости показывать индикатор зафиксированных/незафиксированных месяцев
     * @return Map ViewModel-ей для RecyclerView
     */
    private fun prepareMonths(year: Int, needFixedIndicators: Boolean): Map<Month, MonthVM> {
        val monthTitles = resourceProvider.getStringArray(RDesign.array.design_months_short)
        return monthTitles.mapIndexed { month, title ->
            Month(year, month) to MonthVM(title, isCurrentMonth(year, month), needFixedIndicators)
        }.toMap()
    }

    /**
     * Подготовка списка полугодий
     * @param year заданный год
     * @return Map ViewModel-ей для RecyclerView
     */
    private fun prepareHalfYears(year: Int): Map<HalfYear, HalfYearVM> {
        val halfYearTitles = resourceProvider.getStringArray(R.array.half_year_short)
        return halfYearTitles.mapIndexed { index, halfYearTitle -> HalfYear(year, index) to HalfYearVM(halfYearTitle) }
            .toMap()
    }

    /**
     * Подготовка списка кварталов
     * @param year заданный год
     * @return Map ViewModel-ей для RecyclerView
     */
    private fun prepareQuarters(year: Int): Map<Quarter, QuarterVM> {
        val quarterTitles = resourceProvider.getStringArray(R.array.quarters_short)
        return quarterTitles.mapIndexed { index, quarterTitle -> Quarter(year, index) to QuarterVM(quarterTitle) }
            .toMap()
    }

    /**
     * Проверка, является ли заданный год и месяц текущим
     * @param year год
     * @param month месяц (0-based)
     * @return true, если заданный год и месяц текущий
     */
    private fun isCurrentMonth(year: Int, month: Int): Boolean {
        val calendar = getCurrentDay()
        return year == calendar.year && month == calendar.month
    }
    //endregion

    //region Months
    /**
     * Получение списка элементов режима "Месяц"
     * @param min минимальное значение (дата) текущей календарной сетки
     * @param max максимальное значение (дата) текущей календарной сетки
     * @param selectionStrategy стратегия выбора периода
     * @return хранилище ViewModel-ей для RecyclerView
     */
    fun generateMonthPeriods(
        min: Calendar,
        max: Calendar,
        selectionStrategy: SelectionStrategy
    ): CalendarVmStorage {
        val storage = CalendarVmStorage()
        val monthTitles = resourceProvider.getStringArray(RCommon.array.common_months)
        for (year in min.year..max.year) {
            val monthFrom = getStartMonthOfYear(year, min)
            val monthTo = getEndMonthOfYear(year, max)
            for (month in monthFrom..monthTo) {
                val dayFrom = getStartDayOfMonth(year, month, min)
                val dayTo = getEndDayOfMonth(year, month, max)
                val title = String.format("%s %d", monthTitles[month], year)
                val key = PeriodsVMKey.createMonthKey(year, month)
                val monthLabelClickedAction: (NamedItemVM) -> Unit =
                    { namedItem -> selectionStrategy.monthLabelClicked?.invoke(key, namedItem, min, max) }
                val monthLabel = MonthLabelVM(title, monthLabelClickedAction)
                val dayClickedAction: (Int) -> Unit = { day -> selectionStrategy.dayClicked?.invoke(key, day) }
                val monthDays = prepareDays(year, month, dayFrom, dayTo, dayClickedAction)
                val monthDaysAligned = (0 until getStartDayOfWeek(
                    year,
                    month,
                    dayFrom
                )).map { EmptyVM } + monthDays.values.toList()

                storage.monthLabels[key] = monthLabel
                storage.monthDaysAligned[key] = monthDaysAligned.size
                storage.grid.add(monthLabel)

                storage.days.putAll(monthDays)
                storage.grid.addAll(monthDaysAligned)
            }
        }
        return storage
    }

    /**
     * Подготовка списка дней определенного года и месяца
     * @param year год
     * @param month месяц (0-based)
     * @return Map ViewModel-ей для RecyclerView
     */
    private fun prepareDays(
        year: Int,
        month: Int,
        dayFrom: Int,
        dayTo: Int,
        dayClickedAction: (Int) -> Unit
    ): Map<Day, DayVM> {
        val today = getCurrentDay().toDay()
        return (dayFrom..dayTo).map { day ->
            val key = Day(year, month, day)
            val calendar = key.toCalendar()
            val isCurrent = key == today
            val dayVm = DayVM(calendar.dayOfMonth, calendar.dayOfWeek, key, isCurrent).apply {
                clickAction = { dayClickedAction(day) }
            }
            key to dayVm
        }.toMap()
    }

    /**
     * Получение номера дня недели для заданной даты
     * @param year год
     * @param month месяц (0-based)
     * @return номер дня недели (0-6)
     */
    private fun getStartDayOfWeek(year: Int, month: Int, day: Int) = GregorianCalendar(year, month, day).dayOfWeek
    //endregion
}
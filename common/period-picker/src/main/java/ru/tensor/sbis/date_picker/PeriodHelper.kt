package ru.tensor.sbis.date_picker

import ru.tensor.sbis.date_picker.items.CalendarVmStorage
import ru.tensor.sbis.date_picker.items.HalfYear
import ru.tensor.sbis.date_picker.items.NamedItemVM
import ru.tensor.sbis.date_picker.items.Quarter
import ru.tensor.sbis.date_picker.range.halfYearRange
import ru.tensor.sbis.date_picker.range.quarterRange

/**
 * @author mb.kruglova
 */
class PeriodHelper {
    private val currentDay get() = getCurrentDay()
    private val year = currentDay.year
    private val month = currentDay.month
    private val quarter = currentDay.month / 3
    private val halfYear = currentDay.month / 6
    val dayPeriod = Period(currentDay, currentDay)
    val monthPeriod = Period.fromMonth(year, month)
    val quarterPeriod = Period.fromQuarter(year, quarter)
    val halfYearPeriod = Period.fromHalfYear(year, halfYear)
    val yearPeriod = Period.fromYear(year)

    fun isCurrentDay(period: Period?): Boolean {
        return period?.dateFrom == dayPeriod.dateFrom && period?.dateTo == dayPeriod.dateTo
    }

    fun isCurrentMonth(period: Period?): Boolean {
        return period == monthPeriod
            || (period?.dateFrom == monthPeriod.dateFrom
            && period?.dateTo == monthPeriod.dateTo)
    }

    fun isCurrentQuarter(period: Period?) = period == quarterPeriod

    fun isCurrentHalfYear(period: Period?) = period == halfYearPeriod


    fun isCurrentYear(period: Period?) = period == yearPeriod

    fun checkPeriodIsCurrentAndGetStringRes(period: Period?): Pair<Boolean, Int> {
        return when {
            isCurrentDay(period) -> Pair(true, R.string.date_picker_current_day_label)
            isCurrentMonth(period) -> Pair(true, R.string.date_picker_current_month_label)
            isCurrentQuarter(period) -> Pair(true, R.string.date_picker_current_quarter_label)
            isCurrentHalfYear(period) -> Pair(true, R.string.date_picker_current_half_year_label)
            isCurrentYear(period) -> Pair(true, R.string.date_picker_current_year_label)
            else -> Pair(false, 0)
        }
    }

    fun getLongPeriodVmFromStorage(storage: CalendarVmStorage, period: Period): NamedItemVM? {
        val year = period.yearFrom

        // если выбран год
        if (period == Period.fromYear(year)) {
            val yearVm = storage.yearPeriods[PeriodsVMKey.createYearKey(year)]
            yearVm?.let { return yearVm }
        }

        // если выбрано одно из полугодий
        halfYearRange(year).indexOf(period).let { index ->
            val halfYearVm = storage.halfYears[HalfYear(year, index)]
            halfYearVm?.let { return halfYearVm }
        }

        // если выбрано одно из полугодий
        quarterRange(year).indexOf(period).let { index ->
            val quarterVm = storage.quarters[Quarter(year, index)]
            quarterVm?.let { return quarterVm }
        }
        return null
    }

    fun getMonthPeriodVmFromStorage(storage: CalendarVmStorage, period: Period): NamedItemVM? {
        // если выбран месяц
        if (period == Period.fromMonth(period.yearFrom, period.monthFrom)) {
            val key = PeriodsVMKey.createMonthKey(period.yearFrom, period.monthFrom)
            return storage.monthLabels[key]
        }
        return null
    }

    /**
     * Проверка, что период равен одному из кварталов.
     *
     * Возвращает: является ли период кварталом + порядковый номер квартала.
     */
    fun isFullQuarter(period: Period): Pair<Boolean, Int> {
        var quarterNum = 0
        quarterRange(period.yearFrom).forEach { quarterPeriod ->
            if (quarterPeriod.monthFrom == period.monthFrom && quarterPeriod.monthTo == period.monthTo)
                return true to quarterNum
            quarterNum++
        }
        return false to 0
    }

    /**
     * Проверка, что период равен одному из полугодий.
     *
     * Возвращает: является ли период полугодием + порядковый номер полугодия.
     */
    fun isFullHalfYear(period: Period): Pair<Boolean, Int> {
        var halYearNum = 0
        halfYearRange(period.yearFrom).forEach { halfYearPeriod ->
            if (halfYearPeriod.monthFrom == period.monthFrom && halfYearPeriod.monthTo == period.monthTo)
                return true to halYearNum
            halYearNum++
        }
        return false to 0
    }
}
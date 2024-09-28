package ru.tensor.sbis.date_picker.current

import ru.tensor.sbis.common.util.ResourceProvider
import ru.tensor.sbis.common.util.date.DateFormatUtils
import ru.tensor.sbis.date_picker.*
import ru.tensor.sbis.common.R as RCommon

/**
 * @author mb.kruglova
 */
class CurrentPeriodVmFactory(private val resourceProvider: ResourceProvider, private val periodHelper: PeriodHelper) {

    private val halfYearTitles = resourceProvider.getStringArray(R.array.half_year)
    private val quartersTitles = resourceProvider.getStringArray(R.array.quarters)
    private val monthTitles = resourceProvider.getStringArray(RCommon.array.common_months)

    fun createItems(
        onItemClick: (Period) -> Unit,
        selectedPeriod: Period,
        visibleCurrentPeriods: List<CurrentPeriod>
    ): List<CurrentPeriodVm> {
        val currentDay = getCurrentDay()
        val month = currentDay.month
        val quarter = currentDay.month / 3
        val halfYear = currentDay.month / 6
        val monthTitle = monthTitles[month]
        val quarterTitle = quartersTitles[quarter]
        val halfYearTitle = halfYearTitles[halfYear]

        return visibleCurrentPeriods.sorted().map {
            when (it) {
                CurrentPeriod.DAY -> {
                    CurrentPeriodVm(
                        resourceProvider.getString(R.string.date_picker_day_label),
                        DateFormatUtils.format(currentDay.time, "dd MMM''yy")
                    ) { onItemClick(periodHelper.dayPeriod) }
                        .apply { setSelected(periodHelper.isCurrentDay(selectedPeriod)) }
                }
                CurrentPeriod.MONTH -> {
                    CurrentPeriodVm(
                        resourceProvider.getString(R.string.date_picker_month_label),
                        monthTitle + DateFormatUtils.format(currentDay.time, "''yy")
                    ) { onItemClick(periodHelper.monthPeriod) }
                        .apply { setSelected(periodHelper.isCurrentMonth(selectedPeriod)) }
                }
                CurrentPeriod.QUARTER -> {
                    CurrentPeriodVm(
                        resourceProvider.getString(R.string.date_picker_quarter_label),
                        quarterTitle + DateFormatUtils.format(currentDay.time, "''yy")
                    ) { onItemClick(periodHelper.quarterPeriod) }
                        .apply { setSelected(periodHelper.isCurrentQuarter(selectedPeriod)) }
                }
                CurrentPeriod.HALF_YEAR -> {
                    CurrentPeriodVm(
                        resourceProvider.getString(R.string.date_picker_half_year_label),
                        halfYearTitle + DateFormatUtils.format(currentDay.time, "''yy")
                    ) { onItemClick(periodHelper.halfYearPeriod) }
                        .apply { setSelected(periodHelper.isCurrentHalfYear(selectedPeriod)) }
                }
                CurrentPeriod.YEAR -> {
                    CurrentPeriodVm(
                        resourceProvider.getString(R.string.date_picker_year_label),
                        DateFormatUtils.format(currentDay.time, "yyyy")
                    ) { onItemClick(periodHelper.yearPeriod) }
                        .apply { setSelected(periodHelper.isCurrentYear(selectedPeriod)) }
                }
            }
        }
    }
}
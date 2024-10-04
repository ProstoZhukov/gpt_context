package ru.tensor.sbis.design.period_picker.view.short_period_picker.adapter

/**
 * Тип элемента, отображаемого в выборе периода.
 *
 * @author mb.kruglova
 */
internal sealed class ShortPeriodPickerItem(val year: Int) {

    internal class YearItem(
        year: Int,
        val isYearVisible: Boolean,
        val isHeader: Boolean
    ) : ShortPeriodPickerItem(year)

    internal class HalfYearItem(
        year: Int
    ) : ShortPeriodPickerItem(year)

    internal class QuarterItem(
        year: Int,
        val isHalfYearVisible: Boolean
    ) : ShortPeriodPickerItem(year)

    internal class MonthItem(
        year: Int,
        val isQuarterVisible: Boolean,
        val isHalfYearVisible: Boolean
    ) : ShortPeriodPickerItem(year)
}
package ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.year.calendar.model

import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.year.calendar.model.QuantumItemModel.*

/**
 * Ячейка календаря, отвечающая за год.
 *
 * @property year год.
 * @property months список месяцев.
 * @property quarters список кварталов.
 * @property halfYear список полугодий.
 *
 * @author mb.kruglova
 */
internal class YearModePeriodPickerModel(
    val year: Int,
    val months: List<MonthModel>,
    val quarters: List<QuarterModel>,
    val halfYear: List<HalfYearModel>
)
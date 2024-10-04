package ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.month.calendar.model

import java.util.Calendar

/**
 * Ячейка с наименованием месяца.
 * @property date описывает первый день месяца в году.
 * @property label наименование месяца.
 * @property isRangePart true если день попадает в доступный для отображения период.
 *
 * @author mb.kruglova
 */
internal data class MonthLabelModel(
    override val date: Calendar,
    val label: String,
    val isRangePart: Boolean
) : DayItemModel {
    override val isFirstItem = true
}
package ru.tensor.sbis.design.period_picker.view.short_period_picker.models

import ru.tensor.sbis.design.period_picker.view.models.SelectionType
import java.util.Calendar

/**
 * Параметры периода.
 *
 * @param startDate дата начала периода.
 * @param selectionType тип выбираемого периода.
 *
 * @author mb.kruglova
 */
internal data class PeriodPickerParams(
    val startDate: Calendar,
    val selectionType: SelectionType
)
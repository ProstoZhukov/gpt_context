package ru.tensor.sbis.design.period_picker.view.short_period_picker.adapter.holders

import ru.tensor.sbis.design.period_picker.view.short_period_picker.models.PeriodPickerSelectionParams

/**
 * Функционал для выбора кванта.
 *
 * @author mb.kruglova
 */
internal interface QuantumSelection {

    /** Получить параметры выделения кванта. */
    fun getQuantumSelectionParams(
        startMonth: Int,
        endMonth: Int,
        horizontalViewId: Int,
        isParentConstraint: Boolean = false
    ): PeriodPickerSelectionParams

    /** Выделить год. */
    fun selectYear()
}
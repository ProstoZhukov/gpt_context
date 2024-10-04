package ru.tensor.sbis.design.period_picker.view.models

/**
 * Типы выделения периода.
 *
 * @author mb.kruglova
 */
internal enum class SelectionType {
    NO_SELECTION,
    PRESET_SELECTION,
    COMPLETE_SELECTION,
    DAY,
    MONTH,
    QUARTER,
    HALF_YEAR,
    YEAR
}
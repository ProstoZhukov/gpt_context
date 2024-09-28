package ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.drawables.models

/**
 * Тип кванта.
 *
 * @author mb.kruglova
 */
internal enum class QuantumType {

    /** Квант, не входящий в выбранный периода. */
    NO_SELECTION,

    /** Любой квант, входящий в выбранный периода, кроме начального и конечного значения периода. */
    STANDARD,

    /** Квант начала выбранного периода. */
    START,

    /** Квант окончания выбранного периода. */
    END,

    /** Квант начала и окончания выбранного периода. */
    SINGLE
}
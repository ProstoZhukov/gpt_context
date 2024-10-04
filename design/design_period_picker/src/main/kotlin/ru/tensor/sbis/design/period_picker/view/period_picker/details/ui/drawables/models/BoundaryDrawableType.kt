package ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.drawables.models

/**
 * Тип отрисовки граничных квантов в календаре.
 *
 * @author mb.kruglova
 */
internal enum class BoundaryDrawableType {

    /** Скругление в горизонтали. */
    HORIZONTAL_ROUNDING,

    /** Скругление в вертикали. */
    VERTICAL_ROUNDING,

    /** Скруглением сверху. */
    TOP_ROUNDING,

    /** Скруглением снизу. */
    BOTTOM_ROUNDING,

    /** Черта. */
    BORDER
}
package ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.drawables.models

/**
 * Тип отрисовки центральных квантов выбранного периода,
 * исключая начальный и конечный кванты выбранного периода.
 *
 * @author mb.kruglova
 */
internal enum class CentralDrawableType {

    /** Вертикальные черты. */
    VERTICAL_BORDER,

    /** Черта сверху. */
    TOP_BORDER,

    /** Черта снизу. */
    BOTTOM_BORDER,

    /** Без окантовки. */
    NO_BORDER
}
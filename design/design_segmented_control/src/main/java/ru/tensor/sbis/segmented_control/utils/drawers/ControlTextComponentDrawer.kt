package ru.tensor.sbis.segmented_control.utils.drawers

/**
 * Интерфейс рисования текстовых элементов с возможностью ограничения ширины.
 *
 * @see ControlComponentDrawer
 *
 * @author ps.smirnyh
 */
internal interface ControlTextComponentDrawer : ControlComponentDrawer {
    var maxWidth: Float
}
package ru.tensor.sbis.design.period_picker.decl

import ru.tensor.sbis.design.theme.HorizontalAlignment
import ru.tensor.sbis.design.theme.VerticalAlignment

/**
 * Класс якоря, необходимого для закрепления контейнера Выбора периода относительно вызывающей View.
 *
 * @param viewTag тег вызывающей View
 * @param verticalAlignment выравнивание по вертикали относительно вызывающей View.
 * @param horizontalAlignment выравнивание по горизонтали относительно вызывающей View.
 *
 * @author mb.kruglova
 */
class SbisPeriodPickerAnchor(
    val viewTag: String,
    val horizontalAlignment: HorizontalAlignment? = null,
    val verticalAlignment: VerticalAlignment? = null
)
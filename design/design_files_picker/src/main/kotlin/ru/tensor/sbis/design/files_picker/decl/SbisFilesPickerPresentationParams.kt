package ru.tensor.sbis.design.files_picker.decl

import ru.tensor.sbis.design.container.locator.HorizontalLocator
import ru.tensor.sbis.design.container.locator.VerticalLocator

/**
 * Общие параметры для отображения
 *
 * @property horizontalLocator  Горизонтальное позиционирование
 * @property verticalLocator    Вертикальное позиционирование
 *
 * @author ai.abramenko
 */
data class SbisFilesPickerPresentationParams(
    val horizontalLocator: HorizontalLocator,
    val verticalLocator: VerticalLocator
)

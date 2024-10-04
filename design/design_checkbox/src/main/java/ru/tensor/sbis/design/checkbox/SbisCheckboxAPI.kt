package ru.tensor.sbis.design.checkbox

import ru.tensor.sbis.design.checkbox.models.SbisCheckboxBackgroundType
import ru.tensor.sbis.design.checkbox.models.SbisCheckboxContent
import ru.tensor.sbis.design.checkbox.models.SbisCheckboxMode
import ru.tensor.sbis.design.checkbox.models.SbisCheckboxSize
import ru.tensor.sbis.design.checkbox.models.SbisCheckboxValidationState
import ru.tensor.sbis.design.checkbox.models.SbisCheckboxValue
import ru.tensor.sbis.design.theme.HorizontalPosition

/**
 * Описание API для управления чекбоксом.
 *
 * @author mb.kruglova
 */
interface SbisCheckboxAPI {

    /**
     * Режим чекбокса: по умолчанию или акцентный.
     */
    var mode: SbisCheckboxMode

    /**
     * Значение метки: без метки, с текстом или иконкой.
     */
    var content: SbisCheckboxContent

    /**
     * Расположение метки относительно чекбокса.
     */
    var position: HorizontalPosition

    /**
     * Размер чекбокса.
     */
    var size: SbisCheckboxSize

    /**
     * Значение чекбокса.
     */
    var value: SbisCheckboxValue

    /**
     * Предустановленное значение.
     */
    var presetValue: SbisCheckboxValue?

    /**
     * Включение дефолтных вертикальных отступов.
     */
    var useVerticalOffset: Boolean

    /**
     * Фон чекбокса.
     */
    var backgroundType: SbisCheckboxBackgroundType

    /**
     * Состояние валидации.
     */
    var validationState: SbisCheckboxValidationState
}
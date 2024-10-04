package ru.tensor.sbis.design.view.input.number.api

import ru.tensor.sbis.design.view.input.number.NumberInputView

/**
 * Api числового поля ввода [NumberInputView].
 *
 * @author ps.smirnyh
 */
interface NumberInputViewApi {

    /**
     * Минимальное значение для ввода.
     */
    var minValue: Double

    /**
     * Максимальное значение для ввода.
     */
    var maxValue: Double

    /**
     * Свойство, отвечающее показывать ли нулевое значение при пустой строке в поле ввода.
     */
    var isShownZeroValue: Boolean
}
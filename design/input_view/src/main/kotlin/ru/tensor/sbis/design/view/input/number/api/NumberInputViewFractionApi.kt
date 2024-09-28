package ru.tensor.sbis.design.view.input.number.api

import ru.tensor.sbis.design.view.input.number.NumberInputView

/**
 * Api числового поля ввода [NumberInputView] для введения ограничения на дробную часть.
 *
 * Данная фича специфична для [NumberInputView] и не должна реализоваться в наследнике MoneyInputViewController
 *
 * @author mb.kruglova
 */
interface NumberInputViewFractionApi {

    /**
     * Дробная часть числового поля ввода.
     * При значении, равном 0, считаем, что дробная часть должна отсутствовать.
     */
    var fraction: UByte
}
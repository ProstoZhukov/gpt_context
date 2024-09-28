package ru.tensor.sbis.design.view.input.money.api

import androidx.annotation.ColorInt
import androidx.annotation.Px
import ru.tensor.sbis.design.view.input.money.MoneyInputView
import ru.tensor.sbis.design.view.input.money.MoneyInputViewFraction
import ru.tensor.sbis.design.view.input.number.api.NumberInputViewApi

/**
 * Api денежного поля ввода [MoneyInputView].
 *
 * @author ps.smirnyh
 */
interface MoneyInputViewApi : NumberInputViewApi {

    /**
     * Если true - поле декорируется, false - нет.
     */
    var isDecorated: Boolean

    /**
     * Вид дробной части, см. [MoneyInputViewFraction].
     */
    var fraction: MoneyInputViewFraction

    /**
     * Установить размер декорированной целой части.
     */
    fun setIntegerPartSize(@Px size: Int)

    /**
     * Установить цвет декорированной целой части.
     */
    fun setIntegerPartColor(@ColorInt color: Int)

    /**
     * Установить размер декорированной дробной части.
     */
    fun setFractionPartSize(@Px size: Int)

    /**
     * Установить цвет декорированной дробной части.
     */
    fun setFractionPartColor(@ColorInt color: Int)
}
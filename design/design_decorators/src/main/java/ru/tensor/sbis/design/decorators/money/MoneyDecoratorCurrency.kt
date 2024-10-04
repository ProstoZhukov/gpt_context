package ru.tensor.sbis.design.decorators.money

/**
 * Варианты отображения иконки валюты.
 *
 * @author ps.smirnyh
 */
sealed class MoneyDecoratorCurrency(internal val char: Char) {

    /** Иконка рубля. */
    object Ruble : MoneyDecoratorCurrency('₽')

    /** Иконка доллара. */
    object Dollar : MoneyDecoratorCurrency('$')

    /** Иконка евро. */
    object Euro : MoneyDecoratorCurrency('€')

    /** Кастомная иконка валюты. */
    class Custom(char: Char) : MoneyDecoratorCurrency(char)
}
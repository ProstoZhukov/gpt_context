package ru.tensor.sbis.design.retail_views.banknote

import androidx.annotation.AttrRes
import ru.tensor.sbis.design.R

/**
 * Набор значений банкнот для 'RU' региона.
 *
 * Является "обезличенным", привязка выполняется к месту
 * расположения банкноты на UI представлении [ №столбца / №строки ].
 *
 * Т.к. кол-во банкнот, их расположение и цвет заливки
 * может меняться в зависимости от региона.
 */
internal enum class BanknoteCountry(val value: Int, @AttrRes val backgroundColorRes: Int) {
    /** Банкнота в 1 столбце 1 строке. */
    _1_1(100, R.attr.rub100BackgroundColor),

    /** Банкнота в 1 столбце 2 строке. */
    _1_2(200, R.attr.rub200BackgroundColor),

    /** Банкнота в 1 столбце 3 строке. */
    _1_3(500, R.attr.rub500BackgroundColor),

    /** Банкнота в 2 столбце 1 строке. */
    _2_1(1000, R.attr.rub1000BackgroundColor),

    /** Банкнота в 2 столбце 2 строке. */
    _2_2(2000, R.attr.rub2000BackgroundColor),

    /** Банкнота в 2 столбце 3 строке. */
    _2_3(5000, R.attr.rub5000BackgroundColor)
}
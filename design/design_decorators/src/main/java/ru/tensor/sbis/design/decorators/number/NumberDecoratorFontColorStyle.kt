package ru.tensor.sbis.design.decorators.number

import ru.tensor.sbis.design.decorators.FontColorStyle

/**
 * Стиль текста числового декоратора.
 *
 * @author ps.smirnyh
 */
data class NumberDecoratorFontColorStyle(
    internal val integerPartColor: FontColorStyle,
    internal val fractionPartColor: FontColorStyle? = null
)
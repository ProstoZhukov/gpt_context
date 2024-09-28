package ru.tensor.sbis.design.decorators.money

import ru.tensor.sbis.design.decorators.FontColorStyle
import ru.tensor.sbis.design.decorators.number.NumberDecoratorConfig
import ru.tensor.sbis.design.decorators.number.NumberDecoratorConfigApi
import ru.tensor.sbis.design.theme.HorizontalPosition
import ru.tensor.sbis.design.theme.global_variables.FontSize

/**
 * Класс конфигурации настроек денежного декоратора.
 *
 * @author ps.smirnyh
 */
internal data class MoneyDecoratorConfig(
    val numberConfig: NumberDecoratorConfig = NumberDecoratorConfig(),
    override var currency: MoneyDecoratorCurrency? = null,
    override var currencyPosition: HorizontalPosition = HorizontalPosition.LEFT,
    override var currencySize: FontSize? = null,
    override var currencyStyle: FontColorStyle? = null
) : NumberDecoratorConfigApi by numberConfig, MoneyDecoratorConfigApi {
    override fun changedCurrency(other: MoneyDecoratorConfigApi): Boolean =
        currency != other.currency ||
            currencyPosition != other.currencyPosition ||
            currencySize != other.currencySize ||
            currencyStyle != other.currencyStyle
}
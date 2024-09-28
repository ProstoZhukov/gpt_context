package ru.tensor.sbis.design.decorators.money

import ru.tensor.sbis.design.decorators.FontColorStyle
import ru.tensor.sbis.design.decorators.number.NumberDecoratorConfigApi
import ru.tensor.sbis.design.theme.HorizontalPosition
import ru.tensor.sbis.design.theme.global_variables.FontSize

/**
 * Api для класса конфигурации денежного декоратора.
 *
 * @author ps.smirnyh
 */
interface MoneyDecoratorConfigApi : NumberDecoratorConfigApi {

    /** Иконка валюты. */
    var currency: MoneyDecoratorCurrency?

    /** Положение иконки валюты. */
    var currencyPosition: HorizontalPosition

    /** Размер иконки валюты. */
    var currencySize: FontSize?

    /** Стиль иконки валюты. */
    var currencyStyle: FontColorStyle?

    /** Изменились ли настройки, связанные с валютой. */
    fun changedCurrency(other: MoneyDecoratorConfigApi): Boolean
}
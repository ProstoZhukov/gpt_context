package ru.tensor.sbis.design.decorators.money

import java.math.BigDecimal

/**
 * Api денежного декоратора.
 *
 * @author ps.smirnyh
 */
interface MoneyDecoratorApi {

    /** @SelfDocumented */
    fun changeValue(newValue: BigDecimal?)
}
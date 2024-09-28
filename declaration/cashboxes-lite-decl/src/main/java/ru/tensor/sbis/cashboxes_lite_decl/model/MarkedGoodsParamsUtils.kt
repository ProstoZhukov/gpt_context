package ru.tensor.sbis.cashboxes_lite_decl.model

import java.math.BigDecimal

/** Возвращает минимально допустимую цену на пачку или блок табака */
fun getMinTobaccoPriceDependOfPack(minTobaccoPrice: BigDecimal, isBlock: Boolean): BigDecimal {
    return if (isBlock) minTobaccoPrice.multiply(BigDecimal.TEN) else minTobaccoPrice
}
package ru.tensor.sbis.cashboxes_lite_decl.model

import java.math.BigDecimal

/**
 * Максимально доступное к реализации количество товара, выраженное в конкретной упаковке.
 *
 * @property pack Упаковка
 * @property maxSaleQty Максимально доступное количество к продаже.
 * @property maxRevertQty Максимаьно доступное количество к возврату.
 */
data class PackWithMaxQty(
    val pack: PacksWithPrice,
    val maxSaleQty: BigDecimal,
    val maxRevertQty: BigDecimal
)
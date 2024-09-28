package ru.tensor.sbis.cashboxes_lite_decl.model

/**
 * Интерфейс для получения данных кода маркировки и результатов валидации
 */
interface MarkingCodeValidationInfo {
    /**@SelfDocumented*/
    val markedGoodsParams: MarkedGoodsParams?

    /**@SelfDocumented*/
    val pack: PacksWithPrice?
}
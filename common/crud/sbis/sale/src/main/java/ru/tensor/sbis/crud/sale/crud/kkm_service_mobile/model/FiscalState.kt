package ru.tensor.sbis.crud.sale.crud.kkm_service_mobile.model

/** Состояние фискального регистратора. */
data class FiscalState(
    /** Установлен ли фискальник. */
    val isSetup: Boolean,
    /** Закрыт ли фискальник. */
    val isClosed: Boolean,
    /** Фискализирован ККМ или нет. */
    val hasFiscal: Boolean,
    /** Совпадает ли дата на девайсе и устройстве. */
    val dateMatch: Boolean
)
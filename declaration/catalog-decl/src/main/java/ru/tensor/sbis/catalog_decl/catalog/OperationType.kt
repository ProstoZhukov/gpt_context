package ru.tensor.sbis.catalog_decl.catalog

/** Тип операции, для обозначения контекста в работе функциональности */
enum class OperationType {
    /** Продажа. */
    SALE,

    /** Возврат. */
    REFUND,

    /** Коррекция. */
    CORRECTION
}
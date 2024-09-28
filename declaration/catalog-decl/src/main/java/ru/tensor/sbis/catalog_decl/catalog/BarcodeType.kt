package ru.tensor.sbis.catalog_decl.catalog

/**
 *  Тип штрихкода
 */
enum class BarcodeType {
    EAN8,
    EAN13,
    EAN13_2,
    EAN13_5,
    UPS,
    USER,
    EGAIS,
    SHORT,
    GTIN,
    TRU,
    INVALID,
    MAX_ONLINE_VALUE
}
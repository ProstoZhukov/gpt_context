package ru.tensor.sbis.catalog_decl.catalog

/**
 * Типы маркированной продукции.
 *
 * @author mv.ilin
 */
enum class MarkedProductionType {

    /**
     * 0 - товар
     */
    PRODUCT,

    /**
     * 1 - набор (на онлайн перводится как Kit)
     */
    SET,

    /**
     * 2 - комплект (на онлайн перводится как Set)
     */
    KIT,

    /**
     * 3 - невалидное значение
     */
    INVALID,

    /**
     * 4 - Максимально возможно на онлайне +1
     */
    MAX_ONLINE_VALUE,

}
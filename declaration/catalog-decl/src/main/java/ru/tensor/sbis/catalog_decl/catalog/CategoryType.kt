package ru.tensor.sbis.catalog_decl.catalog

/**
 * Категория наименования.
 *
 * @author aa.mezencev
 */
enum class CategoryType(val isRequiredPrice: Boolean = false) {
    /** товары */
    PRODUCTS,

    /** услуги и работы */
    SERVICES,

    /** неисключительные права */
    NON_EXCLUSIVE_RIGHTS,

    /** материалы */
    MATERIALS,

    /** готовая продукция */
    FINISHED_GOODS,

    /** бухлишко */
    ALCOHOL,

    /** ветеринария */
    VETERINARY,

    /** лекарственные препараты */
    DRUGS,

    /** подарочный сертификат */
    GIFT_CERTIFICATE(isRequiredPrice = true),

    /** комплекты */
    COMPLECTS,

    /** магазин подарков */
    GIFTS_SHOP,

    /** максмально возможно на онлайне +1 */
    MAX_ONLINE_VALUE,

    /** невалидное значение */
    INVALID;
}
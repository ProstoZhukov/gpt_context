package ru.tensor.sbis.catalog_decl.catalog

/**
 * Тип учета наименования.
 *
 * @author aa.mezencev
 */
enum class AccountingType {

    /** Количественно-суммовой */
    COUNT_AND_SUM,

    /** Суммовой */
    SUM,

    /** Без учета */
    NONE,

    /** Комплект без учета */
    COMPLECT_WITHOUT_ACCOUNTING,

    /** Комплект c учетом */
    COMPLECT_WITH_ACCOUNTING,

    /** максмально возможно на онлайне +1 */
    MAX_ONLINE_VALUE,

    /** невалидное значение */
    INVALID;
}
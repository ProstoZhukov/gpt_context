package ru.tensor.sbis.design.retail_models

/**
 * Перечисление поддерживаемых систем налогообложения.
 * @author s.r.golovkin
 */
enum class UiTaxSystemCode {

    /** Не поддерживается. */
    NOT_SUPPORTED,

    /** ОСН.*/
    GENERAL,

    /** УСН доход. */
    SIMPLIFIED_INCOME,

    /** УСН доход - расход. */
    SIMPLIFIED_INCOME_EXPENDITURE,

    /** ЕНВД. */
    UNIFIED_ON_IMPUTED_INCOME,

    /** ЕСХН. */
    UNIFIED_AGRICULTURAL,

    /** Патент. */
    PATENT
}
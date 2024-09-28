package ru.tensor.sbis.design.topNavigation.api.footer

/**
 * Поведение при скролле для подвала.
 *
 * @author da.zolotarev
 */
enum class SbisTopNavigationFooterBehavior {
    /** Фиксированное. Не скрывается при скролле. */
    FIXED,

    /** Скрывается при скролле. Изначально не скрыт. */
    INITIALLY_VISIBLE,

    /** Скрывается при скролле. Изначально скрыт. */
    INITIALLY_HIDDEN
}
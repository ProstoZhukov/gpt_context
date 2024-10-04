package ru.tensor.sbis.design.topNavigation.api

/**
 * Контекст, в который встраивается шапка.
 *
 * @author da.zolotarev
 */
enum class SbisTopNavigationPresentationContext {
    /** @SelfDocumented */
    DEFAULT,

    /** Модальное окно (отображение отличается видом кнопки навигации "Назад"). */
    MODAL
}
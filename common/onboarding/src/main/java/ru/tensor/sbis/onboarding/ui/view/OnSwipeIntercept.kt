package ru.tensor.sbis.onboarding.ui.view

/**
 * Интерфейс для фрагмента кастомной страницы onboarding
 *
 * Позволяет перехватывать свайпы вперед и назад.
 * Используется в МП Витрина
 */
interface OnSwipeIntercept {

    /**
     * Событие на свайп вперед
     *
     * @return true если свайп вперед перехвачен и дальше обрабратываться не будет
     */
    fun onSwipeForwardIntercept(): Boolean

    /**
     * Событие на свайп назад
     *
     * @return true если свайп назад перехвачен и дальше обрабратываться не будет
     */
    fun onSwipeBackIntercept(): Boolean
}
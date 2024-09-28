package ru.tensor.sbis.business.common.ui.base.fragment

/**
 * Интерфейс переопределения и управления анимацией фрагмента
 */
interface TransitionAnimator {
    /** Отключить анимацию по скрытию фрагмента */
    fun disableExitTransitionAnimation()
}
package ru.tensor.sbis.onboarding.ui.base

/**
 * Интерфейс объекта реализующего обработку события перелистывания
 * на следующий / предыдущий экран Приветственного экрана
 * Используется в приложении sabyget (showcase)
 *
 * @author as.chadov
 */
interface OnboardingNextPage {

    /**
     * Обработать переход на следующий экран
     *
     * @param endless true если выполнить бесконечный переход на следующий экран
     * (начать заново при необходимости). По-умолчанию false
     */
    fun goNextPage(endless: Boolean = false)

    /**
     * Обработать переход на предыдущий экран
     */
    fun goPreviousPage()
}
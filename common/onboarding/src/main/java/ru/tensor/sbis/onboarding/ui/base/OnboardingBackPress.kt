package ru.tensor.sbis.onboarding.ui.base

/**
 * Интерфейс объекта реализующего обработку события возврата
 * на предыдущий экран Приветственного экрана
 * Используется в приложении sabyget (showcase)
 *
 * @author as.chadov
 */
interface OnboardingBackPress {

    /**
     * Обработать возврат на предыдущий экран
     *
     * @return true если обработано успешно
     */
    fun onBackPressed(): Boolean

}
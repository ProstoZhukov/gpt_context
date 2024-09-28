package ru.tensor.sbis.verification_decl.onboarding

import androidx.annotation.IdRes
import androidx.fragment.app.Fragment

/**
 * Навигатор по фрагментам "Приветственного Экрана" из модуля onboarding
 *
 * @author as.chadov
 */
interface OnboardingNavigator {

    /**
     * Открыть следующий экран фрагмент-фичи
     */
    fun moveNextPage()

    /**
     * Открыть предыдущий экран фрагмент-фичи
     */
    fun movePreviousPage()

    /**
     * Открыть фрагмент поверх основного приветственного экрана
     *
     * @param creator объект создатель фрагмента
     * @param containerId идентификатор контейнера для создаваемого фрагмента
     * Пропустить при использовании [OnboardingActivity]
     */
    fun showFragmentOnTop(
        creator: (() -> Fragment),
        screenKey: String = "",
        @IdRes containerId: Int = 0
    )

    /**
     * Скрыть фрагмент ранее добавленный поверх основного приветственного экрана
     */
    fun dismissFragment(
        screenKey: String = ""
    )
}
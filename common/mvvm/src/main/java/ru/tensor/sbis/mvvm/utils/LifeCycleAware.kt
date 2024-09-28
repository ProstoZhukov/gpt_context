package ru.tensor.sbis.mvvm.utils

import androidx.fragment.app.FragmentManager

/**
 * Сallback-и жизненного цикла
 *
 * @param <LIFE_CYCLE_CALLBACK_HOLDER> тип обладателя жизненного цикла
 */
@Deprecated("Устаревший подход, переходим на mvi_extension")
interface LifeCycleAware<LIFE_CYCLE_CALLBACK_HOLDER> {

    /**
     * Callback выхода на передний план
     */
    fun resume(
        lifeCycleCallbackHolder: LIFE_CYCLE_CALLBACK_HOLDER,
        fragmentManager: FragmentManager
    )

    /**
     * Callback перехода в фон
     */
    fun pause()
}
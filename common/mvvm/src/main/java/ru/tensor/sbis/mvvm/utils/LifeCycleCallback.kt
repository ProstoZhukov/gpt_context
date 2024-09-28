package ru.tensor.sbis.mvvm.utils

import androidx.fragment.app.FragmentManager

/**
 * Пустой обработчик callback'ов жизненного цикла
 *
 * @param <LIFE_CYCLE_CALLBACK_HOLDER> тип обладателя жизненного цикла
 */
@Deprecated("Устаревший подход, переходим на mvi_extension")
abstract class LifeCycleCallback<LIFE_CYCLE_CALLBACK_HOLDER> :
    LifeCycleAware<LIFE_CYCLE_CALLBACK_HOLDER> {

    /**
     * Callback выхода на передний план
     */
    override fun resume(
        lifeCycleCallbackHolder: LIFE_CYCLE_CALLBACK_HOLDER,
        fragmentManager: FragmentManager
    ) {
    }

    /**
     * Callback перехода в фон
     */
    override fun pause() {}
}
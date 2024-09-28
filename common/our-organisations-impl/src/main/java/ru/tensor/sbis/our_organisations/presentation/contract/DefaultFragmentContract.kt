package ru.tensor.sbis.our_organisations.presentation.contract

import android.os.Bundle
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

/**
 * Котракт для создания фрагмента и получения результата через FragmentResultApi
 *
 * @author as.mozgolin
 */
internal abstract class DefaultFragmentContract<FACTORY, RESULT> : FragmentContract<FACTORY, RESULT> {
    abstract val requestKey: String

    override fun register(
        fragmentManager: () -> FragmentManager,
        lifecycleOwner: LifecycleOwner,
        onResult: (RESULT) -> Unit
    ): FACTORY {
        lifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onCreate(owner: LifecycleOwner) {
                fragmentManager().setFragmentResultListener(requestKey, lifecycleOwner) { key, data ->
                    if (key == requestKey) {
                        onResult(extractResult(data))
                    }
                }
            }
        })

        return getFactory()
    }

    /**
     * Получение информации из бандла
     * @param data Данные из FragmentResultListener
     */
    protected abstract fun extractResult(data: Bundle): RESULT

    /**
     * Создание фабрики для фрагмента
     */
    protected abstract fun getFactory(): FACTORY
}

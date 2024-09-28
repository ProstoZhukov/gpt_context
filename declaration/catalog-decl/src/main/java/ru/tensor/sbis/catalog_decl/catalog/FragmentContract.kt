package ru.tensor.sbis.catalog_decl.catalog

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner

/**
 * Контракт для создания фрагмента и получения результата из него
 *
 * @author as.mozgolin
 */
interface FragmentContract<FACTORY, RESULT> {

    /**
     * Регистрирует обработчик событий из фрагмета
     *
     * @param fragmentManager [FragmentManager]
     * @param lifecycleOwner [LifecycleOwner]
     * @param onResult Обработчик событий из фрагмента
     * @return фабрика для создания фрагмента
     */
    fun register(
        fragmentManager: () -> FragmentManager,
        lifecycleOwner: LifecycleOwner,
        onResult: (RESULT) -> Unit
    ): FACTORY
}

/**
 * Региструет обработчик событий для [FragmentContract] в указаном фрагменте
 *
 * @param fragment [Fragment]
 * @param onResult Обработчик событий из фрагмента
 * @return фабрика для создания фрагмента
 */
fun <FACTORY, RESULT> FragmentContract<FACTORY, RESULT>.register(
    fragment: Fragment,
    onResult: (RESULT) -> Unit
): FACTORY {
    return register({ fragment.childFragmentManager }, fragment, onResult)
}
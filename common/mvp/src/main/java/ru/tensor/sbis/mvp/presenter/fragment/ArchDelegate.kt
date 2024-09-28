package ru.tensor.sbis.mvp.presenter.fragment

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import ru.tensor.sbis.mvp.presenter.BaseArchDelegate

/**
 * Реализация делегата для фрагмента
 */
@Deprecated("Устаревший подход, переходим на mvi_extension")
class ArchDelegate<VIEW_MODEL>(
    private val fragment: Fragment,
    creatingMethod: () -> VIEW_MODEL,
    key: String? = null,
    doOnCleared: (VIEW_MODEL) -> Unit = {}
) : BaseArchDelegate<VIEW_MODEL>(creatingMethod, key, doOnCleared) {

    override fun getViewModelProvider(factory: ViewModelProvider.Factory): ViewModelProvider =
        ViewModelProvider(fragment, factory)
}
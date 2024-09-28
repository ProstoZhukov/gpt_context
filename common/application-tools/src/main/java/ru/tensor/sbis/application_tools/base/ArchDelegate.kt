package ru.tensor.sbis.application_tools.base

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders

/**
 * @author du.bykov
 *
 * Реализация [BaseArchDelegate] с использованием фрагмента в качестве хранилища модели представления.
 */
class ArchDelegate<VIEW_MODEL>(
    private val fragment: Fragment,
    creatingMethod: () -> VIEW_MODEL,
    key: String? = null,
    doOnCleared: (VIEW_MODEL) -> Unit = {}
) : BaseArchDelegate<VIEW_MODEL>(creatingMethod, key, doOnCleared) {

    override fun getViewModelProvider(factory: ViewModelProvider.Factory): ViewModelProvider {
        return ViewModelProviders.of(fragment, factory)
    }
}
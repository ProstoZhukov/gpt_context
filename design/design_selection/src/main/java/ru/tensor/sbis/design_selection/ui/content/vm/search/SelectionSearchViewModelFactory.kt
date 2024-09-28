package ru.tensor.sbis.design_selection.ui.content.vm.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * Фабрика для создания вью-модели поисковой строки.
 *
 * @author vv.chekurda
 */
internal class SelectionSearchViewModelFactory : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass == SelectionSearchViewModelImpl::class.java)
        @Suppress("UNCHECKED_CAST")
        return SelectionSearchViewModelImpl() as T
    }
}
package ru.tensor.sbis.design.selection.ui.utils.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * Реализация [ViewModelProvider.Factory], которая способна создавать только [SelectorListScreenViewModel]
 *
 * @author ma.kolpakov
 */
internal class SearchViewModelFactory(
    private val searchQueryMinLength: Int,
    private val useCaseValue: String
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass == SearchViewModelImpl::class.java)
        @Suppress("UNCHECKED_CAST")
        return SearchViewModelImpl(searchQueryMinLength, useCaseValue) as T
    }
}
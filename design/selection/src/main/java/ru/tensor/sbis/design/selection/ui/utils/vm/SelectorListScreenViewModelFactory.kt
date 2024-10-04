package ru.tensor.sbis.design.selection.ui.utils.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.tensor.sbis.design.selection.ui.list.SelectionListInteractor
import ru.tensor.sbis.design.selection.ui.list.SelectionListScreenEntity
import ru.tensor.sbis.design.selection.ui.list.filter.SelectorFilterCreator
import ru.tensor.sbis.design.selection.ui.utils.vm.choose_all.FixedButtonViewModel
import ru.tensor.sbis.list.base.presentation.ListScreenVMImpl

/**
 * Реализация [ViewModelProvider.Factory], которая способна создавать только [SelectorListScreenViewModel]
 *
 * @author ma.kolpakov
 */
internal class SelectorListScreenViewModelFactory(
    private val entity: SelectionListScreenEntity<Any, Any, Any>,
    private val filterCreator: SelectorFilterCreator<Any, Any>,
    private val interactor: SelectionListInteractor<Any, Any, Any, SelectionListScreenEntity<Any, Any, Any>>,
    private val fixedButtonVm: FixedButtonViewModel<Any>,
    private val listScreenVMImpl: ListScreenVMImpl<SelectionListScreenEntity<Any, Any, Any>>
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass == SelectorListScreenViewModel::class.java)
        @Suppress("UNCHECKED_CAST")
        return SelectorListScreenViewModel(
            entity,
            filterCreator,
            interactor,
            listScreenVMImpl,
            fixedButtonVm
        ) as T
    }
}
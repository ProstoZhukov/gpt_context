package ru.tensor.sbis.design.selection.ui.utils.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.tensor.sbis.design.selection.bl.vm.selection.single.SingleSelectionViewModelImpl
import ru.tensor.sbis.design.selection.ui.contract.SingleSelectionLoader
import ru.tensor.sbis.design.selection.ui.factories.ItemMetaFactory
import ru.tensor.sbis.design.selection.ui.model.SelectorItemModel

/**
 * Реализация [ViewModelProvider.Factory] для создания [SingleSelectionViewModelImpl]
 *
 * @author us.bessonov
 */
internal class SingleSelectorHostViewModelFactory<DATA : SelectorItemModel>(
    private val selectionLoader: SingleSelectionLoader<DATA>,
    private val metaFactory: ItemMetaFactory
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass == SingleSelectionViewModelImpl::class.java)
        @Suppress("UNCHECKED_CAST")
        return SingleSelectionViewModelImpl(selectionLoader, metaFactory) as T
    }
}
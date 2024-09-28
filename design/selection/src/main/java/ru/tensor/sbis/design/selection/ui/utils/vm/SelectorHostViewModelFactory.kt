package ru.tensor.sbis.design.selection.ui.utils.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.tensor.sbis.design.selection.bl.vm.completion.DoneButtonViewModel
import ru.tensor.sbis.design.selection.bl.vm.selection.multi.MultiSelectionViewModelImpl
import ru.tensor.sbis.design.selection.ui.contract.MultiSelectionLoader
import ru.tensor.sbis.design.selection.ui.contract.SelectorSelectionMode
import ru.tensor.sbis.design.selection.ui.factories.ItemMetaFactory
import ru.tensor.sbis.design.selection.ui.model.SelectorItemModel

/**
 * Фабрика вью моделей, жизненный цикл которых связан с корневым фрагментом
 *
 * @author ma.kolpakov
 */
internal class SelectorHostViewModelFactory<DATA : SelectorItemModel>(
    private val selectionLoader: MultiSelectionLoader<DATA>,
    private val metaFactory: ItemMetaFactory,
    private val selectionLimit: Int,
    private val selectionMode: SelectorSelectionMode,
    private val doneButtonViewModel: DoneButtonViewModel
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass == MultiSelectionViewModelImpl::class.java)
        @Suppress("UNCHECKED_CAST")
        return MultiSelectionViewModelImpl(
            selectionLoader,
            metaFactory,
            selectionLimit,
            selectionMode,
            doneButtonViewModel
        ) as T
    }
}
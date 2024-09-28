package ru.tensor.sbis.design.selection.bl.vm.selection.multi.mode

import io.reactivex.Observable
import ru.tensor.sbis.design.selection.bl.contract.model.SelectorItem
import ru.tensor.sbis.design.selection.bl.vm.selection.multi.MultiSelectionViewModelImpl
import ru.tensor.sbis.design.selection.bl.vm.selection.multi.command.SelectionCommandHandler

/**
 * Интерфейс фабрики для выделения процесса создания [AbstractSelectionModeHandler] из [MultiSelectionViewModelImpl].
 * Реализации тесно связаны и влияют друг на друга
 *
 * @author ma.kolpakov
 */
internal interface SelectionModeHandlerFactory<DATA : SelectorItem> {

    fun createSelectionHandler(
        selectionViewModel: MultiSelectionViewModelImpl<DATA>,
        commandHandler: SelectionCommandHandler<DATA>,
        selectedItems: Observable<List<DATA>>,
        limit: Int
    ): AbstractSelectionModeHandler<DATA>
}
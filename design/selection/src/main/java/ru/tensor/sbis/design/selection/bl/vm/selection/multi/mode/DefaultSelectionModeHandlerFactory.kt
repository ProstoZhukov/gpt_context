package ru.tensor.sbis.design.selection.bl.vm.selection.multi.mode

import io.reactivex.Observable
import ru.tensor.sbis.design.selection.bl.contract.model.SelectorItem
import ru.tensor.sbis.design.selection.bl.vm.selection.multi.MultiSelectionViewModelImpl
import ru.tensor.sbis.design.selection.bl.vm.selection.multi.command.SelectionCommandHandler
import ru.tensor.sbis.design.selection.ui.contract.SelectorSelectionMode

/**
 * Реализация, которая создаёт [AbstractSelectionModeHandler] на основе режима работы [mode]
 *
 * @author ma.kolpakov
 */
internal class DefaultSelectionModeHandlerFactory<DATA : SelectorItem>(
    private val mode: SelectorSelectionMode
) : SelectionModeHandlerFactory<DATA> {

    override fun createSelectionHandler(
        selectionViewModel: MultiSelectionViewModelImpl<DATA>,
        commandHandler: SelectionCommandHandler<DATA>,
        selectedItems: Observable<List<DATA>>,
        limit: Int
    ): AbstractSelectionModeHandler<DATA> = when (mode) {
        SelectorSelectionMode.REPLACE_ALL_IF_FIRST ->
            OverrideSelectionModeHandler(commandHandler, limit, selectedItems, selectionViewModel::complete)
        SelectorSelectionMode.ALWAYS_ADD ->
            AddSelectionModeHandler(commandHandler, limit)
        SelectorSelectionMode.REPLACE_ALL ->
            TODO("TODO: 10/15/2020 https://online.sbis.ru/opendoc.html?guid=38c8da1e-30f8-4b81-aa7e-73b11a1fda21")
    }
}
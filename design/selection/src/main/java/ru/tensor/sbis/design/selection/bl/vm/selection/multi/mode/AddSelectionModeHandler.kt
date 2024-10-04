package ru.tensor.sbis.design.selection.bl.vm.selection.multi.mode

import io.reactivex.disposables.Disposable
import io.reactivex.disposables.Disposables
import ru.tensor.sbis.design.selection.bl.contract.model.SelectorItem
import ru.tensor.sbis.design.selection.bl.vm.selection.multi.command.SelectionCommandHandler
import ru.tensor.sbis.design.selection.ui.contract.SelectorSelectionMode

/**
 * Реализация [AbstractSelectionModeHandler] для режима [SelectorSelectionMode.ALWAYS_ADD]
 *
 * @author ma.kolpakov
 */
internal class AddSelectionModeHandler<DATA : SelectorItem>(
    commandHandler: SelectionCommandHandler<DATA>,
    limit: Int,
) : AbstractSelectionModeHandler<DATA>(commandHandler, limit) {

    override fun setSelected(data: DATA): Disposable {
        toggleSelection(data)
        return Disposables.disposed()
    }
}
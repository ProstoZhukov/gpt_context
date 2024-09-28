package ru.tensor.sbis.design.selection.bl.vm.selection.multi.mode

import androidx.annotation.CallSuper
import androidx.annotation.CheckResult
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import ru.tensor.sbis.design.selection.bl.contract.model.SelectorItem
import ru.tensor.sbis.design.selection.bl.vm.selection.multi.MultiSelectionViewModel
import ru.tensor.sbis.design.selection.bl.vm.selection.multi.command.AddSelectionCommand
import ru.tensor.sbis.design.selection.bl.vm.selection.multi.command.RemoveSelectionCommand
import ru.tensor.sbis.design.selection.bl.vm.selection.multi.command.SelectionCommandHandler
import ru.tensor.sbis.design.selection.ui.contract.SelectorSelectionMode

/**
 * Обработчик пользовательского выбора. Реализации специфичных правил определяются на уровне расширений класса
 *
 * @see SelectorSelectionMode
 *
 * @author ma.kolpakov
 */
internal abstract class AbstractSelectionModeHandler<DATA : SelectorItem>(
    protected val commandHandler: SelectionCommandHandler<DATA>,
    protected val limit: Int
) {

    protected val limitSubject = PublishSubject.create<Int>()

    val limitObservable: Observable<Int> = limitSubject

    /**
     * @see MultiSelectionViewModel.setSelected
     */
    @CheckResult
    abstract fun setSelected(data: DATA): Disposable

    /**
     * @see MultiSelectionViewModel.removeSelection
     */
    fun removeSelection(data: DATA) {
        commandHandler.postCommand(RemoveSelectionCommand(data))
    }

    /**
     * @see MultiSelectionViewModel.toggleSelection
     */
    @CallSuper
    open fun toggleSelection(data: DATA) {
        if (data.meta.selected) {
            removeSelection(data)
        } else {
            commandHandler.postCommand(AddSelectionCommand(data, limit, limitSubject))
        }
    }
}
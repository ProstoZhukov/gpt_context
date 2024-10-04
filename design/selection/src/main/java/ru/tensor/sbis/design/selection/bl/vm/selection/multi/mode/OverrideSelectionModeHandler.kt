package ru.tensor.sbis.design.selection.bl.vm.selection.multi.mode

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import ru.tensor.sbis.design.selection.bl.contract.model.SelectorItem
import ru.tensor.sbis.design.selection.bl.vm.selection.multi.command.OverrideSelectionCommand
import ru.tensor.sbis.design.selection.bl.vm.selection.multi.command.SelectionCommandHandler
import ru.tensor.sbis.design.selection.ui.contract.SelectorSelectionMode

/**
 * Реализация [AbstractSelectionModeHandler] для режима [SelectorSelectionMode.REPLACE_ALL_IF_FIRST]
 *
 * @author ma.kolpakov
 */
internal class OverrideSelectionModeHandler<DATA : SelectorItem>(
    commandHandler: SelectionCommandHandler<DATA>,
    limit: Int,
    private val selectedItemsObservable: Observable<List<DATA>>,
    private val onComplete: (data: DATA) -> Unit
) : AbstractSelectionModeHandler<DATA>(commandHandler, limit) {

    /**
     * Отметка о том, что оригинальный список был изменён
     */
    private var selectionWasChanged = false

    /**
     * Поток процедуры выбора или переопределения. По умолчанию [Completable.complete] для корректной работы в
     * случае простого выбора, когда пользователь изменил оригинальный список [selectionWasChanged] не через
     * [applySelection], а через [toggleSelection]
     *
     * @see setSelected
     */
    private var overridingCompletable: Completable = Completable.complete()

    override fun setSelected(data: DATA): Disposable {
        return if (selectionWasChanged)
        // дожидаемся загрузки прошлого выбора и процедуры переопределения
            overridingCompletable.andThen { toggleSelection(data) }.subscribe()
        else
        /*
        Ещё ничего не меняли. Теперь нужно дождаться загрузки прошлого выбора, чтобы решить, какую стратегию
        применять: немедленное завершение (при отсутствии выбора) или переопределение
         */
            applySelection(data)
    }

    override fun toggleSelection(data: DATA) {
        super.toggleSelection(data)
        selectionWasChanged = true
    }

    /**
     * Метод дожидается пользовательского выбора и, в зависимости от его наличия, завершает выбор или переопределяет его
     */
    private fun applySelection(data: DATA): Disposable {
        selectionWasChanged = true
        overridingCompletable = selectedItemsObservable.firstOrError().flatMapCompletable { selection ->
            if (selection.isEmpty())
                Completable.fromAction { onComplete(data) }
            else
                Completable.fromAction { commandHandler.postCommand(OverrideSelectionCommand(data)) }
        }.cache()
        return overridingCompletable.subscribe()
    }
}
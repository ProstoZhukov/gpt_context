package ru.tensor.sbis.design.selection.bl.vm.selection.multi

import androidx.annotation.WorkerThread
import androidx.lifecycle.ViewModel
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import ru.tensor.sbis.design.selection.bl.contract.listener.ClickHandleStrategy.COMPLETE_SELECTION
import ru.tensor.sbis.design.selection.bl.contract.listener.ClickHandleStrategy.DEFAULT
import ru.tensor.sbis.design.selection.bl.contract.listener.ClickHandleStrategy.IGNORE
import ru.tensor.sbis.design.selection.bl.contract.model.SelectorItem
import ru.tensor.sbis.design.selection.bl.utils.CompleteFunction
import ru.tensor.sbis.design.selection.bl.vm.completion.DoneButtonState
import ru.tensor.sbis.design.selection.bl.vm.completion.DoneButtonViewModel
import ru.tensor.sbis.design.selection.bl.vm.selection.ApplySelection
import ru.tensor.sbis.design.selection.bl.vm.selection.CancelSelection
import ru.tensor.sbis.design.selection.bl.vm.selection.CompleteEvent
import ru.tensor.sbis.design.selection.bl.vm.selection.SetSelection
import ru.tensor.sbis.design.selection.bl.vm.selection.filterSelectedItems
import ru.tensor.sbis.design.selection.bl.vm.selection.multi.command.SelectionCommandHandler
import ru.tensor.sbis.design.selection.bl.vm.selection.multi.mode.DefaultSelectionModeHandlerFactory
import ru.tensor.sbis.design.selection.bl.vm.selection.multi.mode.SelectionModeHandlerFactory
import ru.tensor.sbis.design.selection.ui.contract.MultiSelectionLoader
import ru.tensor.sbis.design.selection.ui.contract.SelectorSelectionMode
import ru.tensor.sbis.design.selection.ui.factories.ItemMetaFactory
import ru.tensor.sbis.design.selection.ui.model.SelectorItemModel

/**
 * Реализация по умолчанию для [SelectorItemModel]
 *
 * @author ma.kolpakov
 */
internal class MultiSelectionViewModelImpl<DATA : SelectorItem>(
    private val selectionLoader: MultiSelectionLoader<DATA>,
    private val metaFactory: ItemMetaFactory,
    limit: Int,
    selectionModeHandlerFactory: SelectionModeHandlerFactory<DATA>,
    doneButtonVmDelegate: DoneButtonViewModel,
    observeOn: Scheduler = AndroidSchedulers.mainThread()
) : ViewModel(),
    MultiSelectionViewModel<DATA>,
    DoneButtonState by doneButtonVmDelegate {

    private val disposable = CompositeDisposable()

    private val scheduler = Schedulers.single()

    private val commandHandler = SelectionCommandHandler<DATA>(scheduler)

    private val completeSubject = PublishSubject.create<CompleteEvent<DATA>>()

    private val initialSelection = Single.fromCallable(::loadInitialSelection)
        .subscribeOn(scheduler)
        .cache()

    private val selectedItemsObservable = commandHandler
        .startWith(initialSelection)
        .observeOn(observeOn)
        .replay(1)

    private val selectionHandler = selectionModeHandlerFactory
        .createSelectionHandler(this, commandHandler, selectedItemsObservable, limit)

    override val result: Maybe<List<DATA>> = completeSubject.withLatestFrom(
        selectedItemsObservable.startWith(emptyList<DATA>()),
        CompleteFunction<DATA>()
    )
        .flatMap { it }
        .firstElement()

    override val selection: Observable<List<DATA>> = selectedItemsObservable
        .filterSelectedItems(result.toObservable())

    override val limitExceed: Observable<Int> = selectionHandler.limitObservable.observeOn(observeOn)

    init {
        disposable.addAll(
            initialSelection.subscribe(doneButtonVmDelegate::setInitialData),
            selectedItemsObservable.subscribe(doneButtonVmDelegate::setSelectedData),
            selectedItemsObservable.connect()
        )
    }

    constructor(
        selectionLoader: MultiSelectionLoader<DATA>,
        metaFactory: ItemMetaFactory,
        limit: Int,
        selectionMode: SelectorSelectionMode,
        doneButtonVmDelegate: DoneButtonViewModel,
        observeOn: Scheduler = AndroidSchedulers.mainThread()
    ) : this(
        selectionLoader,
        metaFactory,
        limit,
        DefaultSelectionModeHandlerFactory(selectionMode),
        doneButtonVmDelegate,
        observeOn = observeOn
    )

    override fun setSelected(data: DATA) {
        when (data.meta.handleStrategy) {
            COMPLETE_SELECTION -> complete(data)
            DEFAULT -> disposable.add(selectionHandler.setSelected(data))
            IGNORE -> Unit
        }
    }

    override fun removeSelection(data: DATA) {
        selectionHandler.removeSelection(data)
    }

    override fun toggleSelection(data: DATA) {
        selectionHandler.toggleSelection(data)
    }

    override fun cancel() {
        completeSubject.onNext(CancelSelection)
        completeSubject.onComplete()
    }

    override fun complete() {
        completeSubject.onNext(ApplySelection)
        completeSubject.onComplete()
    }

    override fun updateSelection(selection: List<DATA>) {
        // TODO: 4/26/2021 https://online.sbis.ru/opendoc.html?guid=71062bfd-28b1-4e0f-b750-45ffb1582879
    }

    override fun onCleared() {
        super.onCleared()

        disposable.dispose()
    }

    /**
     * Завершение выбора. В результате [result] будет возвращён только элемент [data]
     */
    fun complete(data: DATA) {
        completeSubject.onNext(SetSelection(data))
        completeSubject.onComplete()
    }

    @WorkerThread
    private fun loadInitialSelection(): List<DATA> = selectionLoader
        .loadSelectedItems()
        .onEach { metaFactory.attachSelectedItemMeta(it as SelectorItemModel) }
}
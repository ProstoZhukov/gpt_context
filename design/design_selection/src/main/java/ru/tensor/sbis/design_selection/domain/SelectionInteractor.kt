package ru.tensor.sbis.design_selection.domain

import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import ru.tensor.sbis.communication_decl.selection.SelectionItemId
import ru.tensor.sbis.design_selection.contract.controller.SelectionDelegate
import ru.tensor.sbis.design_selection.contract.data.SelectionItem
import ru.tensor.sbis.design_selection.contract.customization.selected.panel.SelectedData

/**
 * Интерактор компонента выбора.
 *
 * @property selectionDelegate делегат операций выбора.
 * @property scheduler планировщик для выполнения операций выбора.
 * @property selectedItemsScheduler планировщик для загрузки выбранных.
 *
 * @author vv.chekurda
 */
internal class SelectionInteractor<ITEM : SelectionItem>(
    private val selectionDelegate: SelectionDelegate<ITEM>,
    private val scheduler: Scheduler = Schedulers.single(),
    private val selectedItemsScheduler: Scheduler = Schedulers.io()
) {
    fun select(id: SelectionItemId): Completable =
        Completable.fromAction { selectionDelegate.select(id) }
            .subscribeOn(scheduler)

    fun selectBySearch(id: SelectionItemId): Completable =
        Completable.fromAction { selectionDelegate.select(id, withNotify = false) }
            .subscribeOn(scheduler)

    fun complete(id: SelectionItemId): Single<List<ITEM>> =
        Single.fromCallable {
            selectionDelegate.singleComplete(id).let(::listOf)
        }.subscribeOn(selectedItemsScheduler)
            .observeOn(AndroidSchedulers.mainThread())

    fun replaceSelected(id: SelectionItemId): Completable =
        Completable.fromAction { selectionDelegate.replaceSelected(id) }
            .subscribeOn(scheduler)

    fun replaceSelectedBySearch(id: SelectionItemId): Completable =
        Completable.fromAction { selectionDelegate.replaceSelected(id, withNotify = false) }
            .subscribeOn(scheduler)

    fun unselect(id: SelectionItemId): Completable =
        Completable.fromAction { selectionDelegate.unselect(id) }
            .subscribeOn(scheduler)

    fun unselectAll(): Completable =
        Completable.fromAction {
            selectionDelegate.getAllSelectedItems().forEach {
                selectionDelegate.unselect(it.id)
            }
        }.subscribeOn(scheduler)

    fun getSelectedData(isUserSelection: Boolean): Single<SelectedData<ITEM>> =
        Single.fromCallable {
            val selectedItems = selectionDelegate.getSelectedItems()
            SelectedData(
                items = selectedItems,
                isUserSelection = isUserSelection,
                hasSelectedItems = selectionDelegate.hasSelectedItems()
            )
        }
            .subscribeOn(selectedItemsScheduler)
            .observeOn(AndroidSchedulers.mainThread())

    fun getAllSelectedItems(): Single<List<ITEM>> =
        Single.fromCallable { selectionDelegate.getAllSelectedItems() }
            .subscribeOn(selectedItemsScheduler)
            .observeOn(AndroidSchedulers.mainThread())

    fun subscribeOnFilterChanges(): Observable<Unit> {
        val subject = BehaviorSubject.create<Unit>()
        selectionDelegate.setOnFilterChangedCallback { subject.onNext(Unit) }
        return subject.subscribeOn(scheduler)
    }
}
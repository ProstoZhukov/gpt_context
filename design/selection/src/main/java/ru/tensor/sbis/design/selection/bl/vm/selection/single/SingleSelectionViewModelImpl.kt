package ru.tensor.sbis.design.selection.bl.vm.selection.single

import androidx.annotation.UiThread
import androidx.lifecycle.ViewModel
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import ru.tensor.sbis.design.selection.bl.contract.model.SelectorItem
import ru.tensor.sbis.design.selection.ui.contract.SingleSelectionLoader
import ru.tensor.sbis.design.selection.ui.factories.ItemMetaFactory
import ru.tensor.sbis.design.selection.ui.model.SelectorItemModel

/**
 * Реализация вьюмодели одиночного выбора
 *
 * @author us.bessonov
 */
internal class SingleSelectionViewModelImpl<DATA : SelectorItem>(
    selectionLoader: SingleSelectionLoader<DATA>,
    metaFactory: ItemMetaFactory,
    private val uiScheduler: Scheduler = AndroidSchedulers.mainThread(),
    ioScheduler: Scheduler = Schedulers.io()
) : ViewModel(), SingleSelectionViewModel<DATA> {

    private var selectionList = mutableListOf<DATA>()
    private val completeSubject = PublishSubject.create<Boolean>()
    private val selectionSubject = BehaviorSubject.createDefault(selectionList)

    private val disposable = CompositeDisposable()

    override val selection: Observable<DATA> = selectionSubject.skipWhile { it.isEmpty() }.map { it.first() }

    override val result: Maybe<DATA> = completeSubject.withLatestFrom(
        selectionSubject,
        BiFunction<Boolean, List<DATA>, List<DATA>> { isCancelled, selection ->
            check(isCancelled || selection.isNotEmpty()) {
                "Unable to complete selection: no item selected"
            }
            if (isCancelled) emptyList() else selection
        }
    )
        .flatMap { selection ->
            selection.firstOrNull()?.let { Observable.just(it) }
                ?: Observable.empty()
        }
        .firstElement()

    init {
        disposable.add(
            Maybe.fromCallable<DATA> {
                selectionLoader.loadSelectedItem()
                    ?.apply { metaFactory.attachSelectedItemMeta(this as SelectorItemModel) }
            }
                .subscribeOn(ioScheduler)
                .observeOn(uiScheduler)
                .subscribe(::onInitialSelectionLoaded)
        )
    }

    override fun cancel() {
        completeSubject.onNext(true)
        completeSubject.onComplete()
    }

    override fun complete(data: DATA) {
        setSelected(data)
        completeSubject.onNext(false)
        completeSubject.onComplete()
    }

    override fun updateSelection(selection: DATA) {
        disposable.add(
            Single.just(selection)
                .flatMapMaybe(::checkSelectionUpdateRequired)
                .observeOn(uiScheduler)
                .subscribe {
                    // только внутреннее обновление, без публикации в selection
                    selectionList[0] = it
                }
        )
    }

    override fun onCleared() {
        super.onCleared()
        disposable.dispose()
    }

    private fun onInitialSelectionLoaded(data: DATA) {
        if (selectionSubject.value.isNullOrEmpty()) {
            setSelected(data)
        }
    }

    @UiThread
    private fun setSelected(data: DATA) {
        val lastSelected = selectionSubject.value?.firstOrNull()
        lastSelected?.let {
            if (it == data) return
            it.meta.isSelected = false
        }
        data.meta.isSelected = true
        selectionList = mutableListOf(data)
        selectionSubject.onNext(selectionList)
    }

    private fun checkSelectionUpdateRequired(selection: DATA): Maybe<DATA> {
        val selected = selectionSubject.value?.firstOrNull()
        return when {
            // обновляем, только если что-то выбрано. Устанавливать выбор тут нельзя
            selected == null -> Maybe.empty()
            // элемент уже обновлён
            selected == selection -> Maybe.empty()
            selected.id == selection.id -> {
                selected.meta.invalidate()
                selection.meta.isSelected = true
                Maybe.just(selection)
            }
            else -> error("Unexpected item $selection")
        }
    }
}
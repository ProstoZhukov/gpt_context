package ru.tensor.sbis.crud3.data

import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import ru.tensor.sbis.crud3.ComponentViewModel
import ru.tensor.sbis.crud3.domain.ObserverCallback
import ru.tensor.sbis.service.generated.IndexPair
import ru.tensor.sbis.service.generated.StubType
import ru.tensor.sbis.service.generated.ViewPosition
import timber.log.Timber

/**
 * Колбек для CRUD3 коллекции контроллера. Превращает сигналы коллекции в реактивную последовательность объектов того
 * же смысла.
 * Описание аргументов см в [ComponentViewModel].
 */
internal class Crud3ObserverCallbackImpl<ITEM_WITH_INDEX, SOURCE_ITEM>(
    private val _subject: PublishSubject<CollectionEvent<SOURCE_ITEM, ITEM_WITH_INDEX>> = PublishSubject.create()
) :
    ObserverCallback<ITEM_WITH_INDEX, SOURCE_ITEM> {

    val events: Observable<CollectionEvent<SOURCE_ITEM, ITEM_WITH_INDEX>> = _subject.observeOn(Schedulers.single())

    override fun onReset(p0: List<SOURCE_ITEM>) {
        Timber.d("onReset: ${p0.size}")
        _subject.onNext(OnReset(p0))
    }

    override fun onRemove(p0: List<Long>) {
        Timber.d("onRemove: ${p0.size}")
        _subject.onNext(OnRemove(p0))
    }

    override fun onMove(p0: List<IndexPair>) {
        Timber.d("onMove: ${p0.size}")
        _subject.onNext(OnMove(p0.map { Pair(it.firstIndex, it.secondIndex) }))
    }

    override fun onAdd(p0: List<ITEM_WITH_INDEX>) {
        Timber.d("onAdd: ${p0.size}")
        _subject.onNext(OnAdd(p0))
    }

    override fun onReplace(p0: List<ITEM_WITH_INDEX>) {
        Timber.d("onReplace: ${p0.size}")
        _subject.onNext(OnReplace(p0))
    }

    override fun onAddThrobber(position: ViewPosition) {
        Timber.d("onAddThrobber:$position")
        _subject.onNext(OnAddThrobber(position))
    }

    override fun onRemoveThrobber() {
        Timber.d("onRemoveThrobber")
        _subject.onNext(OnRemoveThrobber())
    }

    override fun onAddStub(stubType: StubType, position: ViewPosition) {
        Timber.d("onAddStub:$stubType $position")
        _subject.onNext(OnAddStub(stubType, position))
    }

    override fun onRemoveStub() {
        Timber.d("onRemoveStub")
        _subject.onNext(OnRemoveStub())
    }
}
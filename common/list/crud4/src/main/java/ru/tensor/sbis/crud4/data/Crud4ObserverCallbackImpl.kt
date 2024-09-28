package ru.tensor.sbis.crud4.data

import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import ru.tensor.sbis.crud4.ComponentViewModel
import ru.tensor.sbis.crud4.domain.ObserverCallback
import ru.tensor.sbis.service.generated.DirectionStatus
import ru.tensor.sbis.service.generated.IndexPair
import ru.tensor.sbis.service.generated.Mark
import ru.tensor.sbis.service.generated.Selection
import ru.tensor.sbis.service.generated.SelectionCounter
import ru.tensor.sbis.service.generated.SelectionStatus
import ru.tensor.sbis.service.generated.StubType
import ru.tensor.sbis.service.generated.ViewPosition
import timber.log.Timber
import java.util.ArrayList

/**
 * Колбек для crud4 коллекции контроллера. Превращает сигналы коллекции в реактивную последовательность объектов того
 * же смысла.
 * Описание аргументов см в [ComponentViewModel].
 */
internal class Crud4ObserverCallbackImpl<ITEM_WITH_INDEX, SOURCE_ITEM, PATH_MODEL>(
    private val _subject: PublishSubject<CollectionEvent<SOURCE_ITEM, ITEM_WITH_INDEX, PATH_MODEL>> = PublishSubject.create()
) :
    ObserverCallback<ITEM_WITH_INDEX, SOURCE_ITEM, PATH_MODEL> {

    val events: Observable<CollectionEvent<SOURCE_ITEM, ITEM_WITH_INDEX, PATH_MODEL>> =
        _subject.observeOn(Schedulers.single())

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

    override fun onAddStub(stubType: StubType, position: ViewPosition, message: String?) {
        Timber.d("onAddStub:$stubType $position $message")
        _subject.onNext(OnAddStub(stubType, position, message))
    }

    override fun onRemoveStub() {
        Timber.d("onRemoveStub")
        _subject.onNext(OnRemoveStub())
    }

    override fun onPath(path: List<PATH_MODEL>) {
        _subject.onNext(OnPath(path))
    }

    override fun onEndUpdate(haveMore: DirectionStatus) {
        Timber.d("onEndUpdate: ${haveMore.backward} | ${haveMore.forward}")
        _subject.onNext(OnEndUpdate(haveMore))
    }

    override fun onMark(marked: Mark) {
        _subject.onNext(OnMark(marked))
    }

    override fun onSelect(selected: ArrayList<Selection>, counter: SelectionCounter) {
        _subject.onNext(OnSelect(selected, counter))
    }

    override fun onSelect(selected: ArrayList<Selection>) {
        val counter = SelectionCounter(null, SelectionStatus.UNSET)    ;
        _subject.onNext(OnSelect(selected, counter))
    }

    override fun onRestorePosition(pos: Long) {
        _subject.onNext(OnRestorePosition(pos))
    }
}
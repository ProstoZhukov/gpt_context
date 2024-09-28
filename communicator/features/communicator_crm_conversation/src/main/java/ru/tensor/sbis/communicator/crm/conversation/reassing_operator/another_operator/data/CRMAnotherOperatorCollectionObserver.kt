package ru.tensor.sbis.communicator.crm.conversation.reassing_operator.another_operator.data

import ru.tensor.sbis.consultations.generated.CollectionObserverOfOperatorViewModel
import ru.tensor.sbis.consultations.generated.ItemWithIndexOfOperatorViewModel
import ru.tensor.sbis.consultations.generated.OperatorViewModel
import ru.tensor.sbis.crud3.domain.ObserverCallback
import ru.tensor.sbis.service.generated.IndexPair
import ru.tensor.sbis.service.generated.StubType
import ru.tensor.sbis.service.generated.ViewPosition

/**
 * Реализция проксирует событие из колбека контроллера в колбек списочного компонента.
 * @param observer
 *
 * @author da.zhukov
 */
internal class CRMAnotherOperatorCollectionObserver(
    private val observer: ObserverCallback<ItemWithIndexOfOperatorViewModel, OperatorViewModel>
) : CollectionObserverOfOperatorViewModel() {

    override fun onAdd(param: ArrayList<ItemWithIndexOfOperatorViewModel>) {
        observer.onAdd(param)
    }

    override fun onAddStub(stubType: StubType, position: ViewPosition) {
        observer.onAddStub(stubType, position)
    }

    override fun onAddThrobber(position: ViewPosition) {
        observer.onAddThrobber(position)
    }

    override fun onMove(param: ArrayList<IndexPair>) {
        observer.onMove(param)
    }

    override fun onRemove(index: ArrayList<Long>) {
        observer.onRemove(index)
    }

    override fun onRemoveStub() {
        observer.onRemoveStub()
    }

    override fun onRemoveThrobber() {
        observer.onRemoveThrobber()
    }

    override fun onReplace(param: ArrayList<ItemWithIndexOfOperatorViewModel>) {
        observer.onReplace(param)
    }

    override fun onReset(items: ArrayList<OperatorViewModel>) {
        observer.onReset(items)
    }

    override fun onBeginUpdate() = Unit

    override fun onEndUpdate() = Unit
}
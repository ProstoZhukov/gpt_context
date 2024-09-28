package ru.tensor.sbis.communicator.communicator_crm_chat_list.filter.connections.data

import ru.tensor.sbis.consultations.generated.ChannelListViewModel
import ru.tensor.sbis.consultations.generated.CollectionObserverOfChannelListViewModel
import ru.tensor.sbis.consultations.generated.ItemWithIndexOfChannelListViewModel
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
internal class CRMConnectionListCollectionObserver(
    private val observer: ObserverCallback<ItemWithIndexOfChannelListViewModel, ChannelListViewModel>
) : CollectionObserverOfChannelListViewModel() {

    override fun onAdd(param: ArrayList<ItemWithIndexOfChannelListViewModel>) {
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

    override fun onReplace(param: ArrayList<ItemWithIndexOfChannelListViewModel>) {
        observer.onReplace(param)
    }

    override fun onReset(items: ArrayList<ChannelListViewModel>) {
        observer.onReset(items)
    }

    override fun onBeginUpdate() = Unit

    override fun onEndUpdate() = Unit
}
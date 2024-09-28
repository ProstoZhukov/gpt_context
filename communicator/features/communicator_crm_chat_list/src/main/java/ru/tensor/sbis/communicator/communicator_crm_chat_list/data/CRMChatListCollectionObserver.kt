package ru.tensor.sbis.communicator.communicator_crm_chat_list.data

import ru.tensor.sbis.consultations.generated.CollectionObserverOfConsultationListElementModel
import ru.tensor.sbis.consultations.generated.ConsultationListElementModel
import ru.tensor.sbis.consultations.generated.ItemWithIndexOfConsultationListElementModel
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
internal class CRMChatListCollectionObserver(
    private val observer: ObserverCallback<ItemWithIndexOfConsultationListElementModel, ConsultationListElementModel>
) : CollectionObserverOfConsultationListElementModel() {

    @get:Synchronized
    private var isCancelled: Boolean = false

    fun cancel() {
        isCancelled = true
    }

    private fun safeCancelled(action: () -> Unit) {
        if (!isCancelled) action()
    }

    override fun onAdd(param: ArrayList<ItemWithIndexOfConsultationListElementModel>) {
        safeCancelled { observer.onAdd(param) }
    }

    override fun onAddStub(stubType: StubType, position: ViewPosition) {
        safeCancelled { observer.onAddStub(stubType, position) }
    }

    override fun onAddThrobber(position: ViewPosition) {
        safeCancelled { observer.onAddThrobber(position) }
    }

    override fun onMove(param: ArrayList<IndexPair>) {
        safeCancelled { observer.onMove(param) }
    }

    override fun onRemove(index: ArrayList<Long>) {
        safeCancelled { observer.onRemove(index) }
    }

    override fun onRemoveStub() {
        safeCancelled { observer.onRemoveStub() }
    }

    override fun onRemoveThrobber() {
        safeCancelled { observer.onRemoveThrobber() }
    }

    override fun onReplace(param: ArrayList<ItemWithIndexOfConsultationListElementModel>) {
        safeCancelled { observer.onReplace(param) }
    }

    override fun onReset(items: ArrayList<ConsultationListElementModel>) {
        safeCancelled { observer.onReset(items) }
    }

    override fun onBeginUpdate() = Unit

    override fun onEndUpdate() = Unit
}
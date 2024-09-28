package ru.tensor.sbis.communicator_support_consultation_list.data

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
 */
internal class SupportConsultationsListCollectionObserver constructor(
    private val observer: ObserverCallback<ItemWithIndexOfConsultationListElementModel, ConsultationListElementModel>
) :
    CollectionObserverOfConsultationListElementModel() {

    override fun onAdd(param: ArrayList<ItemWithIndexOfConsultationListElementModel>) {
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

    override fun onReplace(param: ArrayList<ItemWithIndexOfConsultationListElementModel>) {
        observer.onReplace(param)
    }

    override fun onReset(items: ArrayList<ConsultationListElementModel>) {
        observer.onReset(items)
    }

    override fun onBeginUpdate() = Unit

    override fun onEndUpdate() = Unit


}
package ru.tensor.sbis.communicator.crm.conversation.presentation.ui.quick_reply.data

import ru.tensor.sbis.consultations.generated.CollectionObserverOfQuickReplyViewModel
import ru.tensor.sbis.consultations.generated.ItemWithIndexOfQuickReplyViewModel
import ru.tensor.sbis.consultations.generated.QuickReplyViewModel
import ru.tensor.sbis.crud3.domain.ObserverCallback
import ru.tensor.sbis.service.generated.IndexPair
import ru.tensor.sbis.service.generated.StubType
import ru.tensor.sbis.service.generated.ViewPosition

/**
 * Реализция проксирует событие из колбека контроллера в колбек списочного компонента.
 * @param observer
 *
 * @author dv.baranov
 */
internal class QuickReplyCollectionObserver constructor(
    private val observer: ObserverCallback<ItemWithIndexOfQuickReplyViewModel, QuickReplyViewModel>,
) : CollectionObserverOfQuickReplyViewModel() {

    override fun onAdd(param: ArrayList<ItemWithIndexOfQuickReplyViewModel>) {
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

    override fun onReplace(param: ArrayList<ItemWithIndexOfQuickReplyViewModel>) {
        observer.onReplace(param)
    }

    override fun onReset(items: ArrayList<QuickReplyViewModel>) {
        observer.onReset(items)
    }

    override fun onBeginUpdate() = Unit

    override fun onEndUpdate() = Unit
}

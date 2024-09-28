package ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.ui.links_list.data

import ru.tensor.sbis.communicator.generated.CollectionObserverOfLinkViewModel
import ru.tensor.sbis.communicator.generated.ItemWithIndexOfLinkViewModel
import ru.tensor.sbis.communicator.generated.LinkViewModel
import ru.tensor.sbis.crud3.domain.ObserverCallback
import ru.tensor.sbis.service.generated.IndexPair
import ru.tensor.sbis.service.generated.StubType
import ru.tensor.sbis.service.generated.ViewPosition

/**
 * Реализация проксирует событие из колбека контроллера в колбек списочного компонента.
 * @param observer
 *
 * @author dv.baranov
 */
internal class ConversationLinksListCollectionObserver constructor(
    private val observer: ObserverCallback<ItemWithIndexOfLinkViewModel, LinkViewModel>,
) : CollectionObserverOfLinkViewModel() {

    override fun onAdd(param: ArrayList<ItemWithIndexOfLinkViewModel>) {
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

    override fun onReplace(param: ArrayList<ItemWithIndexOfLinkViewModel>) {
        observer.onReplace(param)
    }

    override fun onReset(items: ArrayList<LinkViewModel>) {
        observer.onReset(items)
    }

    override fun onBeginUpdate() = Unit

    override fun onEndUpdate() = Unit
}
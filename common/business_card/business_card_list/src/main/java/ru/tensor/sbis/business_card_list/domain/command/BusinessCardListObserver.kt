package ru.tensor.sbis.business_card_list.domain.command

import ru.tensor.business.card.mobile.generated.CollectionObserverOfBusinessCard
import ru.tensor.business.card.mobile.generated.ItemWithIndexOfBusinessCard
import ru.tensor.sbis.business_card_host_decl.ui.model.BusinessCard
import ru.tensor.sbis.crud3.domain.ObserverCallback
import ru.tensor.sbis.service.generated.IndexPair
import ru.tensor.sbis.service.generated.StubType
import ru.tensor.sbis.service.generated.ViewPosition
import ru.tensor.business.card.mobile.generated.BusinessCard as ControllerBusinessCard

/**
 * Реализция проксирует событие из колбека контроллера в колбек списочного компонента
 */
internal class BusinessCardListObserver(
    private val callback: ObserverCallback<IndexedBusinessCardItem, BusinessCard>,
    private val mapper: (ControllerBusinessCard) -> BusinessCard,
    private val indexedMapper: (ItemWithIndexOfBusinessCard) -> IndexedBusinessCardItem
) : CollectionObserverOfBusinessCard() {

    override fun onReset(items: ArrayList<ControllerBusinessCard>) {
        callback.onReset(items.map(mapper))
    }

    override fun onMove(param: ArrayList<IndexPair>) {
        callback.onMove(param)
    }

    override fun onRemove(index: ArrayList<Long>) {
        callback.onRemove(index)
    }

    override fun onAdd(param: ArrayList<ItemWithIndexOfBusinessCard>) {
        callback.onAdd(param.map(indexedMapper))
    }

    override fun onAddStub(stubType: StubType, position: ViewPosition) {
        callback.onAddStub(stubType, position)
    }

    override fun onRemoveStub() {
        callback.onRemoveStub()
    }

    override fun onAddThrobber(position: ViewPosition) {
        callback.onAddThrobber(position)
    }

    override fun onRemoveThrobber() {
        callback.onRemoveThrobber()
    }

    override fun onReplace(param: ArrayList<ItemWithIndexOfBusinessCard>) {
        callback.onReplace(param.map(indexedMapper))
    }

    override fun onBeginUpdate() = Unit

    override fun onEndUpdate() = Unit
}
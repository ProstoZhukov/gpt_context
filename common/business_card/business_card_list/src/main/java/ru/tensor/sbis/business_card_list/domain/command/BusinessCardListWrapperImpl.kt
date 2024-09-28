package ru.tensor.sbis.business_card_list.domain.command

import ru.tensor.business.card.mobile.generated.BusinessCardCollectionProvider
import ru.tensor.business.card.mobile.generated.BusinessCardFilter
import ru.tensor.business.card.mobile.generated.CollectionOfBusinessCard
import ru.tensor.business.card.mobile.generated.ItemWithIndexOfBusinessCard
import ru.tensor.business.card.mobile.generated.PaginationOfBusinessCardAnchor
import ru.tensor.sbis.business_card_host_decl.ui.model.BusinessCard
import ru.tensor.sbis.common.data.DependencyProvider
import ru.tensor.sbis.crud3.domain.ObserverCallback
import ru.tensor.sbis.crud3.domain.Wrapper
import ru.tensor.sbis.service.generated.DirectionType
import ru.tensor.sbis.service.generated.DirectionType.FORWARD
import ru.tensor.business.card.mobile.generated.BusinessCard as ControllerBusinessCard


/** @SelfDocumented */
internal class BusinessCardListWrapperImpl(
    private val businessCardCollectionProvider: DependencyProvider<BusinessCardCollectionProvider>,
    private val mapper: (ControllerBusinessCard) -> BusinessCard,
    private val indexedMapper: (ItemWithIndexOfBusinessCard) -> IndexedBusinessCardItem,
    private val filter: BusinessCardListFilter,
) : BusinessCardListWrapper {

    override fun createEmptyFilter(): BusinessCardListFilter = filter

    override fun createPaginationAnchor(itemsOnPage: Long, directionType: DirectionType): PaginationOfBusinessCardAnchor =
        PaginationOfBusinessCardAnchor(null, FORWARD, itemsOnPage)

    override fun createCollection(filter: BusinessCardListFilter, anchor: PaginationOfBusinessCardAnchor): CollectionOfBusinessCard =
        businessCardCollectionProvider.get().get((filter.map()), anchor)

    override fun createCollectionObserver(observer: ObserverCallback<IndexedBusinessCardItem, BusinessCard>): BusinessCardListObserver =
        BusinessCardListObserver(observer, mapper, indexedMapper)

    override fun setObserver(observer: BusinessCardListObserver, toCollection: CollectionOfBusinessCard) {
        toCollection.setObserver(observer)
    }

    override fun goNext(collection: CollectionOfBusinessCard, var1: Long) {
        collection.next(var1)
    }

    override fun goPrev(collection: CollectionOfBusinessCard, var1: Long) {
        collection.prev(var1)
    }

    override fun refresh(collection: CollectionOfBusinessCard) {
        collection.refresh()
    }

    override fun dispose(collection: CollectionOfBusinessCard) {
        collection.dispose()
    }

    override fun getItem(itemWithIndex: IndexedBusinessCardItem): BusinessCard = itemWithIndex.value

    override fun getIndex(itemWithIndex: IndexedBusinessCardItem): Long = itemWithIndex.index.toLong()
}

/**@SelfDocumented*/
internal typealias BusinessCardListWrapper =
    Wrapper<CollectionOfBusinessCard,
        BusinessCardListObserver,
        BusinessCardListFilter,
        PaginationOfBusinessCardAnchor,
        IndexedBusinessCardItem,
        BusinessCard>

/**@SelfDocumented*/
internal typealias IndexedBusinessCardItem = IndexedValue<BusinessCard>
package ru.tensor.sbis.communicator.communicator_crm_chat_list.filter.connections.data

import ru.tensor.sbis.common.data.DependencyProvider
import ru.tensor.sbis.consultations.generated.ChannelListViewModel
import ru.tensor.sbis.consultations.generated.CollectionOfChannelListViewModel
import ru.tensor.sbis.consultations.generated.ConnectionCollectionProvider
import ru.tensor.sbis.consultations.generated.ConnectionFilter
import ru.tensor.sbis.consultations.generated.ItemWithIndexOfChannelListViewModel
import ru.tensor.sbis.consultations.generated.PaginationOfConnectionAnchor
import ru.tensor.sbis.crud3.domain.ObserverCallback
import ru.tensor.sbis.service.generated.DirectionType

/**
 * Реализация CRMConnectionListCollectionWrapper.
 *
 * @author da.zhukov
 */
internal class CRMConnectionListCollectionWrapperImpl(
    private val connectionCollectionProvider: DependencyProvider<ConnectionCollectionProvider>,
    private val filterProvider: () -> ConnectionFilter
) : CRMConnectionListCollectionWrapper {

    override fun createEmptyFilter(): ConnectionFilter =
        filterProvider()

    override fun createPaginationAnchor(itemsOnPage: Long, directionType: DirectionType): PaginationOfConnectionAnchor {
        return PaginationOfConnectionAnchor(null, DirectionType.FORWARD, itemsOnPage)
    }

    override fun createCollection(
        filter: ConnectionFilter,
        anchor: PaginationOfConnectionAnchor
    ): CollectionOfChannelListViewModel {
        return connectionCollectionProvider.get().get(
            filter,
            anchor
        )
    }

    override fun createCollectionObserver(observer: ObserverCallback<ItemWithIndexOfChannelListViewModel, ChannelListViewModel>): CRMConnectionListCollectionObserver {
        return CRMConnectionListCollectionObserver(observer)
    }

    override fun setObserver(
        observer: CRMConnectionListCollectionObserver,
        toCollection: CollectionOfChannelListViewModel
    ) {
        toCollection.setObserver(observer)
    }

    override fun goNext(collection: CollectionOfChannelListViewModel, var1: Long) {
        collection.next(var1)
    }

    override fun goPrev(collection: CollectionOfChannelListViewModel, var1: Long) {
        collection.prev(var1)
    }

    override fun refresh(collection: CollectionOfChannelListViewModel) {
        collection.refresh()
    }

    override fun dispose(collection: CollectionOfChannelListViewModel) {
        collection.dispose()
    }

    override fun getIndex(itemWithIndex: ItemWithIndexOfChannelListViewModel): Long =
        itemWithIndex.index

    override fun getItem(itemWithIndex: ItemWithIndexOfChannelListViewModel): ChannelListViewModel =
        itemWithIndex.item
}
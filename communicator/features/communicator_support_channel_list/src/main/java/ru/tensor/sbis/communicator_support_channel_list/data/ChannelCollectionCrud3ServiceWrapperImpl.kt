package ru.tensor.sbis.communicator_support_channel_list.data

import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import ru.tensor.sbis.consultations.generated.CollectionOfSupportChatsViewModel
import ru.tensor.sbis.consultations.generated.ItemWithIndexOfSupportChatsViewModel
import ru.tensor.sbis.consultations.generated.PaginationOfSupportChatsListAnchor
import ru.tensor.sbis.consultations.generated.SupportChatsCollectionProvider
import ru.tensor.sbis.consultations.generated.SupportChatsListFilter
import ru.tensor.sbis.consultations.generated.SupportChatsType
import ru.tensor.sbis.consultations.generated.SupportChatsViewModel
import ru.tensor.sbis.crud3.domain.ObserverCallback
import ru.tensor.sbis.service.generated.DirectionType
import ru.tensor.sbis.service.generated.StubType


@AssistedFactory
internal interface ChannelCollectionCrud3ServiceWrapperImplFactory {
    fun create(
        supportChatsType: SupportChatsType,
        onStubListener: (stubType: StubType?) -> Unit
    ): ChannelCollectionCrud3ServiceWrapperImpl
}

/**
 * Реализация ChannelCollectionCrud3ServiceWrapper
 */
internal class ChannelCollectionCrud3ServiceWrapperImpl @AssistedInject constructor(
    @Assisted private val supportChatsType: SupportChatsType,
    @Assisted private val onStubListener: (stubType: StubType?) -> Unit,
    private val collectionProvider: SupportChatsCollectionProvider,
    private val supportChatsListCollectionObserverFactory: SupportChatsListCollectionObserverFactory
) :
    ChannelCollectionCrud3ServiceWrapper {

    override fun createEmptyFilter(): SupportChatsListFilter {
        return SupportChatsListFilter(supportChatsType)
    }

    override fun createPaginationAnchor(
        itemsOnPage: Long,
        directionType: DirectionType
    ): PaginationOfSupportChatsListAnchor {
        return PaginationOfSupportChatsListAnchor().apply {
            pageSize = itemsOnPage
        }
    }

    override fun createCollection(
        filter: SupportChatsListFilter,
        anchor: PaginationOfSupportChatsListAnchor
    ): CollectionOfSupportChatsViewModel {
        return collectionProvider.get(
            filter,
            anchor
        )
    }

    override fun createCollectionObserver(observer: ObserverCallback<ItemWithIndexOfSupportChatsViewModel, SupportChatsViewModel>): SupportChannelsListCollectionObserver {
        return supportChatsListCollectionObserverFactory.create(observer, onStubListener)
    }

    override fun setObserver(
        observer: SupportChannelsListCollectionObserver,
        toCollection: CollectionOfSupportChatsViewModel
    ) {
        toCollection.setObserver(observer)
    }

    override fun goNext(collection: CollectionOfSupportChatsViewModel, var1: Long) {
        collection.next(var1)
    }

    override fun goPrev(collection: CollectionOfSupportChatsViewModel, var1: Long) {
        collection.prev(var1)
    }

    override fun refresh(collection: CollectionOfSupportChatsViewModel) {
        collection.refresh()
    }

    override fun dispose(collection: CollectionOfSupportChatsViewModel) {
        collection.dispose()
    }

    override fun getIndex(itemWithIndex: ItemWithIndexOfSupportChatsViewModel): Long = itemWithIndex.index

    override fun getItem(itemWithIndex: ItemWithIndexOfSupportChatsViewModel): SupportChatsViewModel =
        itemWithIndex.item
}
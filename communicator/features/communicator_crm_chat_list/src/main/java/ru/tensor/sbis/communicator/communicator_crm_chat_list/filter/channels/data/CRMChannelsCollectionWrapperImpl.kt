package ru.tensor.sbis.communicator.communicator_crm_chat_list.filter.channels.data

import ru.tensor.sbis.common.data.DependencyProvider
import ru.tensor.sbis.consultations.generated.ChannelHierarchyCollectionFilter
import ru.tensor.sbis.consultations.generated.ChannelHierarchyCollectionProvider
import ru.tensor.sbis.consultations.generated.ChannelHierarchyViewModel
import ru.tensor.sbis.consultations.generated.CollectionOfChannelHierarchyViewModel
import ru.tensor.sbis.consultations.generated.ItemWithIndexOfChannelHierarchyViewModel
import ru.tensor.sbis.consultations.generated.PaginationOfChannelFolderAnchor
import ru.tensor.sbis.crud3.domain.ObserverCallback
import ru.tensor.sbis.service.generated.DirectionType

/**
 * Реализация CRMChannelsCollectionWrapper.
 *
 * @author da.zhukov
 */
internal class CRMChannelsCollectionWrapperImpl(
    private val channelHierarchyCollectionProvider: DependencyProvider<ChannelHierarchyCollectionProvider>,
    private val filterProvider: () -> ChannelHierarchyCollectionFilter
) : CRMChannelsCollectionWrapper {

    override fun createEmptyFilter(): ChannelHierarchyCollectionFilter =
        filterProvider()

    override fun createPaginationAnchor(
        itemsOnPage: Long,
        directionType: DirectionType
    ): PaginationOfChannelFolderAnchor {
        return PaginationOfChannelFolderAnchor(
            null,
            DirectionType.FORWARD,
            itemsOnPage
        )
    }

    override fun createCollection(
        filter: ChannelHierarchyCollectionFilter,
        anchor: PaginationOfChannelFolderAnchor
    ): CollectionOfChannelHierarchyViewModel {
        return channelHierarchyCollectionProvider.get().get(
            filter,
            anchor
        )
    }

    override fun createCollectionObserver(
        observer: ObserverCallback<ItemWithIndexOfChannelHierarchyViewModel, ChannelHierarchyViewModel>
    ): CRMChannelsCollectionObserver {
        return CRMChannelsCollectionObserver(observer)
    }

    override fun setObserver(
        observer: CRMChannelsCollectionObserver,
        toCollection: CollectionOfChannelHierarchyViewModel
    ) {
        toCollection.setObserver(observer)
    }

    override fun goNext(collection: CollectionOfChannelHierarchyViewModel, var1: Long) {
        collection.next(var1)
    }

    override fun goPrev(collection: CollectionOfChannelHierarchyViewModel, var1: Long) {
        collection.prev(var1)
    }

    override fun refresh(collection: CollectionOfChannelHierarchyViewModel) {
        collection.refresh()
    }

    override fun dispose(collection: CollectionOfChannelHierarchyViewModel) {
        collection.dispose()
    }

    override fun getIndex(itemWithIndex: ItemWithIndexOfChannelHierarchyViewModel): Long =
        itemWithIndex.index

    override fun getItem(itemWithIndex: ItemWithIndexOfChannelHierarchyViewModel): ChannelHierarchyViewModel =
        itemWithIndex.item
}
package ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.ui.links_list.data

import ru.tensor.sbis.common.data.DependencyProvider
import ru.tensor.sbis.communicator.generated.CollectionOfLinkViewModel
import ru.tensor.sbis.communicator.generated.ItemWithIndexOfLinkViewModel
import ru.tensor.sbis.communicator.generated.LinkCollectionProvider
import ru.tensor.sbis.communicator.generated.LinkFilter
import ru.tensor.sbis.communicator.generated.LinkViewModel
import ru.tensor.sbis.communicator.generated.PaginationOfLinkAnchor
import ru.tensor.sbis.crud3.domain.ObserverCallback
import ru.tensor.sbis.service.generated.DirectionType

/**
 * Реализация ConversationLinksListCollectionWrapper.
 *
 * @author dv.baranov
 */
internal class ConversationLinksListCollectionWrapperImpl(
    private val linkCollectionProvider: DependencyProvider<LinkCollectionProvider>,
    private val filterProvider: () -> LinkFilter,
) : ConversationLinksListCollectionWrapper {

    override fun createEmptyFilter(): LinkFilter =
        filterProvider()

    override fun createPaginationAnchor(
        itemsOnPage: Long,
        directionType: DirectionType,
    ): PaginationOfLinkAnchor {
        return PaginationOfLinkAnchor(null, DirectionType.FORWARD, itemsOnPage)
    }

    override fun createCollection(
        filter: LinkFilter,
        anchor: PaginationOfLinkAnchor,
    ): CollectionOfLinkViewModel {
        return linkCollectionProvider.get().get(
            filter,
            anchor,
        )
    }

    override fun createCollectionObserver(observer: ObserverCallback<ItemWithIndexOfLinkViewModel, LinkViewModel>): ConversationLinksListCollectionObserver {
        return ConversationLinksListCollectionObserver(observer)
    }

    override fun setObserver(
        observer: ConversationLinksListCollectionObserver,
        toCollection: CollectionOfLinkViewModel,
    ) {
        toCollection.setObserver(observer)
    }

    override fun goNext(collection: CollectionOfLinkViewModel, var1: Long) {
        collection.next(var1)
    }

    override fun goPrev(collection: CollectionOfLinkViewModel, var1: Long) {
        collection.prev(var1)
    }

    override fun refresh(collection: CollectionOfLinkViewModel) {
        collection.refresh()
    }

    override fun dispose(collection: CollectionOfLinkViewModel) {
        collection.dispose()
    }

    override fun getIndex(itemWithIndex: ItemWithIndexOfLinkViewModel): Long =
        itemWithIndex.index

    override fun getItem(itemWithIndex: ItemWithIndexOfLinkViewModel): LinkViewModel =
        itemWithIndex.item
}

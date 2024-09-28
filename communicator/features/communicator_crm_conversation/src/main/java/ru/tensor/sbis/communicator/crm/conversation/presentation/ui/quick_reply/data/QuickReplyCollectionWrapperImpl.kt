package ru.tensor.sbis.communicator.crm.conversation.presentation.ui.quick_reply.data

import ru.tensor.sbis.common.data.DependencyProvider
import ru.tensor.sbis.consultations.generated.CollectionOfQuickReplyViewModel
import ru.tensor.sbis.consultations.generated.ItemWithIndexOfQuickReplyViewModel
import ru.tensor.sbis.consultations.generated.PaginationOfQuickReplyAnchor
import ru.tensor.sbis.consultations.generated.QuickReplyCollectionProvider
import ru.tensor.sbis.consultations.generated.QuickReplyFilter
import ru.tensor.sbis.consultations.generated.QuickReplyViewModel
import ru.tensor.sbis.crud3.domain.ObserverCallback
import ru.tensor.sbis.service.generated.DirectionType

/**
 * Реализация QuickReplyCollectionWrapper.
 *
 * @author dv.baranov
 */
internal class QuickReplyCollectionWrapperImpl(
    private val quickReplyCollectionProvider: DependencyProvider<QuickReplyCollectionProvider>,
    private val filterProvider: () -> QuickReplyFilter,
) : QuickReplyCollectionWrapper {

    override fun createEmptyFilter(): QuickReplyFilter =
        filterProvider()

    override fun createPaginationAnchor(
        itemsOnPage: Long,
        directionType: DirectionType,
    ): PaginationOfQuickReplyAnchor {
        return PaginationOfQuickReplyAnchor(null, DirectionType.FORWARD, itemsOnPage)
    }

    override fun createCollection(
        filter: QuickReplyFilter,
        anchor: PaginationOfQuickReplyAnchor,
    ): CollectionOfQuickReplyViewModel {
        return quickReplyCollectionProvider.get().get(
            filter,
            anchor,
        )
    }

    override fun createCollectionObserver(observer: ObserverCallback<ItemWithIndexOfQuickReplyViewModel, QuickReplyViewModel>): QuickReplyCollectionObserver {
        return QuickReplyCollectionObserver(observer)
    }

    override fun setObserver(
        observer: QuickReplyCollectionObserver,
        toCollection: CollectionOfQuickReplyViewModel,
    ) {
        toCollection.setObserver(observer)
    }

    override fun goNext(collection: CollectionOfQuickReplyViewModel, var1: Long) {
        collection.next(var1)
    }

    override fun goPrev(collection: CollectionOfQuickReplyViewModel, var1: Long) {
        collection.prev(var1)
    }

    override fun refresh(collection: CollectionOfQuickReplyViewModel) {
        collection.refresh()
    }

    override fun dispose(collection: CollectionOfQuickReplyViewModel) {
        collection.dispose()
    }

    override fun getIndex(itemWithIndex: ItemWithIndexOfQuickReplyViewModel): Long =
        itemWithIndex.index

    override fun getItem(itemWithIndex: ItemWithIndexOfQuickReplyViewModel): QuickReplyViewModel =
        itemWithIndex.item
}

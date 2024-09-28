package ru.tensor.sbis.communicator.communicator_files.data

import ru.tensor.sbis.common.data.DependencyProvider
import ru.tensor.sbis.communicator.generated.CollectionOfThemeAttachmentViewModel
import ru.tensor.sbis.communicator.generated.ItemWithIndexOfThemeAttachmentViewModel
import ru.tensor.sbis.communicator.generated.PaginationOfThemeAttachmentAnchor
import ru.tensor.sbis.communicator.generated.ThemeAttachmentCollectionProvider
import ru.tensor.sbis.communicator.generated.ThemeAttachmentFilter
import ru.tensor.sbis.communicator.generated.ThemeAttachmentViewModel
import ru.tensor.sbis.crud3.domain.ObserverCallback
import ru.tensor.sbis.service.generated.DirectionType

/**
 * Реализация CRMChannelsCollectionWrapper файлов переписки.
 *
 * @author da.zhukov
 */
internal class CommunicatorFilesCollectionWrapperImpl(
    private val collectionProvider: DependencyProvider<ThemeAttachmentCollectionProvider>,
    private val filterProvider: () -> ThemeAttachmentFilter
) : CommunicatorFilesWrapper {

    override fun createEmptyFilter(): ThemeAttachmentFilter =
        filterProvider()

    override fun createPaginationAnchor(
        itemsOnPage: Long,
        directionType: DirectionType
    ): PaginationOfThemeAttachmentAnchor {
        return PaginationOfThemeAttachmentAnchor(
            null,
            DirectionType.FORWARD,
            itemsOnPage
        )
    }

    override fun createCollection(
        filter: ThemeAttachmentFilter,
        anchor: PaginationOfThemeAttachmentAnchor
    ): CollectionOfThemeAttachmentViewModel {
        return collectionProvider.get().get(
            filter,
            anchor
        )
    }

    override fun createCollectionObserver(
        observer: ObserverCallback<ItemWithIndexOfThemeAttachmentViewModel, ThemeAttachmentViewModel>
    ): CommunicatorFilesObserver {
        return CommunicatorFilesObserver(observer)
    }

    override fun setObserver(
        observer: CommunicatorFilesObserver,
        toCollection: CollectionOfThemeAttachmentViewModel
    ) {
        toCollection.setObserver(observer)
    }

    override fun goNext(collection: CollectionOfThemeAttachmentViewModel, var1: Long) {
        collection.next(var1)
    }

    override fun goPrev(collection: CollectionOfThemeAttachmentViewModel, var1: Long) {
        collection.prev(var1)
    }

    override fun refresh(collection: CollectionOfThemeAttachmentViewModel) {
        collection.refresh()
    }

    override fun dispose(collection: CollectionOfThemeAttachmentViewModel) {
        collection.dispose()
    }

    override fun getIndex(itemWithIndex: ItemWithIndexOfThemeAttachmentViewModel): Long =
        itemWithIndex.index

    override fun getItem(itemWithIndex: ItemWithIndexOfThemeAttachmentViewModel): ThemeAttachmentViewModel =
        itemWithIndex.item
}
package ru.tensor.sbis.logging.log_packages.data

import ru.tensor.sbis.crud3.domain.ObserverCallback
import ru.tensor.sbis.crud3.domain.Wrapper
import ru.tensor.sbis.platform.logdelivery.generated.CollectionOfLogPackageViewModel
import ru.tensor.sbis.platform.logdelivery.generated.ItemWithIndexOfLogPackageViewModel
import ru.tensor.sbis.platform.logdelivery.generated.LogCollectionProvider
import ru.tensor.sbis.platform.logdelivery.generated.LogFilter
import ru.tensor.sbis.platform.logdelivery.generated.LogPackageViewModel
import ru.tensor.sbis.platform.logdelivery.generated.PaginationOfLogAnchor
import ru.tensor.sbis.service.generated.DirectionType


/**
 * Реализация обертки для микросервиса контроллера collection.
 *
 * @author av.krymov
 */
class Crud3LogServiceWrapper(private val collectionProvider: LogCollectionProvider) :
    Wrapper<CollectionOfLogPackageViewModel, Crud3CollectionObserver, LogFilter, PaginationOfLogAnchor,
        ItemWithIndexOfLogPackageViewModel, LogPackageViewModel> {

    override fun createEmptyFilter() = LogFilter()

    override fun createPaginationAnchor(itemsOnPage: Long, directionType: DirectionType) =
        PaginationOfLogAnchor(
            null,
            directionType,
            itemsOnPage
        )

    override fun createCollection(
        filter: LogFilter,
        anchor: PaginationOfLogAnchor
    ): CollectionOfLogPackageViewModel {
        return collectionProvider.get(
            filter,
            anchor
        )
    }

    override fun createCollectionObserver(
        observer: ObserverCallback<ItemWithIndexOfLogPackageViewModel, LogPackageViewModel>
    ) = Crud3CollectionObserver(observer)

    override fun setObserver(
        observer: Crud3CollectionObserver,
        toCollection: CollectionOfLogPackageViewModel
    ) = toCollection.setObserver(observer)

    override fun goNext(collection: CollectionOfLogPackageViewModel, var1: Long) {
        collection.next(var1)
    }

    override fun goPrev(collection: CollectionOfLogPackageViewModel, var1: Long) {
        collection.prev(var1)
    }

    override fun refresh(collection: CollectionOfLogPackageViewModel) {
        collection.refresh()
    }

    override fun dispose(collection: CollectionOfLogPackageViewModel) {
        collection.dispose()
    }

    override fun getIndex(itemWithIndex: ItemWithIndexOfLogPackageViewModel) = itemWithIndex.index

    override fun getItem(itemWithIndex: ItemWithIndexOfLogPackageViewModel) = itemWithIndex.item
}
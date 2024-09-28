package ru.tensor.sbis.app_file_browser.data

import ru.tensor.sbis.crud3.domain.ObserverCallback
import ru.tensor.sbis.crud3.domain.Wrapper
import ru.tensor.sbis.mfb.generated.CollectionOfFileInfo
import ru.tensor.sbis.mfb.generated.FileInfo
import ru.tensor.sbis.mfb.generated.Filter
import ru.tensor.sbis.mfb.generated.ItemWithIndexOfFileInfo
import ru.tensor.sbis.mfb.generated.MobileFileController
import ru.tensor.sbis.mfb.generated.PaginationOfAnchor
import ru.tensor.sbis.service.generated.DirectionType

/**
 * Обёртка над микросервисом браузера файлов приложения.
 *
 * @author us.bessonov
 */
internal class Crud3MobileFileControllerWrapper(
    private val controller: MobileFileController
) : Wrapper<CollectionOfFileInfo, Crud3CollectionObserver, Filter, PaginationOfAnchor, ItemWithIndexOfFileInfo, FileInfo> {

    override fun createEmptyFilter() = Filter()

    override fun createPaginationAnchor(itemsOnPage: Long, directionType: DirectionType) =
        PaginationOfAnchor(null, directionType, itemsOnPage)

    override fun createCollection(filter: Filter, anchor: PaginationOfAnchor): CollectionOfFileInfo =
        controller.get(filter, anchor)

    override fun createCollectionObserver(observer: ObserverCallback<ItemWithIndexOfFileInfo, FileInfo>) =
        Crud3CollectionObserver(observer)

    override fun setObserver(observer: Crud3CollectionObserver, toCollection: CollectionOfFileInfo) =
        toCollection.setObserver(observer)

    override fun goNext(collection: CollectionOfFileInfo, var1: Long) = collection.next(var1)

    override fun goPrev(collection: CollectionOfFileInfo, var1: Long) = collection.prev(var1)

    override fun refresh(collection: CollectionOfFileInfo) = collection.refresh()

    override fun dispose(collection: CollectionOfFileInfo) = collection.dispose()

    override fun getIndex(itemWithIndex: ItemWithIndexOfFileInfo) = itemWithIndex.index

    override fun getItem(itemWithIndex: ItemWithIndexOfFileInfo) = itemWithIndex.item
}
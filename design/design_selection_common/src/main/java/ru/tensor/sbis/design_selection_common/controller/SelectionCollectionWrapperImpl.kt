package ru.tensor.sbis.design_selection_common.controller

import android.util.Log
import ru.tensor.sbis.communication_decl.selection.SelectionConfig
import ru.tensor.sbis.communication_decl.selection.SelectionItemId
import ru.tensor.sbis.crud3.domain.ObserverCallback
import ru.tensor.sbis.design_selection.contract.controller.SelectionCollectionWrapper
import ru.tensor.sbis.design_selection.contract.data.SelectionFolderItem
import ru.tensor.sbis.design_selection.contract.data.SelectionItem
import ru.tensor.sbis.design_selection.contract.data.SelectionItemMapper
import ru.tensor.sbis.recipients.generated.CollectionObserverOfRecipientViewModel
import ru.tensor.sbis.recipients.generated.CollectionOfRecipientViewModel
import ru.tensor.sbis.recipients.generated.ItemWithIndexOfRecipientViewModel
import ru.tensor.sbis.recipients.generated.PaginationOfRecipientAnchor
import ru.tensor.sbis.recipients.generated.RecipientFilter
import ru.tensor.sbis.recipients.generated.RecipientId
import ru.tensor.sbis.recipients.generated.RecipientViewModel
import ru.tensor.sbis.service.generated.DirectionType
import ru.tensor.sbis.service.generated.IndexPair
import ru.tensor.sbis.service.generated.StubType
import ru.tensor.sbis.service.generated.ViewPosition

/**
 * Реализация обертки над коллекцией выбора получателей.
 *
 * @author vv.chekurda
 */
class SelectionCollectionWrapperImpl<ITEM : SelectionItem>(
    private val adapter: SelectionControllerAdapter<ITEM>,
    private val config: SelectionConfig,
    private val mapper: SelectionItemMapper<RecipientViewModel, RecipientId, ITEM, SelectionItemId>,
    private val folderItem: SelectionFolderItem? = null,
    private val enableLogs: Boolean = true
) : SelectionCollectionWrapper<CollectionOfRecipientViewModel,
        CollectionObserverOfRecipientViewModel, RecipientFilter, PaginationOfRecipientAnchor,
        ItemWithIndexOfRecipientViewModel, RecipientViewModel, ITEM> {

    private val itemsLimit = config.itemsLimit?.takeIf { it < Int.MAX_VALUE }?.toLong()

    override fun createEmptyFilter(): RecipientFilter =
        RecipientFilter(
            searchString = "",
            folder = folderItem?.id?.let(mapper::getId)
        )

    override fun createPaginationAnchor(
        itemsOnPage: Long,
        directionType: DirectionType
    ): PaginationOfRecipientAnchor {
        log("createPaginationAnchor directionType = $directionType, itemsOnPage = $itemsOnPage")
        return PaginationOfRecipientAnchor(null, directionType, itemsOnPage)
    }

    override fun createCollection(
        filter: RecipientFilter,
        anchor: PaginationOfRecipientAnchor
    ): CollectionOfRecipientViewModel {
        log("createCollection filter = $filter, anchor = $anchor")
        return adapter.createCollection(filter, anchor)
    }

    override fun setObserver(
        observer: CollectionObserverOfRecipientViewModel,
        toCollection: CollectionOfRecipientViewModel
    ) {
        toCollection.setObserver(observer)
    }

    override fun goNext(collection: CollectionOfRecipientViewModel, var1: Long) {
        log("goNext $var1")
        if (itemsLimit != null) return
        collection.next(var1)
    }

    override fun goPrev(collection: CollectionOfRecipientViewModel, var1: Long) {
        log("goPrev $var1")
        if (itemsLimit != null) return
        collection.prev(var1)
    }

    override fun refresh(collection: CollectionOfRecipientViewModel) {
        log("refresh")
        collection.refresh()
    }

    override fun dispose(collection: CollectionOfRecipientViewModel) {
        log("dispose")
        collection.dispose()
    }

    override fun getIndex(itemWithIndex: ItemWithIndexOfRecipientViewModel): Long =
        itemWithIndex.index

    override fun getItem(itemWithIndex: ItemWithIndexOfRecipientViewModel): ITEM =
        mapper.map(itemWithIndex.item)

    override fun createCollectionObserver(
        observer: ObserverCallback<ItemWithIndexOfRecipientViewModel, ITEM>
    ): CollectionObserverOfRecipientViewModel {
        val collectionObserver = object : CollectionObserverOfRecipientViewModel() {
            override fun onReset(items: ArrayList<RecipientViewModel>) {
                log("onReset ${this.hashCode()}, size = ${items.size}, items = ${items.map { it.id }}")
                observer.onReset(items.map(mapper::map))
            }

            override fun onRemove(index: ArrayList<Long>) {
                log("onRemove ${this.hashCode()} $index")
                observer.onRemove(index)
            }

            override fun onMove(param: ArrayList<IndexPair>) {
                log("onMove ${this.hashCode()} $param")
                observer.onMove(param)
            }

            override fun onAdd(param: ArrayList<ItemWithIndexOfRecipientViewModel>) {
                log("onAdd ${this.hashCode()} ${param.map { "${it.index} : ${it.item.id}" }}")
                observer.onAdd(param)
            }

            override fun onReplace(param: ArrayList<ItemWithIndexOfRecipientViewModel>) {
                log("onReplace ${this.hashCode()} ${param.map { "${it.index} : ${it.item.id}" }}")
                observer.onReplace(param)
            }

            override fun onAddThrobber(position: ViewPosition) {
                log("onAddThrobber ${this.hashCode()}")
                observer.onAddThrobber(position)
            }

            override fun onRemoveThrobber() {
                log("onRemoveThrobber ${this.hashCode()}")
                observer.onRemoveThrobber()
            }

            override fun onAddStub(stubType: StubType, position: ViewPosition) {
                log("onAddStub $stubType")
                observer.onAddStub(stubType, position)
            }

            override fun onRemoveStub() {
                log("onRemoveStub")
                observer.onRemoveStub()
            }

            override fun onBeginUpdate() = Unit
            override fun onEndUpdate() = Unit
        }
        return adapter.createCollectionObserver(collectionObserver)
    }

    private fun log(text: String) {
        if (!enableLogs) return
        Log.d(DEBUG_SELECTION_TAG, text)
    }
}

private const val DEBUG_SELECTION_TAG = "Selection"
package ru.tensor.sbis.crud4.domain

import androidx.annotation.AnyThread
import io.reactivex.Observable
import ru.tensor.sbis.crud4.ComponentViewModel
import ru.tensor.sbis.crud4.data.CollectionEvent
import ru.tensor.sbis.crud4.data.Crud4ObserverCallbackImpl
import ru.tensor.sbis.crud4.data.HierarchyObserverWrapper
import ru.tensor.sbis.service.DecoratedProtocol
import ru.tensor.sbis.service.HierarchyCollectionProtocol
import ru.tensor.sbis.service.PathProtocol
import ru.tensor.sbis.service.SelectionDataProtocol
import timber.log.Timber

/**
 * Выполняет работу с коллекцией и хранит колбек микросервиса.
 *
 * Описание аргументов см в [ComponentViewModel].
 */
@AnyThread
internal class CollectionPaginator<
    COLLECTION : HierarchyCollectionProtocol<in COLLECTION_OBSERVER, IDENTIFIER>,
    COLLECTION_OBSERVER,
    ITEM_WITH_INDEX,
    SOURCE_ITEM: DecoratedProtocol<IDENTIFIER>,
    PATH_MODEL : PathProtocol<IDENTIFIER>,
    IDENTIFIER>(
    private val observerWrapper: HierarchyObserverWrapper<COLLECTION_OBSERVER, ObserverCallback<ITEM_WITH_INDEX, SOURCE_ITEM, PATH_MODEL>>,
    private var viewPostSize: Int,
    private val callback: Crud4ObserverCallbackImpl<ITEM_WITH_INDEX, SOURCE_ITEM, PATH_MODEL> = Crud4ObserverCallbackImpl()
) {
    private var collection: COLLECTION? = null

    private var oldObserver: COLLECTION_OBSERVER? = null

    /**
     * Последнее направления запроса данных.
     */
    var lastLoadedPageDirection = Direction.NEXT

    init {
        setDefaultValueToLastLoadedPageDirection()
    }

    /**
     * Поток события колбека коллекции микросервиса.
     */
    val events: Observable<CollectionEvent<SOURCE_ITEM, ITEM_WITH_INDEX, PATH_MODEL>> = callback.events

    @AnyThread
    fun loadPrevious(currentItemsCount: Int) {
        lastLoadedPageDirection = Direction.PREVIOUS
        val ind = (currentItemsCount - viewPostSize - 1).takeIf { it > 0 }
            ?: (currentItemsCount - 1).coerceAtLeast(0)
        Timber.tag("crud4").d("loadPrevious direction=$lastLoadedPageDirection anchorIndex=$ind")
        collection?.prev(ind.toLong())
    }

    @AnyThread
    fun loadNext(currentItemsCount: Int) {
        lastLoadedPageDirection = Direction.NEXT
        val ind = viewPostSize.takeIf { currentItemsCount - viewPostSize >= 0 }
            ?: 0
        Timber.tag("crud4").d("loadNext  direction=$lastLoadedPageDirection anchorIndex=$ind")
        collection?.next(ind.toLong())
    }

    @AnyThread
    fun setCollection(collection: COLLECTION) {
        this.collection = collection
        oldObserver?.let {
            observerWrapper.asDisposable(it).dispose()
        }
        val observer = observerWrapper.createObserver(callback)
            .also { oldObserver = it }
        collection.setObserver(observer)
    }

    fun refresh() = collection?.refresh()

    fun changeRoot(pathModel: PATH_MODEL?) = collection?.changeRoot(pathModel!!.ident)

    fun collapse(pos: Long) = collection?.collapse(pos)

    fun expand(pos: Long) = collection?.expand(pos)

    fun mark(pos: Long) = collection?.mark(pos)

    fun select(pos: Long) = collection?.select(pos, null)

    fun getSelection(): SelectionDataProtocol<IDENTIFIER> {
        return collection?.getSelection()
            ?: throw IllegalStateException("Нельзя запрашивать выделение до того как была установленна коллекция")
    }

    fun resetSelection() = collection?.resetSelection()

    private fun setDefaultValueToLastLoadedPageDirection() {
        lastLoadedPageDirection = Direction.NEXT
    }

    fun dispose() {
        collection = null
    }
}

/**
 * Направления запроса данных пагинации.
 */
enum class Direction {
    NEXT,
    PREVIOUS
}
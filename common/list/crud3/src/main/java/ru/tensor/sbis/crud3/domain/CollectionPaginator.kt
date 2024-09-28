package ru.tensor.sbis.crud3.domain

import androidx.annotation.AnyThread
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import ru.tensor.sbis.crud3.ComponentViewModel
import ru.tensor.sbis.crud3.data.CollectionEvent
import ru.tensor.sbis.crud3.data.Crud3ObserverCallbackImpl
import ru.tensor.sbis.service.generated.DirectionType
import timber.log.Timber

/**
 * Выполняет работу с коллекцией и хранит колбек микросервиса.
 *
 * Описание аргументов см в [ComponentViewModel].
 */
@AnyThread
internal class CollectionPaginator<COLLECTION : Any, COLLECTION_OBSERVER, FILTER, PAGINATION_ANCHOR, ITEM_WITH_INDEX, SOURCE_ITEM>(
    private val wrapper: Wrapper<COLLECTION, COLLECTION_OBSERVER, FILTER, PAGINATION_ANCHOR, ITEM_WITH_INDEX, SOURCE_ITEM>,
    val pageSize: Int,
    private var viewPostSize: Int,
    initialPageDirection: PageDirection = PageDirection.BOTHWAY,
    private val callback: Crud3ObserverCallbackImpl<ITEM_WITH_INDEX, SOURCE_ITEM> = Crud3ObserverCallbackImpl()
) {
    private var collection: COLLECTION? = null
    private var filter = wrapper.createEmptyFilter()

    /**
     * Режим пагинации списка.
     */
    private var page = 0
    private val initialDirection: DirectionType = initialPageDirection.toDirectionType()

    /**
     * Последнее направления запроса данных.
     */
    var lastLoadedPageDirection = Direction.NEXT

    init {
        setDefaultValueToLastLoadedPageDirection()
        resetPage()
    }

    /**
     * Поток события колбека коллекции микросервиса.
     */
    val events: Observable<CollectionEvent<SOURCE_ITEM, ITEM_WITH_INDEX>> = callback.events

    @AnyThread
    fun startListenToCollection() {
        resetPage()
        createCollectionAndSetCallbackInBackgroundThread()
    }

    @AnyThread
    fun loadPrevious(currentItemsCount: Int) {
        page--
        lastLoadedPageDirection = Direction.PREVIOUS
        var ind = viewPostSize * 2 - 1
        if (ind > currentItemsCount - 1) ind = currentItemsCount - 1
        Timber.tag("CRUD3").d("loadPrevious page=$page direction=$lastLoadedPageDirection anchorIndex=$ind")
        collection?.let { wrapper.goPrev(it, ind.toLong()) }
    }

    @AnyThread
    fun loadNext(currentItemsCount: Int) {
        page++
        lastLoadedPageDirection = Direction.NEXT
        var ind = currentItemsCount - viewPostSize * 2
        if (ind < 0) ind = 0
        Timber.tag("CRUD3").d("loadNext page=$page direction=$lastLoadedPageDirection anchorIndex=$ind")
        collection?.let { wrapper.goNext(it, ind.toLong()) }
    }

    @AnyThread
    fun setFilter(filter: FILTER) {
        this.filter = filter
        startListenToCollection()
    }

    @AnyThread
    fun resetFilterToEmpty() {
        filter = wrapper.createEmptyFilter()
        startListenToCollection()
    }

    fun refresh() = collection?.let { wrapper.refresh(it) }

    fun hasSame(filter: FILTER?) = filter == this.filter

    fun isZeroPage(items: List<SOURCE_ITEM>): Boolean = wrapper.isZeroPage(items) ?: (page == 0)

    @AnyThread
    fun stopListenToCollection() {
        collection?.let { wrapper.dispose(it) }
    }

    private fun createCollectionAndSetCallbackInBackgroundThread() {
        collection?.let { wrapper.dispose(it) }

        Completable.fromCallable {
            setDefaultValueToLastLoadedPageDirection()

            collection = wrapper.createCollection(
                filter,
                wrapper.createPaginationAnchor(
                    pageSize.toLong(),
                    initialDirection
                )
            )
            wrapper.setObserver(
                wrapper.createCollectionObserver(callback),
                collection!!
            )
            @Suppress("RedundantUnitExpression")
            Unit //Это нужно.
        }
            .subscribeOn(Schedulers.single())
            .subscribe()
    }

    private fun resetPage() {
        page = if (initialDirection == DirectionType.FORWARD) 0 else Int.MAX_VALUE / 2
    }

    private fun setDefaultValueToLastLoadedPageDirection() {
        lastLoadedPageDirection = Direction.NEXT
    }
}

/**
 * Направления запроса данных пагинации.
 */
enum class Direction {
    NEXT,
    PREVIOUS
}
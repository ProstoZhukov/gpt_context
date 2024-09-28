package ru.tensor.sbis.crud3.view.viewmodel

import androidx.annotation.AnyThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.closeItOnDestroyVm
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.runOnDestroy
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import ru.tensor.sbis.crud3.CollectionViewModel
import ru.tensor.sbis.crud3.ResetableRefreshable
import ru.tensor.sbis.crud3.data.OnAdd
import ru.tensor.sbis.crud3.data.OnAddStub
import ru.tensor.sbis.crud3.data.OnAddThrobber
import ru.tensor.sbis.crud3.data.OnMove
import ru.tensor.sbis.crud3.data.OnRemove
import ru.tensor.sbis.crud3.data.OnRemoveStub
import ru.tensor.sbis.crud3.data.OnRemoveThrobber
import ru.tensor.sbis.crud3.data.OnReplace
import ru.tensor.sbis.crud3.data.OnReset
import ru.tensor.sbis.crud3.domain.CollectionPaginator
import ru.tensor.sbis.crud3.domain.Direction
import ru.tensor.sbis.crud3.domain.ItemWithIndex
import ru.tensor.sbis.crud3.view.CollectionItems
import ru.tensor.sbis.crud3.view.datachange.DataChange
import ru.tensor.sbis.crud3.view.datachange.ItemChanged
import ru.tensor.sbis.crud3.view.datachange.ItemInserted
import ru.tensor.sbis.crud3.view.datachange.ItemMoved
import ru.tensor.sbis.crud3.view.datachange.ItemRemoved
import ru.tensor.sbis.crud3.view.datachange.SetItems
import ru.tensor.sbis.crud3.view.SingleLiveEvent
import ru.tensor.sbis.crud3.view.StubFactory
import ru.tensor.sbis.crud3.view.StubType.NO_NETWORK
import ru.tensor.sbis.crud3.view.StubType.SERVER_TROUBLE
import ru.tensor.sbis.design.stubview.StubViewContent
import ru.tensor.sbis.list.view.DataChangedObserver.ItemVisibilityPositionProvider
import ru.tensor.sbis.service.generated.StubType
import ru.tensor.sbis.service.generated.ViewPosition
import timber.log.Timber

/**
 * Реализация вью модели крана списка на основе CRUD3 коллекции. Удерживает данные при смене конфигурации приложения.
 * Содержит в себе недостающую в контроллере логику работы с пагинацией, отображением индикаторов пагинации по месту.
 * Связывает события контроллера по отображению заглушки с реализациями заглушек, предоставленными пользователем
 * компонента, а так же реагирует на пулл-ту-рефреш.
 */
@AnyThread
internal class CollectionViewModelImpl<COLLECTION : Any, COLLECTION_OBSERVER, FILTER, PAGINATION_ANCHOR, ITEM_WITH_INDEX, SOURCE_ITEM>(
    private val wrapper: ItemWithIndex<ITEM_WITH_INDEX, SOURCE_ITEM>,
    private val paginator: CollectionPaginator<COLLECTION, COLLECTION_OBSERVER, FILTER, PAGINATION_ANCHOR, ITEM_WITH_INDEX, SOURCE_ITEM>,
    stubFactory: StubFactory,
) : ViewModel(), CollectionViewModel<FILTER, SOURCE_ITEM>,
    ResetableRefreshable<FILTER> {

    private val _stubFactory = MutableLiveData<StubViewContent>(null)
    private val _stubVisibility = MutableLiveData(false)
    private val _toolbarNoNetworkVisibility = MutableLiveData(false)
    private val _centralThrobberVisibility = MutableLiveData(false)
    private val _toolbarThrobberVisibility = MutableLiveData(false)
    private val paginatorDisposable: Disposable
    private val _dataChange = PublishSubject.create<DataChange<SOURCE_ITEM>>()
    private val _loadNextAvailable = LiveDataWithActualValue(false)
    private val _loadPreviousAvailable = LiveDataWithActualValue(false)
    private val _loadNextThrobberIsVisible = MutableLiveData(false)
    private val _loadPreviousThrobberIsVisible = MutableLiveData(false)
    private val _refreshIsAvailable = LiveDataWithActualValue(true)
    private val _isRefreshing = MutableLiveData(false)
    private val collectionItems = CollectionItems<SOURCE_ITEM>()
    override val dataChange: Observable<DataChange<SOURCE_ITEM>> = _dataChange
        /**
         * Старт подписки происходит при создании View, это может произойти после смены конфигурации,
         * в таком случае необходимо показать данные на момент уничтожения View.
         */
        .startWith(Observable.fromCallable { SetItems(collectionItems.getAllItems()) })
    override val stubVisibility: LiveData<Boolean> = _stubVisibility.distinctUntilChanged()
    override val toolbarNoNetworkVisibility = _toolbarNoNetworkVisibility.distinctUntilChanged()
    override val centralThrobberVisibility: LiveData<Boolean> = _centralThrobberVisibility.distinctUntilChanged()
    override val toolbarThrobberVisibility = _toolbarThrobberVisibility.distinctUntilChanged()
    override val stubFactory: LiveData<StubViewContent> = _stubFactory.distinctUntilChanged()
    override val isZeroPage
        get() = paginator.isZeroPage(collectionItems.getAllItems())
    override var loadNextAvailable: LiveData<Boolean> = _loadNextAvailable.distinctUntilChanged()
    override var loadPreviousAvailable: LiveData<Boolean> = _loadPreviousAvailable.distinctUntilChanged()
    override var loadNextThrobberIsVisible: LiveData<Boolean> = _loadNextThrobberIsVisible.distinctUntilChanged()
    override var loadPreviousThrobberIsVisible: LiveData<Boolean> =
        _loadPreviousThrobberIsVisible.distinctUntilChanged()
    override var refreshIsAvailable: LiveData<Boolean> = _refreshIsAvailable.distinctUntilChanged()
    override var isRefreshing: LiveData<Boolean> = _isRefreshing
    override val scrollScrollToZeroPosition = SingleLiveEvent<Int>()
    override var needScrollToFirstOnReset: Boolean = true

    private var isNeedOnResetScroll = false
    init {
        paginator.startListenToCollection()
        runOnDestroy {
            paginator.stopListenToCollection()
        }
        paginatorDisposable = paginator.events
            .subscribe({ result ->
                _isRefreshing.postValue(false)
                when (result) {
                    is OnReset -> {
                        collectionItems.reset(result.p0)
                        _dataChange.onNext(SetItems(collectionItems.getAllItems()))
                        removeInPlaceThrobbers()
                        _stubVisibility.postValue(false)
                        updatePaginationAvailabilities(isReset = true)
                        isNeedOnResetScroll = true
                    }

                    is OnAdd -> {
                        val pairs = result.p0.map {
                            val index = wrapper.getIndex(it)
                            val item = wrapper.getItem(it)
                            Pair(index, item)
                        }
                        collectionItems.add(pairs)
                        _dataChange.onNext(ItemInserted(pairs, collectionItems.getAllItems()))
                        removeInPlaceThrobbers()
                        updatePaginationAvailabilities()
                    }

                    is OnMove -> {
                        collectionItems.move(result.p0)
                        _dataChange.onNext(ItemMoved(result.p0, collectionItems.getAllItems()))
                        removeInPlaceThrobbers()
                        updatePaginationAvailabilities()
                    }

                    is OnRemove -> {
                        collectionItems.remove(result.p0)
                        _dataChange.onNext(ItemRemoved(result.p0, collectionItems.getAllItems()))
                        removeInPlaceThrobbers()
                        updatePaginationAvailabilities()
                    }

                    is OnReplace -> {
                        val pairs = result.p0.map {
                            val index = wrapper.getIndex(it)
                            val item = wrapper.getItem(it)
                            Pair(index, item)
                        }
                        collectionItems.replace(pairs)
                        _dataChange.onNext(ItemChanged(pairs, collectionItems.getAllItems()))
                    }

                    is OnAddThrobber -> {
                        when (result.position) {
                            ViewPosition.IN_PLACE -> {
                                if (hasItems()) {
                                    if (paginator.lastLoadedPageDirection == Direction.NEXT) {
                                        _loadNextThrobberIsVisible.postValue(true)
                                        _loadNextAvailable.postValue(false)
                                    } else {
                                        _loadPreviousThrobberIsVisible.postValue(true)
                                        _loadPreviousAvailable.postValue(false)
                                    }
                                } else if (_isRefreshing.value != true) {
                                    _centralThrobberVisibility.postValue(true)
                                    _stubVisibility.postValue(false)
                                    _dataChange.onNext(SetItems(emptyList()))
                                }
                            }

                            ViewPosition.HEADER -> {
                                _toolbarThrobberVisibility.postValue(true)
                            }
                        }
                    }

                    is OnRemoveThrobber -> {
                        removeInPlaceThrobbers()
                        _toolbarThrobberVisibility.postValue(false)
                    }

                    is OnAddStub -> {
                        when (result.position) {
                            ViewPosition.IN_PLACE -> {
                                _stubFactory.postValue(
                                    stubFactory.create(
                                        when (result.stubType) {
                                            StubType.NO_DATA_STUB -> ru.tensor.sbis.crud3.view.StubType.NO_DATA
                                            StubType.BAD_FILTER_STUB -> ru.tensor.sbis.crud3.view.StubType.BAD_FILTER
                                            StubType.NO_NETWORK_STUB -> NO_NETWORK
                                            StubType.SERVER_TROUBLE -> SERVER_TROUBLE
                                            StubType.ENTRY_NOT_FOUND -> SERVER_TROUBLE
                                        }
                                    )
                                )
                                _stubVisibility.postValue(true)
                                collectionItems.reset(emptyList())
                                _dataChange.onNext(SetItems(emptyList()))
                                removeInPlaceThrobbers()
                            }

                            ViewPosition.HEADER -> {
                                if (result.stubType == StubType.NO_NETWORK_STUB) _toolbarNoNetworkVisibility.postValue(
                                    true
                                )
                                else Timber.e("Недопустимое сочетание типа заглушки ${result.stubType} и позиции ${result.position}")
                            }
                        }
                    }

                    is OnRemoveStub -> {
                        _stubVisibility.postValue(false)
                        _toolbarNoNetworkVisibility.postValue(false)
                    }
                }
            }) {
                Timber.e(it)
            }

        paginatorDisposable.closeItOnDestroyVm(this)
    }

    override fun refresh() {
        _isRefreshing.postValue(true)
        paginator.refresh()
        _isRefreshing.postValue(false)
    }

    override fun reset(filter: FILTER?) {
        if (paginator.hasSame(filter)) return
        innerReset(filter)
    }

    override fun resetForce(filter: FILTER?) {
        innerReset(filter)
    }

    override fun loadPrevious() {
        if (!_loadPreviousAvailable.actualValue) return

        _loadNextAvailable.postValue(true)
        _loadPreviousAvailable.postValue(false)
        paginator.loadPrevious(collectionItems.size)
    }

    override fun loadNext() {
        if (!_loadNextAvailable.actualValue) return

        _loadPreviousAvailable.postValue(true)
        _loadNextAvailable.postValue(false)
        paginator.loadNext(collectionItems.size)
    }

    override fun onItemRangeInserted(
        positionStart: Int,
        itemCount: Int,
        provider: ItemVisibilityPositionProvider
    ) {
        if (positionStart == 0
            && itemCount <= 3
            && provider.findFirstCompletelyVisibleItemPosition() == 0
        ) scrollScrollToZeroPosition.postValue(0)

        if (isNeedOnResetScroll && needScrollToFirstOnReset){
            scrollScrollToZeroPosition.postValue(0)
            isNeedOnResetScroll = false
        }
    }

    private fun innerReset(filter: FILTER?) {
        if (filter == null) {
            _centralThrobberVisibility.postValue(true)
            paginator.resetFilterToEmpty()
            return
        }

        paginator.setFilter(filter)
    }

    private fun removeInPlaceThrobbers() {
        _centralThrobberVisibility.postValue(false)
        _loadNextThrobberIsVisible.postValue(false)
        _loadPreviousThrobberIsVisible.postValue(false)
    }

    private fun updatePaginationAvailabilities(isReset: Boolean = false) {
        val isZeroPage = paginator.isZeroPage(collectionItems.getAllItems())
        val (isPreviousAvailable, isNextAvailable) = when {
            // На позиции нулевой страницы, предыдущих данных за ней нет,
            // следующие данные могут быть только для полной страницы.
            isZeroPage -> {
                false to isFullPage()
            }
            // Не на нулевой странице и список полный, данные могут быть с любой стороны.
            isFullPage() -> {
                true to true
            }
            // Если при сбросе получили неполную страницу, данных с обеих сторон нет.
            isReset -> {
                false to false
            }
            // Скролились в сторону предыдущих, уперлись - данных в эту сторону больше нет,
            // в сторону следующих могут быть.
            paginator.lastLoadedPageDirection == Direction.PREVIOUS -> {
                false to true
            }
            // Скролились в сторону следующих, уперлись - данных в эту сторону больше нет,
            // в сторону предыдущих могут быть.
            else -> {
                true to false
            }
        }
        _loadPreviousAvailable.postValue(isPreviousAvailable)
        _loadNextAvailable.postValue(isNextAvailable)
        _refreshIsAvailable.postValue(isZeroPage)
    }

    private fun isFullPage() = collectionItems.size >= paginator.pageSize
    private fun hasItems() = collectionItems.getAllItems().isNotEmpty()
}
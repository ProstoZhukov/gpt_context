package ru.tensor.sbis.crud4.view.viewmodel

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
import ru.tensor.sbis.crud4.CollectionViewModel
import ru.tensor.sbis.crud4.ResetableRefreshable
import ru.tensor.sbis.crud4.data.OnAdd
import ru.tensor.sbis.crud4.data.OnAddStub
import ru.tensor.sbis.crud4.data.OnAddThrobber
import ru.tensor.sbis.crud4.data.OnEndUpdate
import ru.tensor.sbis.crud4.data.OnMark
import ru.tensor.sbis.crud4.data.OnMove
import ru.tensor.sbis.crud4.data.OnPath
import ru.tensor.sbis.crud4.data.OnRemove
import ru.tensor.sbis.crud4.data.OnRemoveStub
import ru.tensor.sbis.crud4.data.OnRemoveThrobber
import ru.tensor.sbis.crud4.data.OnReplace
import ru.tensor.sbis.crud4.data.OnReset
import ru.tensor.sbis.crud4.data.OnRestorePosition
import ru.tensor.sbis.crud4.data.OnSelect
import ru.tensor.sbis.crud4.domain.CollectionPaginator
import ru.tensor.sbis.crud4.domain.Direction
import ru.tensor.sbis.crud4.domain.ItemWithIndex
import ru.tensor.sbis.crud4.view.CollectionItems
import ru.tensor.sbis.crud4.view.SingleLiveEvent
import ru.tensor.sbis.crud4.view.StubFactory
import ru.tensor.sbis.crud4.view.StubType.NO_NETWORK
import ru.tensor.sbis.crud4.view.StubType.SERVER_TROUBLE
import ru.tensor.sbis.crud4.view.datachange.DataChange
import ru.tensor.sbis.crud4.view.datachange.ItemChanged
import ru.tensor.sbis.crud4.view.datachange.ItemInserted
import ru.tensor.sbis.crud4.view.datachange.ItemMoved
import ru.tensor.sbis.crud4.view.datachange.ItemRemoved
import ru.tensor.sbis.crud4.view.datachange.SetItems
import ru.tensor.sbis.design.stubview.StubViewContent
import ru.tensor.sbis.list.view.DataChangedObserver.ItemVisibilityPositionProvider
import ru.tensor.sbis.service.DecoratedProtocol
import ru.tensor.sbis.service.HierarchyCollectionObserverProtocol
import ru.tensor.sbis.service.HierarchyCollectionProtocol
import ru.tensor.sbis.service.PathProtocol
import ru.tensor.sbis.service.SelectionDataProtocol
import ru.tensor.sbis.service.generated.DirectionStatus
import ru.tensor.sbis.service.generated.StubType
import ru.tensor.sbis.service.generated.ViewPosition
import timber.log.Timber

/**
 * Реализация вью модели крана списка на основе crud4 коллекции. Удерживает данные при смене конфигурации приложения.
 * Содержит в себе недостающую в контроллере логику работы с пагинацией, отображением индикаторов пагинации по месту.
 * Связывает события контроллера по отображению заглушки с реализациями заглушек, предоставленными пользователем
 * компонента, а так же реагирует на пулл-ту-рефреш.
 */
@AnyThread
internal class CollectionViewModelImpl<
    COLLECTION : HierarchyCollectionProtocol<in COLLECTION_OBSERVER, IDENTIFIER>,
    COLLECTION_OBSERVER,
    FILTER,
    ITEM_WITH_INDEX,
    SOURCE_ITEM : DecoratedProtocol<IDENTIFIER>,
    PATH_MODEL : PathProtocol<IDENTIFIER>,
    IDENTIFIER
>(
    private val itemWithIndexExtractor: ItemWithIndex<ITEM_WITH_INDEX, SOURCE_ITEM>,
    private val paginator: CollectionPaginator<COLLECTION, COLLECTION_OBSERVER, ITEM_WITH_INDEX, SOURCE_ITEM, PATH_MODEL, IDENTIFIER>,
    stubFactory: StubFactory,
) : ViewModel(), CollectionViewModel<COLLECTION, FILTER, SOURCE_ITEM, PATH_MODEL, IDENTIFIER>,
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
    private val _onPath: MutableLiveData<List<PATH_MODEL>> = MutableLiveData()
    private val _selectedSizeChange: MutableLiveData<Long?> = MutableLiveData()
    private val _loadPreviousThrobberIsVisible = MutableLiveData(false)
    private val _refreshIsAvailable = LiveDataWithActualValue(true)
    private val _onEndUpdate = MutableLiveData<DirectionStatus>()
    private val _isRefreshing = MutableLiveData(false)
    private val collectionItems = CollectionItems<SOURCE_ITEM>()
    private val _onChangeFilter = SingleLiveEvent<FILTER?>()

    override val dataChange: Observable<DataChange<SOURCE_ITEM>> = _dataChange
        /**
         * Старт подписки происходит при создании View, это может произойти после смены конфигурации,
         * в таком случае необходимо показать данные на момент уничтожения View.
         */
        .startWith(Observable.fromCallable { SetItems(collectionItems.getAllItems()) })

    override val stubVisibility: LiveData<Boolean> = _stubVisibility.distinctUntilChanged()
    override val onPath: LiveData<List<PATH_MODEL>> = _onPath
    override val selectedSizeChange: LiveData<Long?> = _selectedSizeChange
    override val onEndUpdate: LiveData<DirectionStatus> = _onEndUpdate
    override val toolbarNoNetworkVisibility = _toolbarNoNetworkVisibility.distinctUntilChanged()
    override val centralThrobberVisibility: LiveData<Boolean> = _centralThrobberVisibility.distinctUntilChanged()
    override val toolbarThrobberVisibility = _toolbarThrobberVisibility.distinctUntilChanged()
    override val stubFactory: LiveData<StubViewContent> = _stubFactory.distinctUntilChanged()
    override var isZeroPage: Boolean = true
    override var loadNextAvailable: LiveData<Boolean> = _loadNextAvailable.distinctUntilChanged()
    override var loadPreviousAvailable: LiveData<Boolean> = _loadPreviousAvailable.distinctUntilChanged()
    override var loadNextThrobberIsVisible: LiveData<Boolean> = _loadNextThrobberIsVisible.distinctUntilChanged()
    override var loadPreviousThrobberIsVisible: LiveData<Boolean> =
        _loadPreviousThrobberIsVisible.distinctUntilChanged()
    override var refreshIsAvailable: LiveData<Boolean> = _refreshIsAvailable.distinctUntilChanged()
    override var isRefreshing: LiveData<Boolean> = _isRefreshing
    override val scrollScrollToZeroPosition = SingleLiveEvent<Int>()
    override val scrollScrollToPosition = SingleLiveEvent<Int>()

    override val onChangeFilter: LiveData<FILTER?> = _onChangeFilter

    init {
        runOnDestroy {
            paginator.dispose()
        }

        paginatorDisposable = paginator.events
            .subscribe({ result ->
                _isRefreshing.postValue(false)
                when (result) {
                    is OnReset -> {
                        collectionItems.reset(result.p0)
                        _dataChange.onNext(SetItems(collectionItems.getAllItems(), forceScrollToInitialPosition = true))
                        removeInPlaceThrobbers()
                        _stubVisibility.postValue(false)
                    }

                    is OnAdd -> {
                        val pairs = result.p0.map {
                            val index = itemWithIndexExtractor.getIndex(it)
                            val item = itemWithIndexExtractor.getItem(it)
                            Pair(index, item)
                        }
                        collectionItems.add(pairs)
                        _dataChange.onNext(ItemInserted(pairs, collectionItems.getAllItems()))
                        removeInPlaceThrobbers()
                    }

                    is OnMove -> {
                        collectionItems.move(result.p0)
                        _dataChange.onNext(ItemMoved(result.p0, collectionItems.getAllItems()))
                        removeInPlaceThrobbers()
                    }

                    is OnRemove -> {
                        collectionItems.remove(result.p0)
                        _dataChange.onNext(ItemRemoved(result.p0, collectionItems.getAllItems()))
                        removeInPlaceThrobbers()
                    }

                    is OnReplace -> {
                        val pairs = result.p0.map {
                            val index = itemWithIndexExtractor.getIndex(it)
                            val item = itemWithIndexExtractor.getItem(it)
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
                                            StubType.NO_DATA_STUB -> ru.tensor.sbis.crud4.view.StubType.NO_DATA
                                            StubType.BAD_FILTER_STUB -> ru.tensor.sbis.crud4.view.StubType.BAD_FILTER
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

                    is OnPath -> {
                        _onPath.postValue(result.path)
                    }

                    is OnEndUpdate -> {
                        isZeroPage = result.haveMore.backward == false
                        updatePaginationAvailabilities(result.haveMore)
                        _onEndUpdate.postValue(result.haveMore)
                    }

                    is OnMark -> {
                        val changedItems = mutableListOf<Pair<Long, SOURCE_ITEM>>()

                        result.marked.disable?.let {
                            val disableItem = collectionItems.getAllItems()[it.toInt()]
                            disableItem.isMarked = false
                            changedItems.add(Pair(it, disableItem))
                        }

                        result.marked.enable?.let {
                            val enableItem = collectionItems.getAllItems()[it.toInt()]
                            enableItem.isMarked = true
                            changedItems.add(Pair(it, enableItem))
                        }

                        if (changedItems.size > 0) {
                            _dataChange.onNext(ItemChanged(changedItems, collectionItems.getAllItems()))
                        }
                    }

                    is OnSelect -> {
                        val changedItems = mutableListOf<Pair<Long, SOURCE_ITEM>>()
                        result.selected.forEach {
                            val selectedItem = collectionItems.getAllItems()[it.pos.toInt()]
                            selectedItem.isSelected = it.status
                            changedItems.add(Pair(it.pos, selectedItem))
                        }
                        if (changedItems.size > 0) {
                            _dataChange.onNext(ItemChanged(changedItems, collectionItems.getAllItems()))
                        }
                        _selectedSizeChange.postValue(result.counter.size)
                    }

                    is OnRestorePosition -> {
                        scrollScrollToPosition.postValue(result.pos.toInt())
                    }
                }
            }) {
                Timber.e(it)
            }

        paginatorDisposable.closeItOnDestroyVm(this)
    }

    override fun changeRoot(pathModel: PATH_MODEL?) {
        paginator.changeRoot(pathModel)
    }

    override fun expand(item: SOURCE_ITEM) {
        expand(getIndex(item))
    }

    override fun expand(index: Long) {
        if (index < 0) return
        paginator.expand(index)
    }

    override fun collapse(item: SOURCE_ITEM) {
        val pos = getIndex(item)
        if (pos < 0) return
        paginator.collapse(pos)
    }

    override fun mark(item: SOURCE_ITEM) {
        val pos = getIndex(item)
        if (pos < 0) return
        paginator.mark(pos)
    }

    override fun select(item: SOURCE_ITEM) {
        val pos = getIndex(item)
        if (pos < 0) return
        paginator.select(pos)
    }

    override fun getSelected(): SelectionDataProtocol<IDENTIFIER> {
        return paginator.getSelection()
    }

    override fun resetSelection() {
        paginator.resetSelection()
    }

    override fun refresh() {
        _isRefreshing.postValue(true)
        paginator.refresh()
        _isRefreshing.postValue(false)
    }

    override fun reset(filter: FILTER?) {
        if (_onChangeFilter.value == filter) return
        _onChangeFilter.postValue(filter)
    }

    override fun resetForce(filter: FILTER?) {
        _onChangeFilter.postValue(filter)
    }

    override fun setCollection(collection: COLLECTION) {
        paginator.setCollection(collection)
        removeInPlaceThrobbers()
        _toolbarThrobberVisibility.postValue(false)
    }

    override fun loadPrevious() {
        if (!_loadPreviousAvailable.actualValue) return

        paginator.loadPrevious(collectionItems.size)
    }

    override fun loadNext() {
        if (!_loadNextAvailable.actualValue) return

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
    }

    private fun removeInPlaceThrobbers() {
        _centralThrobberVisibility.postValue(false)
        _loadNextThrobberIsVisible.postValue(false)
        _loadPreviousThrobberIsVisible.postValue(false)
    }

    private fun updatePaginationAvailabilities(directionStatus: DirectionStatus) {
        _loadPreviousAvailable.postValue(directionStatus.backward)
        _loadNextAvailable.postValue(directionStatus.forward)
        _refreshIsAvailable.postValue(!directionStatus.backward)
    }

    private fun getIndex(item: SOURCE_ITEM) = collectionItems.getAllItems().indexOf(item).toLong()

    private fun hasItems() = collectionItems.getAllItems().isNotEmpty()
}
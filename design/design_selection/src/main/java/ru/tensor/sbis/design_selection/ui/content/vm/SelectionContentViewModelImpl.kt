package ru.tensor.sbis.design_selection.ui.content.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.SerialDisposable
import io.reactivex.subjects.PublishSubject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch
import org.apache.commons.lang3.StringUtils
import ru.tensor.sbis.common.rx.plusAssign
import ru.tensor.sbis.common.util.storeIn
import ru.tensor.sbis.communication_decl.selection.SelectionConfig
import ru.tensor.sbis.design_selection.contract.data.SelectionFolderItem
import ru.tensor.sbis.design_selection.contract.data.SelectionItem
import ru.tensor.sbis.design_selection.contract.filter.SelectionFilterMeta
import ru.tensor.sbis.design_selection.contract.listeners.SelectionResultListener.SelectionComponentResult
import ru.tensor.sbis.design_selection.domain.SelectionInteractor
import ru.tensor.sbis.design_selection.domain.list.SelectionListComponent
import ru.tensor.sbis.design_selection.domain.list.SelectionStrategy
import ru.tensor.sbis.design_selection.domain.list.SelectionStrategyHelper
import ru.tensor.sbis.design_selection.ui.content.listener.SelectionItemClickListener
import ru.tensor.sbis.design_selection.ui.content.listener.SelectionItemClickListener.ClickType
import ru.tensor.sbis.toolbox_decl.selection_statistic.SelectionStatisticAction.CLICK_ON_ADD_BUTTON
import ru.tensor.sbis.toolbox_decl.selection_statistic.SelectionStatisticAction.CLICK_ON_ITEM
import ru.tensor.sbis.toolbox_decl.selection_statistic.SelectionStatisticAction.SEARCH
import ru.tensor.sbis.toolbox_decl.selection_statistic.SelectionStatisticEvent
import ru.tensor.sbis.design_selection.ui.content.utils.SelectionStatisticUtil
import ru.tensor.sbis.design_selection.ui.content.vm.search.SelectionSearchViewModel
import ru.tensor.sbis.design_selection.ui.main.router.SelectionRouter
import ru.tensor.sbis.design_selection.ui.main.utils.SelectionLogger
import ru.tensor.sbis.design_selection.ui.main.utils.SelectionRulesHelper
import ru.tensor.sbis.design_selection.ui.main.vm.contract.SelectionContentDelegate
import timber.log.Timber
import java.util.concurrent.TimeUnit

internal class SelectionContentViewModelImpl<ITEM : SelectionItem>(
    private val contentDelegate: SelectionContentDelegate<ITEM>,
    private val searchVM: SelectionSearchViewModel,
    private val selectionInteractor: SelectionInteractor<ITEM>,
    private val listComponent: SelectionListComponent,
    private val selectionHelper: SelectionStrategyHelper<ITEM>,
    private val rulesHelper: SelectionRulesHelper,
    clickListener: SelectionItemClickListener<ITEM>,
    folderItem: SelectionFolderItem?,
    private val updateSelectedItemsSubject: PublishSubject<Boolean> = PublishSubject.create(),
    private val config: SelectionConfig,
    private val mainDispatcher: CoroutineDispatcher = Dispatchers.Main
) : ViewModel(), SelectionContentViewModel<ITEM> {

    private var router: SelectionRouter? = null

    private var filterMeta = SelectionFilterMeta(query = StringUtils.EMPTY, folderItem?.id)
    private val isRootContent = folderItem == null

    private val filterChangesDisposable = SerialDisposable()
    private val disposer = CompositeDisposable().apply {
        add(filterChangesDisposable)
    }

    private var isContentVisible: Boolean = true
    private val isTesting = mainDispatcher != Dispatchers.Main

    override val onUnselectClicked: Observable<Pair<ITEM, Boolean>>
        get() = contentDelegate.selectedItemsClickListener.onUnselectClicked

    init {
        SelectionLogger.onCreateCollection(folderItem?.id)
        clickListener.clickEvent
            .subscribe { onItemClicked(it.first, it.second) }
            .storeIn(disposer)
        updateSelectedItemsSubject.throttleLatest(UPDATE_SELECTED_ITEMS_THROTTLE_MS, TimeUnit.MILLISECONDS)
            .flatMapSingle(selectionInteractor::getSelectedData)
            .subscribe { selected ->
                SelectionLogger.onSetSelected(selected.items.map { it.id })
                contentDelegate.setSelectedData(selected)
            }.storeIn(disposer)
        searchVM.searchQueryObservable
            .subscribe { query ->
                SelectionStatisticUtil.sendStatistic(
                    SelectionStatisticEvent(config.useCase.name, SEARCH.value)
                )
                SelectionLogger.onSearch(query)
                onSearchQueryChanged(query)
            }.storeIn(disposer)
        contentDelegate.result
            .subscribe(
                {
                    if (config.isFinalComplete) {
                        searchVM.hideKeyboard()
                    }
                },
                Timber::e,
                searchVM::hideKeyboard
            ).storeIn(disposer)
        contentDelegate.clearSelectedObservable
            .flatMapCompletable { selectionInteractor.unselectAll() }
            .subscribe {
                updateSelectedItems(true)
            }.storeIn(disposer)

        subscribeOnFilterChanges()
        subscribeOnOuterSearchQuery()

        updateSelectedItems(isUserSelection = false)

        if (!isTesting) {
            listComponent.isStubVisible.observeForever { isVisible ->
                contentDelegate.hasSelectableItems.tryEmit(!isVisible)
            }
            viewModelScope.launch(mainDispatcher) {
                contentDelegate.resetScroll.collect {
                    listComponent.scrollToPosition(0)
                }
            }
        }

        if (isRootContent) {
            contentDelegate.onDoneButtonClickedObservable
                .subscribe { onCompleteClicked() }
                .storeIn(disposer)
        }
    }

    override fun select(item: ITEM, append: Boolean) {
        when (selectionHelper.produceSelectStrategy(item, append)) {
            SelectionStrategy.SELECT -> {
                SelectionLogger.onSelect(item.id, append)
                performSelect(item)
            }
            SelectionStrategy.SELECT_AND_CANCEL_SEARCH -> {
                SelectionLogger.onSelect(item.id, append)
                performSelectBySearch(item)
            }
            SelectionStrategy.REPLACE_SELECTED -> {
                SelectionLogger.onSelect(item.id, append)
                performReplaceSelected(item)
            }
            SelectionStrategy.REPLACE_SELECTED_AND_CANCEL_SEARCH -> {
                SelectionLogger.onSelect(item.id, append)
                performReplaceSelectedBySearch(item)
            }
            SelectionStrategy.COMPLETE -> {
                SelectionLogger.onSelectComplete(item.id)
                performComplete(item, appended = append)
            }
            SelectionStrategy.OPEN_FOLDER -> openFolder(item)
        }
    }

    private fun openFolder(item: ITEM) {
        searchVM.hideKeyboard()
        router?.openFolder(item as SelectionFolderItem)
    }

    private fun performSelect(item: ITEM) {
        selectionInteractor.select(item.id)
            .subscribe(
                { updateSelectedItems() },
                { error ->
                    contentDelegate.onError(error.message ?: return@subscribe)
                }
            ).storeIn(disposer)
    }

    private fun performSelectBySearch(item: ITEM) {
        selectionInteractor.selectBySearch(item.id)
            .subscribe(
                { searchVM.cancelSearch() },
                { error ->
                    searchVM.cancelSearch()
                    contentDelegate.onError(error.message ?: return@subscribe)
                }
            ).storeIn(disposer)
    }

    private fun performReplaceSelected(item: ITEM) {
        selectionInteractor.replaceSelected(item.id)
            .subscribe { updateSelectedItems() }
            .storeIn(disposer)
    }

    private fun performReplaceSelectedBySearch(item: ITEM) {
        selectionInteractor.replaceSelectedBySearch(item.id)
            .doOnTerminate { searchVM.cancelSearch() }
            .subscribe()
            .storeIn(disposer)
    }

    private fun performComplete(item: ITEM, appended: Boolean) {
        if (rulesHelper.isFinalComplete) {
            selectionInteractor.complete(item.id)
                .subscribe { items ->
                    val result = SelectionComponentResult(items, appended)
                    contentDelegate.complete(result)
                }.storeIn(disposer)
        } else {
            if (searchVM.searchQuery.isNotEmpty()) {
                searchVM.cancelSearch()
            }
            val result = SelectionComponentResult(listOf(item), appended)
            contentDelegate.complete(result)
        }
    }

    override fun unselect(item: ITEM, animate: Boolean) {
        SelectionLogger.onUnselect(item.id)
        selectionInteractor.unselect(item.id)
            .doOnSubscribe { selectionHelper.onUnselect() }
            .subscribe { updateSelectedItems(isUserSelection = animate) }
            .storeIn(disposer)
    }

    override fun onFolderTitleClicked() {
        router?.back()
    }

    override fun setRouter(router: SelectionRouter?) {
        this.router = router
    }

    override fun onContentVisibilityChanged(isVisible: Boolean) {
        val oldValue = isContentVisible
        isContentVisible = isVisible
        if (!oldValue && isVisible) {
            contentDelegate.searchQuery.tryEmit(searchVM.searchQuery)
        }
    }

    override fun reset() {
        subscribeOnFilterChanges()
        listComponent.reset(filterMeta)
    }

    private fun onCompleteClicked() {
        disposer += selectionInteractor.getAllSelectedItems()
            .subscribe { items ->
                val result = SelectionComponentResult(items)
                contentDelegate.complete(result)
            }
    }

    private fun updateSelectedItems(isUserSelection: Boolean = true) {
        updateSelectedItemsSubject.onNext(isUserSelection)
    }

    private fun onSearchQueryChanged(query: String) {
        if (isContentVisible) {
            contentDelegate.searchQuery.tryEmit(query)
        }
        filterMeta = filterMeta.copy(query = query)
        listComponent.reset(filterMeta)
    }

    private fun onSearchQueryChangedByOuterDelegate(query: String) {
        if (isContentVisible) {
            searchVM.setSearchText(query)
        }
    }

    private fun subscribeOnOuterSearchQuery() {
        viewModelScope.launch(mainDispatcher) {
            val dropCount = if (isRootContent) 0 else 1
            if (!isRootContent) {
                contentDelegate.searchQuery.tryEmit(searchVM.searchQuery)
            }

            contentDelegate.searchQuery.drop(dropCount).collect { query ->
                onSearchQueryChangedByOuterDelegate(query)
            }
        }
    }

    private fun subscribeOnFilterChanges() {
        selectionInteractor.subscribeOnFilterChanges()
            .subscribe { updateSelectedItems(isUserSelection = false) }
            .storeIn(filterChangesDisposable)
    }

    private fun onItemClicked(item: ITEM, type: ClickType) {
        when (type) {
            ClickType.ADD -> {
                SelectionStatisticUtil.sendStatistic(
                    SelectionStatisticEvent(config.useCase.name, CLICK_ON_ADD_BUTTON.value)
                )
                select(item, append = true)
            }
            ClickType.CLICK -> {
                SelectionStatisticUtil.sendStatistic(
                    SelectionStatisticEvent(config.useCase.name, CLICK_ON_ITEM.value)
                )
                select(item, append = false)
            }
            ClickType.LONG_CLICK -> Unit
            ClickType.NAVIGATE -> searchVM.hideKeyboard()
        }
    }

    override fun onCleared() {
        disposer.dispose()
    }
}

private const val UPDATE_SELECTED_ITEMS_THROTTLE_MS = 300L
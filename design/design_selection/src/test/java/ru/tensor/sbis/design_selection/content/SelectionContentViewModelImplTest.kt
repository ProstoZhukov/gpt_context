package ru.tensor.sbis.design_selection.content

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.subjects.PublishSubject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import ru.tensor.sbis.common.testing.TrampolineSchedulerRule
import ru.tensor.sbis.communication_decl.selection.SelectionConfig
import ru.tensor.sbis.communication_decl.selection.SelectionItemId
import ru.tensor.sbis.communication_decl.selection.recipient.RecipientSelectionUseCase
import ru.tensor.sbis.design_selection.contract.customization.selected.panel.SelectedData
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
import ru.tensor.sbis.design_selection.ui.content.vm.SelectionContentViewModelImpl
import ru.tensor.sbis.design_selection.ui.content.vm.search.SelectionSearchViewModel
import ru.tensor.sbis.design_selection.ui.main.router.SelectionRouter
import ru.tensor.sbis.design_selection.ui.main.vm.contract.SelectionContentDelegate

/**
 * Тесты вью-модели области контента компонента выбора [SelectionContentViewModelImpl].
 *
 * @author vv.chekurda
 */
@RunWith(MockitoJUnitRunner.StrictStubs::class)
@ExperimentalCoroutinesApi
class SelectionContentViewModelImplTest {

    @get:Rule
    val rule = TrampolineSchedulerRule()

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var contentDelegate: SelectionContentDelegate<SelectionItem>
    private lateinit var selectionInteractor: SelectionInteractor<SelectionItem>
    private lateinit var selectionHelper: SelectionStrategyHelper<SelectionItem>
    private lateinit var searchVM: SelectionSearchViewModel
    private lateinit var listComponent: SelectionListComponent

    private lateinit var viewModel: SelectionContentViewModelImpl<SelectionItem>

    @Test
    fun `When call select, then take strategy from selection helper`() {
        prepareViewModel()
        whenever(selectionInteractor.select(any())).thenReturn(Completable.never())
        whenever(selectionHelper.produceSelectStrategy(any(), any())).thenReturn(SelectionStrategy.SELECT)
        val selectedItem = mock<SelectionItem> {
            on { id } doReturn mock()
        }

        viewModel.select(item = selectedItem, append = false)

        verify(selectionHelper).produceSelectStrategy(selectedItem, false)
    }

    @Test
    fun `When select strategy is SELECT, then perform select`() {
        prepareViewModel()
        whenever(selectionInteractor.select(any())).thenReturn(Completable.never())
        whenever(selectionHelper.produceSelectStrategy(any(), any())).thenReturn(SelectionStrategy.SELECT)
        val itemId: SelectionItemId = mock()
        val selectedItem = mock<SelectionItem> {
            on { id } doReturn itemId
        }

        viewModel.select(item = selectedItem, append = false)

        verify(selectionInteractor).select(itemId)
    }

    @Test
    fun `When select return success, then update selected items by user`() {
        val updateSelectedItemsSubject = PublishSubject.create<Boolean>()
        prepareViewModel(updateSelectedItemsSubject = updateSelectedItemsSubject)
        val selectedItems = mock<SelectedData<SelectionItem>>()
        whenever(selectionInteractor.select(any())).thenReturn(Completable.complete())
        whenever(selectionInteractor.getSelectedData(true)).thenReturn(Single.just(selectedItems))
        whenever(selectionHelper.produceSelectStrategy(any(), any())).thenReturn(SelectionStrategy.SELECT)
        val selectedItem = mock<SelectionItem> {
            on { id } doReturn mock()
        }

        val updateSelectedItemsObserver = updateSelectedItemsSubject.test()
        viewModel.select(item = selectedItem, append = false)

        verify(selectionInteractor).select(any())
        updateSelectedItemsObserver.awaitCount(1)
            .assertValueCount(1)
            .assertValueAt(0, true)
    }

    @Test
    fun `When select return error, then notify content delegate`() {
        prepareViewModel()
        val errorMessage = "123"
        whenever(selectionInteractor.select(any())).thenReturn(Completable.error(Exception(errorMessage)))
        whenever(selectionHelper.produceSelectStrategy(any(), any())).thenReturn(SelectionStrategy.SELECT)
        val selectedItem = mock<SelectionItem> {
            on { id } doReturn mock()
        }

        viewModel.select(item = selectedItem, append = false)

        verify(selectionInteractor).select(any())
        verify(contentDelegate).onError(errorMessage)
    }

    @Test
    fun `When select strategy is SELECT_AND_CANCEL_SEARCH, then perform selectBySearch`() {
        prepareViewModel()
        whenever(selectionInteractor.selectBySearch(any())).thenReturn(Completable.never())
        whenever(selectionHelper.produceSelectStrategy(any(), any()))
            .thenReturn(SelectionStrategy.SELECT_AND_CANCEL_SEARCH)
        val itemId: SelectionItemId = mock()
        val selectedItem = mock<SelectionItem> {
            on { id } doReturn itemId
        }

        viewModel.select(item = selectedItem, append = false)

        verify(selectionInteractor).selectBySearch(itemId)
    }

    @Test
    fun `When selectBySearch return success, then cancel search`() {
        prepareViewModel()
        whenever(selectionInteractor.selectBySearch(any())).thenReturn(Completable.complete())
        whenever(selectionHelper.produceSelectStrategy(any(), any()))
            .thenReturn(SelectionStrategy.SELECT_AND_CANCEL_SEARCH)
        val selectedItem = mock<SelectionItem> {
            on { id } doReturn mock()
        }

        viewModel.select(item = selectedItem, append = false)

        verify(selectionInteractor).selectBySearch(any())
        verify(searchVM).cancelSearch()
    }

    @Test
    fun `When selectBySearch return error, then cancel search and notify content delegate`() {
        prepareViewModel()
        val errorMessage = "123"
        whenever(selectionInteractor.selectBySearch(any())).thenReturn(Completable.error(Exception(errorMessage)))
        whenever(selectionHelper.produceSelectStrategy(any(), any()))
            .thenReturn(SelectionStrategy.SELECT_AND_CANCEL_SEARCH)
        val selectedItem = mock<SelectionItem> {
            on { id } doReturn mock()
        }

        viewModel.select(item = selectedItem, append = false)

        verify(selectionInteractor).selectBySearch(any())
        verify(searchVM).cancelSearch()
        verify(contentDelegate).onError(errorMessage)
    }

    @Test
    fun `When select strategy is REPLACE_SELECTED, then perform replace selected`() {
        prepareViewModel()
        whenever(selectionInteractor.replaceSelected(any())).thenReturn(Completable.never())
        whenever(selectionHelper.produceSelectStrategy(any(), any())).thenReturn(SelectionStrategy.REPLACE_SELECTED)
        val itemId: SelectionItemId = mock()
        val selectedItem = mock<SelectionItem> {
            on { id } doReturn itemId
        }

        viewModel.select(item = selectedItem, append = false)

        verify(selectionInteractor).replaceSelected(itemId)
    }

    @Test
    fun `When replaceSelected return success, then update selected items by user`() {
        val updateSelectedItemsSubject = PublishSubject.create<Boolean>()
        prepareViewModel(updateSelectedItemsSubject = updateSelectedItemsSubject)
        whenever(selectionInteractor.replaceSelected(any())).thenReturn(Completable.complete())
        whenever(selectionHelper.produceSelectStrategy(any(), any())).thenReturn(SelectionStrategy.REPLACE_SELECTED)
        val selectedItem = mock<SelectionItem> {
            on { id } doReturn mock()
        }

        val updateSelectedItemsObserver = updateSelectedItemsSubject.test()
        viewModel.select(item = selectedItem, append = false)

        verify(selectionInteractor).replaceSelected(any())
        updateSelectedItemsObserver.awaitCount(1)
            .assertValueCount(1)
            .assertValueAt(0, true)
    }

    @Test
    fun `When select strategy is COMPLETE, then perform complete`() {
        prepareViewModel()
        whenever(selectionInteractor.complete(any())).thenReturn(Single.never())
        whenever(selectionHelper.produceSelectStrategy(any(), any())).thenReturn(SelectionStrategy.COMPLETE)
        val itemId: SelectionItemId = mock()
        val selectedItem = mock<SelectionItem> {
            on { id } doReturn itemId
        }

        viewModel.select(item = selectedItem, append = false)

        verify(selectionInteractor).complete(itemId)
    }

    @Test
    fun `When complete return success, then notify content delegate`() {
        prepareViewModel()
        val interactorCompleteResult = mock<List<SelectionItem>>()
        val delegateResult = SelectionComponentResult(interactorCompleteResult)
        whenever(selectionInteractor.complete(any())).thenReturn(Single.just(interactorCompleteResult))
        whenever(selectionHelper.produceSelectStrategy(any(), any())).thenReturn(SelectionStrategy.COMPLETE)
        val selectedItem = mock<SelectionItem> {
            on { id } doReturn mock()
        }

        viewModel.select(item = selectedItem, append = false)

        verify(selectionInteractor).complete(any())
        verify(contentDelegate).complete(delegateResult)
    }

    @Test
    fun `When select strategy is OPEN_FOLDER, then hide keyboard and open folder`() {
        prepareViewModel()
        val router = mock<SelectionRouter>()
        viewModel.setRouter(router)
        whenever(selectionHelper.produceSelectStrategy(any(), any())).thenReturn(SelectionStrategy.OPEN_FOLDER)
        val selectedFolder = mock<SelectionFolderItem>()

        viewModel.select(item = selectedFolder, append = false)

        verify(searchVM).hideKeyboard()
        verify(router).openFolder(selectedFolder)
    }

    @Test
    fun `When on item clicked, then produce select strategy with append false`() {
        val clickSubject = PublishSubject.create<Pair<SelectionItem, ClickType>>()
        val clickListener = mock<SelectionItemClickListener<SelectionItem>> {
            on { clickEvent } doReturn clickSubject
        }
        val clickedItem = mock<SelectionItem> {
            on { id } doReturn mock()
        }
        prepareViewModel(clickListener = clickListener)
        whenever(selectionHelper.produceSelectStrategy(any(), any())).thenReturn(SelectionStrategy.COMPLETE)
        whenever(selectionInteractor.complete(any())).thenReturn(Single.never())

        clickSubject.onNext(clickedItem to ClickType.CLICK)

        verify(selectionHelper).produceSelectStrategy(clickedItem, append = false)
    }

    @Test
    fun `When on item add, then produce select strategy with append true`() {
        val clickSubject = PublishSubject.create<Pair<SelectionItem, ClickType>>()
        val clickListener = mock<SelectionItemClickListener<SelectionItem>> {
            on { clickEvent } doReturn clickSubject
        }
        val clickedItem = mock<SelectionItem> {
            on { id } doReturn mock()
        }
        prepareViewModel(clickListener = clickListener)
        whenever(selectionHelper.produceSelectStrategy(any(), any())).thenReturn(SelectionStrategy.COMPLETE)
        whenever(selectionInteractor.complete(any())).thenReturn(Single.never())

        clickSubject.onNext(clickedItem to ClickType.ADD)

        verify(selectionHelper).produceSelectStrategy(clickedItem, append = true)
    }

    @Test
    fun `When create ViewModel, then subscribe on filter changes`() {
        prepareViewModel()

        verify(selectionInteractor).subscribeOnFilterChanges()
    }

    @Test
    fun `When create ViewModel as root content, then update selected items`() {
        val selectionInteractor = mockInteractor()
        val selectedData = SelectedData(items = listOf(mock()))
        whenever(selectionInteractor.getSelectedData(false)).thenReturn(Single.just(selectedData))
        prepareViewModel(folderItem = null, selectionInteractor = selectionInteractor)

        verify(selectionInteractor).getSelectedData(false)
        verify(contentDelegate).setSelectedData(selectedData)
    }

    @Test
    fun `When create ViewModel as folder content, then update selected items`() {
        val selectionInteractor = mockInteractor()
        val selectedData = SelectedData(items = listOf(mock()))
        whenever(selectionInteractor.getSelectedData(false)).thenReturn(Single.just(selectedData))
        prepareViewModel(folderItem = mock(), selectionInteractor = selectionInteractor)

        verify(selectionInteractor).getSelectedData(false)
        verify(contentDelegate).setSelectedData(selectedData)
    }

    @Test
    fun `When filter is changed, then update selected items`() {
        val selectionInteractor = mockInteractor()
        val filterChangedSubject = PublishSubject.create<Unit>()
        val updateSelectedItemsSubject = PublishSubject.create<Boolean>()
        val selectedData = SelectedData(items = listOf(mock()))
        whenever(selectionInteractor.subscribeOnFilterChanges()).thenReturn(filterChangedSubject)
        whenever(selectionInteractor.getSelectedData(false)).thenReturn(Single.just(selectedData))
        prepareViewModel(
            selectionInteractor = selectionInteractor,
            updateSelectedItemsSubject = updateSelectedItemsSubject
        )

        val updateSelectedObserver = updateSelectedItemsSubject.test()
        filterChangedSubject.onNext(Unit)

        updateSelectedObserver.awaitCount(1)
            .assertValueCount(1)
            .assertValueAt(0, false)
    }

    @Test
    fun `When on search query changed, then reset list component with new query`() {
        val searchQuerySubject = PublishSubject.create<String>()
        val searchVM = mock<SelectionSearchViewModel> {
            on { searchQueryObservable } doReturn searchQuerySubject
        }
        prepareViewModel(searchVM = searchVM)
        val filterMeta = SelectionFilterMeta<SelectionItemId>(query = "123")

        searchQuerySubject.onNext(filterMeta.query)

        verify(listComponent).reset(filterMeta)
    }

    @Test
    fun `When on folder title clicked, then perform back`() {
        prepareViewModel()
        val router = mock<SelectionRouter>()
        viewModel.setRouter(router)

        viewModel.onFolderTitleClicked()

        verify(router).back()
    }

    @Test
    fun `When on done button clicked, then get all selected items and notify delegate`() {
        val contentDelegate = mockContentDelegate()

        val doneButtonClickedSubject = PublishSubject.create<Unit>()
        whenever(contentDelegate.onDoneButtonClickedObservable).thenReturn(doneButtonClickedSubject)
        prepareViewModel(contentDelegate = contentDelegate)
        val allSelectedItems = mock<List<SelectionItem>>()
        val delegateResult = SelectionComponentResult(allSelectedItems)
        whenever(selectionInteractor.getAllSelectedItems()).thenReturn(Single.just(allSelectedItems))

        doneButtonClickedSubject.onNext(Unit)

        verify(selectionInteractor).getAllSelectedItems()
        verify(contentDelegate).complete(delegateResult)
    }

    private fun prepareViewModel(
        contentDelegate: SelectionContentDelegate<SelectionItem> = mockContentDelegate(),
        selectionInteractor: SelectionInteractor<SelectionItem> = mockInteractor(),
        clickListener: SelectionItemClickListener<SelectionItem> = mockClickListener(),
        searchVM: SelectionSearchViewModel = mockSearchVM(),
        folderItem: SelectionFolderItem? = null,
        updateSelectedItemsSubject: PublishSubject<Boolean> = PublishSubject.create()
    ) {
        this.contentDelegate = contentDelegate
        this.selectionInteractor = selectionInteractor
        this.searchVM = searchVM
        selectionHelper = mock()
        listComponent = mock()
        viewModel = SelectionContentViewModelImpl(
            contentDelegate = contentDelegate,
            searchVM = searchVM,
            selectionInteractor = selectionInteractor,
            listComponent = listComponent,
            selectionHelper = selectionHelper,
            rulesHelper = mock { on { isFinalComplete } doReturn true },
            clickListener = clickListener,
            folderItem = folderItem,
            updateSelectedItemsSubject = updateSelectedItemsSubject,
            config = mockConfig(),
            mainDispatcher = testDispatcher
        )
    }

    private fun mockContentDelegate(): SelectionContentDelegate<SelectionItem> =
        mock {
            on { onDoneButtonClickedObservable } doReturn Observable.never()
            on { result } doReturn Observable.never()
            on { clearSelectedObservable } doReturn Observable.never()
            on { searchQuery } doReturn MutableStateFlow("")
        }

    private fun mockInteractor(): SelectionInteractor<SelectionItem> =
        mock {
            on { subscribeOnFilterChanges() } doReturn Observable.never()
            on { getSelectedData(any()) } doReturn Single.never()
        }

    private fun mockClickListener(): SelectionItemClickListener<SelectionItem> =
        mock {
            on { clickEvent } doReturn PublishSubject.create()
        }

    private fun mockSearchVM(): SelectionSearchViewModel =
        mock {
            on { searchQueryObservable } doReturn Observable.never()
        }

    private fun mockConfig(): SelectionConfig =
        mock {
            on { useCase } doReturn RecipientSelectionUseCase.Base
        }
}
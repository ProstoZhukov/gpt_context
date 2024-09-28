package ru.tensor.sbis.business.common.ui.viewmodel

import org.mockito.kotlin.*
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers
import ru.tensor.sbis.business.common.domain.NetworkAssistant
import ru.tensor.sbis.business.common.domain.PopupNotificationHelper
import ru.tensor.sbis.business.common.domain.ToastHelper
import ru.tensor.sbis.business.common.domain.filter.impl.search.SearchPageListFilterImpl
import ru.tensor.sbis.business.common.domain.interactor.RequestInteractor
import ru.tensor.sbis.business.common.domain.interactor.SearchInteractor
import ru.tensor.sbis.business.common.domain.result.PayloadPagedListResult
import ru.tensor.sbis.business.common.testUtils.TestCppFilter
import ru.tensor.sbis.business.common.testUtils.TestData
import ru.tensor.sbis.business.common.ui.base.Error
import ru.tensor.sbis.business.common.ui.base.PagingScrollHelper
import ru.tensor.sbis.business.common.ui.base.state_vm.DisplayedErrors
import ru.tensor.sbis.common.testing.TrampolineSchedulerRule

class PagedListWithSearchVMTest {
    @get:Rule
    @Suppress("RedundantVisibilityModifier")
    public var rule = TrampolineSchedulerRule()

    //region filter
    private val cppFilter = TestCppFilter()
    private val mockFilter = mock<SearchPageListFilterImpl<TestCppFilter>> {
        on { asSearchFilter } doAnswer { testShouldSearch }
        on { searchQuery } doReturn "Сбер"
        on { build() } doReturn cppFilter
    }
    //region

    private val mockPagingHelper = mock<PagingScrollHelper> {
        on { observePaging(anyOrNull()) } doAnswer { Observable.empty() }
    }
    private val mockNetworkAssistant = mock<NetworkAssistant> {
        on { addOnConnectAction(any(), anyOrNull()) } doReturn Observable.never()
        on { isConnected } doReturn true
    }
    private val mockToastHelper = mock<ToastHelper>()
    private val mockPopupNotificationHelper = mock<PopupNotificationHelper>()

    //region interactor
    private var dataSubject = PublishSubject.create<PayloadPagedListResult<TestData, TestData>>()
    private val dataObservable: Observable<Result<PayloadPagedListResult<TestData, TestData>>>
        get() = dataSubject.map(Result.Companion::success).onErrorReturn(Result.Companion::failure)
    private val mockInteractor =
        mock<SearchInteractor<PayloadPagedListResult<TestData, TestData>, SearchPageListFilterImpl<TestCppFilter>>> {
            on { requestData(any()) } doAnswer { dataObservable }
            on { searchData(mockFilter.searchQuery) } doReturn dataObservable
        }
    //endregion interactor

    private lateinit var viewModel: PagedListWithSearchVM<*, *, *>
    private var testShouldSearch = false

    @Before
    fun setUp() {
        viewModel = TestPagedListWithSearchVM(
            filter = mockFilter, interactor = mockInteractor, searchVM = mock()
        ).apply {
            networkAssistant = mockNetworkAssistant
            pagingHelper = mockPagingHelper
            toastHelper = mockToastHelper
            popupNotificationHelper = mockPopupNotificationHelper
        }
    }

    @Test
    fun `Add exclusion to toast helper on initialization`() {
        viewModel.initialize()

        verify(mockToastHelper).addExclusion(Error.NoDataReceivedError::class.java)
    }

    @Test
    fun `Add exclusion to popup notification helper on initialization`() {
        viewModel.initialize()

        verify(mockPopupNotificationHelper).addExclusion(Error.NoDataReceivedError::class.java)
    }

    @Test
    fun `Set interactor subscription and requests data from cache`() {
        viewModel.initialize()

        verify(mockInteractor, only()).requestData(UpdateCause.INITIAL_REFRESH)
        assertTrue(dataSubject.hasObservers())
    }

    @Test
    fun `During initialization, sets interactor subscription and requests data`() {
        viewModel.initialize()

        verify(mockInteractor, only()).requestData(UpdateCause.INITIAL_REFRESH)
        assertTrue(dataSubject.hasObservers())
    }

    @Test
    fun `Invoke search call when filter contains search query`() {
        viewModel.initialize()

        testShouldSearch = true
        viewModel.updateState(false)

        assertTrue(viewModel.deferredFirstSearchPageProcessor)
        verify(mockPagingHelper).relieve(anyOrNull())
        verify(mockFilter, atLeastOnce()).reset()
    }

    @Test
    fun `Just update if there is no search string`() {
        viewModel.initialize()

        testShouldSearch = false
        viewModel.updateState(false)

        verify(mockFilter).reset()
        verify(mockPagingHelper).relieve(ArgumentMatchers.anyString())
    }
}

private class TestPagedListWithSearchVM(
    filter: SearchPageListFilterImpl<TestCppFilter>,
    interactor: RequestInteractor<PayloadPagedListResult<TestData, TestData>, SearchPageListFilterImpl<TestCppFilter>>,
    searchVM: FilterSearchPanelVM
) : PagedListWithSearchVM<SearchPageListFilterImpl<TestCppFilter>, TestData, TestData>(
    filter, interactor, searchVM
) {
    override val errorVmProvider = object : DisplayedErrors {}
    override fun transformListDataToViewModelList(listData: List<TestData>) =
        listData.map { it.toBaseObservableVM() }
}
package ru.tensor.sbis.business.common.ui.viewmodel

import androidx.databinding.BaseObservable
import org.mockito.kotlin.*
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import ru.tensor.sbis.base_components.adapter.vmadapter.LoadMoreVM
import ru.tensor.sbis.business.common.domain.NetworkAssistant
import ru.tensor.sbis.business.common.domain.PopupNotificationHelper
import ru.tensor.sbis.business.common.domain.ToastHelper
import ru.tensor.sbis.business.common.domain.filter.impl.PageListFilterImpl
import ru.tensor.sbis.business.common.domain.interactor.RequestInteractor
import ru.tensor.sbis.business.common.domain.result.PayloadPagedListResult
import ru.tensor.sbis.business.common.testUtils.TestCppFilter
import ru.tensor.sbis.business.common.testUtils.TestData
import ru.tensor.sbis.business.common.testUtils.TestDisplayedErrors
import ru.tensor.sbis.business.common.testUtils.assertType
import ru.tensor.sbis.business.common.ui.base.Error
import ru.tensor.sbis.business.common.ui.base.PagingScrollHelper
import ru.tensor.sbis.business.common.ui.base.state_vm.InformationVM
import ru.tensor.sbis.business.common.ui.base.state_vm.UiErrorType
import ru.tensor.sbis.business.common.ui.utils.*
import ru.tensor.sbis.business.common.ui.viewmodel.UpdateCause.*
import ru.tensor.sbis.common.testing.TrampolineSchedulerRule

class PagedListVMTest {

    @get:Rule
    @Suppress("RedundantVisibilityModifier")
    public var rule = TrampolineSchedulerRule()

    private val mockFilter = mock<PageListFilterImpl<TestCppFilter>> {
        on { hasCertainFilter } doAnswer { testHasCertainFilter }
        on { isFirstPageToSync } doAnswer { testIsFirstPageSync }
        on { isFirstPageOnlySynced } doAnswer { testIsFirstPageSync }
        on { pageNumber } doAnswer { testFilterPage }
    }

    //region interactor
    private var dataEmitter = PublishSubject.create<PayloadPagedListResult<TestData, TestData>>()
    private val mockInteractor =
        mock<RequestInteractor<PayloadPagedListResult<TestData, TestData>, PageListFilterImpl<TestCppFilter>>> {
            on { requestData(any()) } doAnswer {
                if (dataEmitter.hasObservers() || dataEmitter.hasComplete()) {
                    dataEmitter.onComplete()
                    dataEmitter = PublishSubject.create()
                }
                dataEmitter.map(Result.Companion::success).onErrorReturn(Result.Companion::failure)
            }
        }
    //endregion interactor

    private val pagingSubject = PublishSubject.create<Unit>()
    private val mockPagingHelper = mock<PagingScrollHelper> {
        on { observePaging(anyOrNull()) } doReturn pagingSubject
    }
    private val mockToastHelper = mock<ToastHelper>()
    private val mockPopupNotificationHelper = mock<PopupNotificationHelper>()
    private val mockNetworkAssistant = mock<NetworkAssistant> {
        on { addOnConnectAction(any(), anyOrNull()) } doReturn Observable.never()
        on { isConnected } doAnswer { testIsConnected }
        on { isDisconnected } doAnswer { testIsConnected.not() }
    }

    private var testNoPermission = true
    private var testIsConnected = true
    private var testIsFirstPageSync = true
    private var testFilterPage = 0
    private var testHasCertainFilter = false
    private var testAloneItemCallMarker = false

    private lateinit var viewModel: PagedListVM<*, *, *>

    @Before
    fun setUp() {
        viewModel = TestPagedListVM(
            filter = mockFilter, interactor = mockInteractor
        ).apply {
            networkAssistant = mockNetworkAssistant
            pagingHelper = mockPagingHelper
            toastHelper = mockToastHelper
            popupNotificationHelper = mockPopupNotificationHelper
        }
    }

    @After
    fun tearDown() {
        testIsConnected = true
        testHasCertainFilter = false
        testNoPermission = true
        testIsFirstPageSync = true
        testFilterPage = 0
        testAloneItemCallMarker = false
    }

    @Test
    fun `During initialization, sets interactor subscription and requests data`() {
        viewModel.initialize()

        verify(mockInteractor, only()).requestData(INITIAL_REFRESH)
        assertTrue(dataEmitter.hasObservers())
    }

    @Test
    fun `During initialization, don't requests data if fetchDataOnInit is false`() {
        val viewModel = TestPagedListVMWithoutLoadOnStart(mockFilter, mockInteractor).apply {
            networkAssistant = mockNetworkAssistant
            pagingHelper = mockPagingHelper
            toastHelper = mockToastHelper
            popupNotificationHelper = mockPopupNotificationHelper
        }

        viewModel.initialize()

        verify(mockInteractor, never()).requestData(any())
    }

    @Test
    fun `During initialization, sets interactor subscription and read data from cache if NO connection`() {
        testIsConnected = false
        viewModel.initialize()

        verify(mockInteractor, only()).requestData(REQUEST_FROM_CACHE)
        assertTrue(dataEmitter.hasObservers())
    }

    @Test
    fun `During initialization add default exclusions to toasts`() {
        viewModel.initialize()

        verify(mockToastHelper).addExclusion(anyOrNull())
    }

    @Test
    fun `During initialization add default exclusions to popup notifications`() {
        viewModel.initialize()

        verify(mockPopupNotificationHelper).addExclusion(anyOrNull())
    }

    @Test
    fun `Update action affect only when viewmodel has already initialized`() {
        viewModel.updateState()

        verify(mockInteractor, never()).requestData(any())
    }

    @Test
    fun `On update state reset pagination state and requests data`() {
        viewModel.initialize()
        viewModel.updateState()

        verify(mockPagingHelper).relieve(anyOrNull())
        verify(mockFilter).reset()
        verify(mockInteractor, times(1)).requestData(INITIAL_REFRESH)
        verify(mockInteractor, times(1)).requestData(PULL_TO_REFRESH)
    }

    @Test
    fun `Show BLANK stub when got empty data AFTER refresh`() {
        viewModel.initialize()

        dataEmitter.emit(formResult(size = 2, afterRefresh = false))
        //отображаем старые данные из кэша
        assert(viewModel.listVms.isNotNullOrEmpty())

        dataEmitter.emit(formResult(size = 0, afterRefresh = true))
        //скрываем данные и выводим ошибку если после синхронизации данных уже нет
        assert(viewModel.listVms.isNullOrEmpty())
        assertNotNull(viewModel.errorVm.get())
    }

    @Test
    fun `If getting result and current list are empty when NO internet connection show networkError stub`() {
        testIsConnected = false
        viewModel.initialize()

        dataEmitter.emit(formResult())

        assertNotNull(viewModel.errorVm)
        assertType<InformationVM>(viewModel.errorVm.get())
        val stub = viewModel.errorVm.get() as InformationVM
        assertTrue(stub.isType(UiErrorType.NETWORK_ERROR))
    }

    @Test
    fun `If getting result and current list are empty show noDataFound stub`() {
        testIsConnected = true
        viewModel.initialize()

        dataEmitter.emit(formResult(afterRefresh = true))

        assertNotNull(viewModel.errorVm)
        assertType<InformationVM>(viewModel.errorVm.get())
        val stub = viewModel.errorVm.get() as InformationVM
        assertTrue(stub.isType(UiErrorType.NO_DATA_FOUND))
    }

    @Test
    fun `If getting result is empty and list is empty when filter is Specified show noDataForFilter stub`() {
        testHasCertainFilter = true
        viewModel.initialize()

        dataEmitter.emit(formResult(afterRefresh = true))

        assertNotNull(viewModel.errorVm)
        assertType<InformationVM>(viewModel.errorVm.get())
        val stub = viewModel.errorVm.get() as InformationVM
        assertTrue(stub.isType(UiErrorType.NO_DATA_FOR_FILTER))
    }

    @Test
    fun `Call aloneItemListData processor if it enabled and received single item list`() {
        viewModel.initialize()
        dataEmitter.emit(formResult(1))

        assertTrue(testAloneItemCallMarker)
    }

    @Test
    fun `Don not call aloneItemListData processor if list size more than one`() {
        viewModel.initialize()
        dataEmitter.emit(formResult(2))

        assertFalse(testAloneItemCallMarker)
    }

    @Test
    fun `Should load data by filter change if internet is connected`() {
        val mockNetworkAssistant: NetworkAssistant = mock {
            on { isConnected } doReturn true
        }
        viewModel.initialize()
        viewModel.networkAssistant = mockNetworkAssistant
        viewModel.updateState(true)

        assertNull(viewModel.errorVm.get())
    }

    @Test
    fun `Should show error data by filter change if internet is disconnected`() {
        val mockNetworkAssistant: NetworkAssistant = mock {
            on { isConnected } doReturn false
        }
        viewModel.initialize()
        viewModel.networkAssistant = mockNetworkAssistant
        assertNull(viewModel.errorVm.get())

        viewModel.updateState(true, false)

        assertNotNull(viewModel.errorVm.get())
    }

    @Test
    fun `On received NO access RIGHNTS by SYNC, hide previously received data and interrupt processing result`() {
        viewModel.initialize()
        viewModel.updateState()

        dataEmitter.emit(formResult(10))
        assert(viewModel.listVms.isNotNullOrEmpty())

        dataEmitter.emit(
            formResult(
                size = 10, afterRefresh = true, error = Error.NoPermissionsError()
            )
        )
        assert(viewModel.listVms.isNullOrEmpty())
    }

    @Test
    fun `On received NO access RIGHNTS error, hide previously received data and show error`() {
        viewModel.initialize()
        // отображаем данные
        dataEmitter.emit(formResult(10))
        assert(viewModel.listVms.isNotNullOrEmpty())
        // скрываем данные если оказалось что права недоступны
        dataEmitter.emit(formResult(10, error = Error.NoPermissionsError()))
        assert(viewModel.listVms.isNullOrEmpty())
        // отображаем ошибку если она получена вне данных
        dataEmitter.onError(Error.NoPermissionsError())
        assertNotNull(viewModel.errorVm.get())
    }

    @Test
    fun `Show initial progress before data received, after receiving displays result and hides indicator`() {
        viewModel.initialize()

        assertFalse(viewModel.isRefreshing.get())
        assertNotNull(viewModel.progressInitVm.get())
        assert(viewModel.listVms.isNullOrEmpty())

        dataEmitter.emit(formResult(size = 2))

        assertFalse(viewModel.isRefreshing.get())
        assertNull(viewModel.progressInitVm.get())
        assert(viewModel.listVms.isNotNullOrEmpty())
        assertNull(viewModel.errorVm.get())
    }

    @Test
    fun `Show paging progress until data synced, cache has records or have caused error`() {
        viewModel.initialize()

        // первый разворот
        dataEmitter.emit(formResult(3), true)

        // следующий разворот еще не получен, удерживаем прогресс
        pagingSubject.onNext(Unit)
        assert(viewModel.listVms.contains(LoadMoreVM::class.java))
        dataEmitter.emit(formResult())
        assert(viewModel.listVms.contains(LoadMoreVM::class.java))

        // синхронизация разворота завершена
        dataEmitter.emit(formResult(size = 0, afterRefresh = true))
        assert(viewModel.listVms.notContains(LoadMoreVM::class.java))

        // следующий разворот - есть данные из кэша, НЕ удерживаем прогресс
        pagingSubject.onNext(Unit)
        dataEmitter.emit(formResult(size = 2))
        assert(viewModel.listVms.notContains(LoadMoreVM::class.java))

        // следующий разворот - ошибка, НЕ удерживаем прогресс
        pagingSubject.onNext(Unit)
        dataEmitter.onError(Error.UnknownError())
        assert(viewModel.listVms.notContains(LoadMoreVM::class.java))
    }

    @Test
    fun `If data received and more pages available, call PagingScrollHelper's callback`() {
        viewModel.initialize()

        dataEmitter.emit(formResult(2, true))
        pagingSubject.onNext(Unit)

        verify(mockInteractor).requestData(SCROLL_TO_REFRESH)
        verify(mockPagingHelper).relieve(anyOrNull())
    }

    @Test
    fun `If data NOT received and no more pages, block PagingScrollHelper's callback`() {
        viewModel.initialize()

        dataEmitter.emit(formResult(afterRefresh = true))
        pagingSubject.onNext(Unit)

        verify(mockPagingHelper).block(anyOrNull())
        verify(mockPagingHelper, never()).relieve(anyOrNull())
    }

    @Test
    fun `When pagination event is received, it requests next page, displays pagination progress indicator, after receiving data adds it to list and hides indicator`() {
        val firstPageSize = 3
        val secondPageSize = 2
        viewModel.initialize()

        val firstResult = formResult(firstPageSize, afterRefresh = true)
        dataEmitter.emit(firstResult, withPaging = true)
        pagingSubject.onNext(Unit)

        verify(mockInteractor).requestData(SCROLL_TO_REFRESH)
        assertEquals(firstPageSize, viewModel.listVms.count { it !is LoadMoreVM })

        val secondResult = formResult(secondPageSize)
        dataEmitter.emit(secondResult, withPaging = true)

        assertEquals(firstPageSize + secondPageSize, viewModel.listVms.count { it !is LoadMoreVM })
    }

    @Test
    fun `During forced update displays indicator of progress, after receiving displays result and hides indicator`() {
        viewModel.initialize()
        dataEmitter.emit(formResult(size = 2))
        viewModel.updateState()

        assertTrue(viewModel.isRefreshing.get())
        assertNull(viewModel.progressInitVm.get())
        assertTrue(viewModel.listVms.count { it is LoadMoreVM } == 0)

        // моделируем ошибку данных перед обновлением
        viewModel.errorVm.set(BaseObservable())
        dataEmitter.emit(formResult(size = 2))

        assertFalse(viewModel.isRefreshing.get())
        assertNull(viewModel.progressInitVm.get())
        assert(viewModel.listVms.isNotNullOrEmpty())
        assertNull(viewModel.errorVm.get())
    }

    //region utils methods
    /**
     * Вернуть данные от ранее подписанного интерактора
     *
     * @param result возвращаемый результат
     * @param withPaging обработать с пейджингом
     */
    private fun PublishSubject<PayloadPagedListResult<TestData, TestData>>.emit(
        result: PayloadPagedListResult<TestData, TestData>,
        withPaging: Boolean = false,
    ) {
        if (withPaging) {
            testIsFirstPageSync = false
            testFilterPage++
        }
        onNext(result)
    }

    private fun formResult(
        size: Int = 0,
        afterRefresh: Boolean = false,
        error: Throwable? = null,
    ): PayloadPagedListResult<TestData, TestData> {
        val list = mutableListOf<TestData>()
        repeat(size) {
            list.add(TestData())
        }
        return PayloadPagedListResult(
            list = list,
            extra = TestData(),
            hasMore = size > 0 || afterRefresh.not(),
            error = error,
            fromRefreshedCache = afterRefresh
        )
    }
    //endregion utils methods

    /**
     * Тестовый класс базовой VM списка с пагинацией
     */
    private open inner class TestPagedListVM(
        filter: PageListFilterImpl<TestCppFilter>,
        interactor: RequestInteractor<PayloadPagedListResult<TestData, TestData>, PageListFilterImpl<TestCppFilter>>,
    ) : PagedListVM<PageListFilterImpl<TestCppFilter>, TestData, TestData>(filter, interactor) {
        override fun processAloneItemListData(aloneItem: TestData): Boolean {
            testAloneItemCallMarker = true
            return true
        }

        override val errorVmProvider = TestDisplayedErrors
        override fun shouldProcessNoPermission() = testNoPermission
        override fun transformListDataToViewModelList(listData: List<TestData>) =
            listData.map { it.toBaseObservableVM() }
    }

    private inner class TestPagedListVMWithoutLoadOnStart(
        filter: PageListFilterImpl<TestCppFilter>,
        interactor: RequestInteractor<PayloadPagedListResult<TestData, TestData>, PageListFilterImpl<TestCppFilter>>,
    ) : TestPagedListVM(filter, interactor) {
        override val fetchDataOnInit = false
    }
}
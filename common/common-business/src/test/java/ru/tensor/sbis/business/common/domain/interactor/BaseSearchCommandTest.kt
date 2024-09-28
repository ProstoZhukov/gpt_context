package ru.tensor.sbis.business.common.domain.interactor

import org.mockito.kotlin.*
import io.reactivex.observers.TestObserver
import org.junit.Rule
import org.junit.Test
import ru.tensor.sbis.business.common.data.base.BaseCrudListRepository
import ru.tensor.sbis.business.common.domain.filter.impl.search.SearchPageListFilterImpl
import ru.tensor.sbis.business.common.domain.filter.navigation.PageNavigation
import ru.tensor.sbis.business.common.domain.interactor.impl.BaseListSearchInteractor
import ru.tensor.sbis.business.common.domain.result.PayloadPagedListResult
import ru.tensor.sbis.business.common.testUtils.TestData
import ru.tensor.sbis.business.common.ui.base.Error.NoInternetConnection
import ru.tensor.sbis.business.common.ui.base.Error.NoSearchDataError
import ru.tensor.sbis.common.modelmapper.BaseModelMapper
import ru.tensor.sbis.common.testing.TrampolineSchedulerRule
import ru.tensor.sbis.common.util.NetworkUtils

class BaseSearchCommandTest {

    @get:Rule
    @Suppress("RedundantVisibilityModifier")
    public var rule = TrampolineSchedulerRule()

    private companion object {
        const val CPP_FILTER_VALUE = "8000"
        const val TEST_RESULT_LIST_VALUE = "9000"
    }

    // region classes
    private data class TestCppFilter(val id: String)

    private class TestSearchPagedListFilter : SearchPageListFilterImpl<TestCppFilter>(mock()) {
        override fun getLastSyncNavigation(lastFilter: TestCppFilter): PageNavigation =
            PageNavigation()

        override fun innerBuild(): TestCppFilter = TestCppFilter(CPP_FILTER_VALUE)
    }

    private class TestCppList
    private data class TestResultList(val id: String = "") :
        PayloadPagedListResult<TestData, TestData>(mutableListOf(), null, false)

    private class TestListSearchInteractorImpl(
        filter: TestSearchPagedListFilter,
        repository: BaseCrudListRepository<*, *, TestCppList, TestCppFilter>,
        mapper: BaseModelMapper<TestCppList, TestResultList>,
        override var networkUtils: NetworkUtils,
        private val isDataEmpty: Boolean
    ) : BaseListSearchInteractor<TestCppList, TestResultList, TestCppFilter, TestSearchPagedListFilter>(
        filter = filter, repository = repository, mapper = mapper
    ) {
        override fun isSearchDataEmpty(cppResult: TestCppList?) = isDataEmpty
    }
    // endregion classes

    // region mock variables
    private val mockRepository: BaseCrudListRepository<*, *, TestCppList, TestCppFilter> = mock {
        on { search(any(), any()) } doReturn TestCppList()
    }
    private val mockMapper: BaseModelMapper<TestCppList, TestResultList> = mock {
        on { apply(any()) } doReturn TestResultList(TEST_RESULT_LIST_VALUE)
    }
    private val mockNetworkUtils: NetworkUtils = mock {
        on { isConnected } doReturn true
    }
    private val mockNetworkUtilsOffline: NetworkUtils = mock {
        on { isConnected } doReturn false
    }
    // endregion mock variables

    private fun getInteractor(
        filter: TestSearchPagedListFilter = TestSearchPagedListFilter(),
        networkUtils: NetworkUtils = mockNetworkUtils,
        isDataEmpty: Boolean = false
    ): BaseListSearchInteractor<TestCppList, TestResultList, TestCppFilter, TestSearchPagedListFilter> =
        TestListSearchInteractorImpl(filter, mockRepository, mockMapper, networkUtils, isDataEmpty)

    @Test
    fun `On search call interactor search()`() {
        val filter = TestSearchPagedListFilter().apply { searchQuery = "some search string" }

        getInteractor(filter).searchData(filter.searchQuery).subscribe()

        verify(mockRepository).search(eq(filter.searchQuery), eq(TestCppFilter(CPP_FILTER_VALUE)))
    }

    @Test
    fun `Get result on search`() {
        val testObserver = TestObserver<Result<TestResultList>>()
        val filter = TestSearchPagedListFilter().apply { searchQuery = "some str" }
        getInteractor(filter).searchData(filter.searchQuery).subscribe(testObserver)

        testObserver.assertValueCount(1)
        testObserver.assertValue { it.getOrNull() == TestResultList(TEST_RESULT_LIST_VALUE) }
    }

    @Test
    fun `On network error`() {
        val testObserver = TestObserver<Result<TestResultList>>()

        getInteractor(networkUtils = mockNetworkUtilsOffline).searchData("some str")
            .subscribe(testObserver)

        testObserver.assertValueCount(1)
        testObserver.assertNoErrors()
        testObserver.assertValue { it.exceptionOrNull() is NoInternetConnection }
    }

    @Test
    fun `On search receive empty result error`() {
        val testObserver = TestObserver<Result<TestResultList>>()

        getInteractor(isDataEmpty = true).searchData("some str").subscribe(testObserver)

        testObserver.assertValueCount(1)
        testObserver.assertNoErrors()
        testObserver.assertValue { it.exceptionOrNull() is NoSearchDataError }
    }
}

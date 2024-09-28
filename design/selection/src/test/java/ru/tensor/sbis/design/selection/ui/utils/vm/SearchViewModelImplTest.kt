package ru.tensor.sbis.design.selection.ui.utils.vm

import org.apache.commons.lang3.StringUtils
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

private const val MINIMAL_SEARCH_QUERY_LENGTH = 3

/**
 * @author ma.kolpakov
 */
@RunWith(MockitoJUnitRunner.StrictStubs::class)
class SearchViewModelImplTest {

    private val testQuery: String = "Test query"
    private val minQuery = testQuery.substring(0, MINIMAL_SEARCH_QUERY_LENGTH)
    private val lessThanMinQuery = minQuery.substring(0, minQuery.lastIndex - 1)

    private lateinit var vm: SearchViewModelImpl

    @Before
    fun setUp() {
        vm = SearchViewModelImpl(MINIMAL_SEARCH_QUERY_LENGTH, StringUtils.EMPTY)
    }

    @Test
    fun `While query is equal to DEFAULT_SEARCH_QUERY subscribers shouldn't receive it`() {
        val query = vm.searchText.test()

        vm.setSearchText(DEFAULT_SEARCH_QUERY)
        vm.setSearchText(DEFAULT_SEARCH_QUERY)

        query.assertNoValues()
    }

    @Test
    fun `When searchQuery get a subscriber, then it receive empty string by default`() {
        vm.searchQuery.test().assertValue(DEFAULT_SEARCH_QUERY)
    }

    //region Fix https://online.sbis.ru/opendoc.html?guid=dc509fa3-efe2-4317-bc61-2c5a804ce3f2
    @Test
    fun `When searchQuery subscription was disposed and query wasn't empty, then new observer should receive cancel events`() {
        val observer = vm.searchText.test()

        vm.setSearchText(testQuery)
        observer.dispose()

        // новая подписка должна получать все события, включая очистку строк
        val newObserver = vm.searchText.test()

        vm.cancelSearch()

        newObserver.assertValues(testQuery, DEFAULT_SEARCH_QUERY)
    }

    @Test
    fun `When minimalSearchQuery subscription was disposed and query wasn't empty, then new observer should receive cancel events`() {
        val observer = vm.searchQuery.test()

        vm.setSearchText(testQuery)
        observer.dispose()

        // новая подписка должна получать все события, включая очистку строк
        val newObserver = vm.searchQuery.test()

        vm.cancelSearch()

        newObserver.assertValues(testQuery, DEFAULT_SEARCH_QUERY)
    }
    //endregion

    /**
     * Панель поиска по умолчанию актуальную строку, на это не нужно реагировать т.к. прошлый запрос всё ещё в памяти
     */
    @Test
    fun `When view model receive the same query, then it shouldn't be delivered to subscribers`() {
        val query = vm.searchText.test()

        vm.setSearchText(testQuery)
        vm.setSearchText(testQuery)

        query.assertValue(testQuery)
    }

    @Test
    fun `When view model receive different queries, then all of them should be delivered to subscribers`() {
        val anotherTestQuery = "Another test query"
        val query = vm.searchText.test()

        vm.setSearchText(testQuery)
        vm.setSearchText(testQuery)
        vm.setSearchText(anotherTestQuery)

        query.assertValues(testQuery, anotherTestQuery)
    }

    @Test
    fun `When search query is cancelled, then keyboard should be closed`() {
        val keyboard = vm.hideKeyboardEvent.test()

        vm.cancelSearch()

        keyboard.assertValue(Unit)
    }

    //region Fix https://online.sbis.ru/opendoc.html?guid=2af11b6a-8b89-450a-9a56-488d7c33a2d4
    @Test
    fun `When search query length less than MINIMAL_SEARCH_QUERY_LENGTH, then it shouldn't be delivered to subscribers`() {
        val query = vm.searchQuery.skip(1 /* значение по умолчанию */).test()

        minQuery.fold("") { acc, c ->
            val result = acc + c
            vm.setSearchText(result)
            result
        }

        query.assertValue(minQuery)
    }

    @Test
    fun `When user delete symbol and search query length become less than MINIMAL_SEARCH_QUERY_LENGTH, then empty string should be delivered to subscribers`() {
        val query = vm.searchQuery.skip(1).test()

        vm.setSearchText(minQuery)
        vm.setSearchText(lessThanMinQuery)

        query.assertValues(minQuery, DEFAULT_SEARCH_QUERY)
    }
    //endregion

    //region Fix https://online.sbis.ru/opendoc.html?guid=07531671-9f10-4f8f-906e-4ef8eabd1d65
    @Test
    fun `When vm disabled, then search text should be ignored`() {
        val query = vm.searchQuery.test()

        vm.isEnabled = false
        vm.setSearchText(testQuery)

        query.assertValue(DEFAULT_SEARCH_QUERY)
    }

    @Test
    fun `When vm enabled, then new subscribers shouldn't receive skipped search text`() {
        vm.isEnabled = false
        vm.setSearchText(testQuery)
        vm.isEnabled = true

        val newObserver = vm.searchQuery.test()

        newObserver.assertValue(DEFAULT_SEARCH_QUERY)
    }
    //endregion
}
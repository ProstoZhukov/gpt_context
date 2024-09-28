package ru.tensor.sbis.business.common.domain.filter.impl

import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.mock
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import ru.tensor.sbis.business.common.data.HashFilterProvider
import ru.tensor.sbis.business.common.domain.filter.navigation.PageNavigation
import ru.tensor.sbis.common.testing.TrampolineSchedulerRule

class PageListFilterImplTest {

    @get:Rule
    @Suppress("RedundantVisibilityModifier")
    public var rule = TrampolineSchedulerRule()

    private val mockHashFilterProvider = mock<HashFilterProvider> {
        on { getHash<TestCppPageFilter>(any()) } doAnswer { invocationOnMock ->
            val cppFilter = invocationOnMock.arguments.first() as TestCppPageFilter
            cppFilter.getHash()
        }
    }

    private val token1 = 1
    private val token2 = 2

    private lateinit var filter: TestPageListFilter

    @Before
    fun setUp() {
        filter = TestPageListFilter(mockHashFilterProvider)
    }

    @Test
    fun `Correct state when first page TO sync`() {
        filter.apply {
            assert(isFirstPageToSync)

            build()
            incPage()
            assertFalse(isFirstPageToSync)
        }
    }

    @Test
    fun `Correct state when first page ONLY sync`() {
        filter.apply {
            assert(isFirstPageOnlySynced)

            build()
            assert(isFirstPageOnlySynced)
            incPage()
            assert(isFirstPageOnlySynced)

            build()
            assertFalse(isFirstPageOnlySynced)
        }
    }

    @Test
    fun `Increment filter page only if previous one was used`() {
        filter.apply {
            incPage()
            incPage()
            incPage()

            assert(pageNumber == 0)

            turnPage(3)

            assert(pageNumber == 3)
        }
    }

    @Test
    fun `Correct navigation state after pagination`() {
        filter.apply {
            turnPage(2)

            val cppFilter = build()
            assert(cppFilter.navigation.pageNumber == 2)
        }
    }

    @Test
    fun `Correct reset of filter navigation state`() {
        filter.apply {
            turnPage(3)

            assert(pageNumber > 0)

            reset()

            assert(pageNumber == 0)
        }
    }

    @Test
    fun `Correct cpp filter on build`() {
        var cppFilter: TestCppPageFilter
        filter.apply {
            token = token1
            cppFilter = build()
            incPage()

            assertEquals(cppFilter.token, token1)
            assertEquals(cppFilter.navigation.pageNumber, 0)

            token = token2
            cppFilter = build()

            assertEquals(cppFilter.token, token2)
            assertEquals(cppFilter.navigation.pageNumber, 1)
        }
    }

    @Test
    fun `Proper handling filter shownPage state`() {
        filter.apply {
            turnPage(3)
            build()

            assert(shownPage)

            incPage()

            assertFalse(shownPage)
        }
    }

    @Test
    fun `Return correct synced filter on build by hash`() {
        filter.apply {
            token = token1
            turnPage(2)

            val askedPageCppFilter = build()
            val hash = "${askedPageCppFilter.getHash()}"

            token = token2
            turnPage(2)

            val onRefreshCallbackPageCppFilter = build(hash)

            assertEquals(askedPageCppFilter.token, onRefreshCallbackPageCppFilter.token)
            assertEquals(askedPageCppFilter.getHash(), onRefreshCallbackPageCppFilter.getHash())
            assertEquals(askedPageCppFilter, onRefreshCallbackPageCppFilter)
        }
    }

    @Test
    fun `Build correct filter for refresh callback`() {
        filter.apply {
            token = token1
            turnPage(2)

            val askedPageCppFilter = build()

            token = token2
            incPage()

            val predictNextPageCppFilter = build(markAsUsed = false)
            val actualNextPageCppFilter = build()

            assertNotEquals(predictNextPageCppFilter.getHash(), askedPageCppFilter.getHash())
            assertNotEquals(predictNextPageCppFilter.token, askedPageCppFilter.token)
            assertNotEquals(predictNextPageCppFilter, askedPageCppFilter)

            assertEquals(predictNextPageCppFilter.getHash(), actualNextPageCppFilter.getHash())
            assertEquals(predictNextPageCppFilter.navigation, actualNextPageCppFilter.navigation)
        }
    }
}

private fun TestPageListFilter.turnPage(time: Int) {
    repeat(time) {
        build()
        incPage()
    }
}

private class TestPageListFilter(hashProvider: HashFilterProvider) :
    PageListFilterImpl<TestCppPageFilter>(hashProvider) {
    var token: Int = 0

    override fun getLastSyncNavigation(lastFilter: TestCppPageFilter) =
        lastFilter.navigation.run { PageNavigation(pageSize, pageNumber) }

    override fun innerBuild(): TestCppPageFilter {
        val navigation = getNavigation().run { TestCppPageFilter.CppNavigation(limit, pageNumber) }
        return TestCppPageFilter(token, navigation)
    }
}

private data class TestCppPageFilter(
    var token: Int = 0,
    var navigation: CppNavigation = CppNavigation()
) {
    fun getHash(): Int {
        var result = token.hashCode()
        result = 31 * result + navigation.pageNumber
        result = 31 * result + navigation.pageSize
        return result
    }

    data class CppNavigation(
        var pageSize: Int = 0,
        var pageNumber: Int = 0
    )
}
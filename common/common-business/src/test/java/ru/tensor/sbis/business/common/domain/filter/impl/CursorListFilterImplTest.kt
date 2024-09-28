package ru.tensor.sbis.business.common.domain.filter.impl

import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.mock
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import ru.tensor.sbis.business.common.data.HashFilterProvider
import ru.tensor.sbis.business.common.domain.filter.CursorBuilder
import ru.tensor.sbis.business.common.domain.filter.navigation.CursorNavigation
import ru.tensor.sbis.common.testing.TrampolineSchedulerRule

class CursorListFilterImplTest {

    @get:Rule
    @Suppress("RedundantVisibilityModifier")
    public var rule = TrampolineSchedulerRule()

    private val mockHashFilterProvider = mock<HashFilterProvider> {
        on { getHash<TestCppCursorFilter>(any()) } doAnswer { invocationOnMock ->
            val cppFilter = invocationOnMock.arguments.first() as TestCppCursorFilter
            cppFilter.getHash()
        }
    }

    private val token1 = 1
    private val token2 = 2

    private lateinit var filter: TestCursorListFilter

    @Before
    fun setUp() {
        filter = TestCursorListFilter(mockHashFilterProvider)
    }

    @Test
    fun `Correct filter state when first page is going TO sync`() {
        filter.apply {
            assert(isFirstPageToSync)

            build()
            incPosition(getCursorBuilder(positionId = 1L))
            assertFalse(isFirstPageToSync)
        }
    }

    @Test
    fun `Correct filter state when ONLY first page is synced`() {
        filter.apply {
            assert(isFirstPageOnlySynced)

            build()
            assert(isFirstPageOnlySynced)
            incPosition(getCursorBuilder(positionId = 1L))
            assert(isFirstPageOnlySynced)

            build()
            assertFalse(isFirstPageOnlySynced)
        }
    }

    @Test
    fun `Increment filter cursor only if previous one was used`() {
        filter.apply {
            incPosition(getCursorBuilder(positionId = 1L))
            incPosition(getCursorBuilder(positionId = 2L))
            incPosition(getCursorBuilder(positionId = 3L))

            assert(isFirstPageOnlySynced)
            assertNull(currentCursor)

            val newCursorIds = listOf(1L, 2L, 3L)
            turnPage(newCursorIds)

            assertFalse(isFirstPageOnlySynced)
            assert(currentCursor!!.id == 3L)
        }
    }

    @Test
    fun `Correct filter navigation state after pagination`() {
        filter.apply {
            val newCursorIds = listOf(1L, 2L)
            turnPage(newCursorIds)

            val cppFilter = build()
            assert(cppFilter.navigation.position?.id == 2L)
        }
    }

    @Test
    fun `Proper reset of filter navigation state`() {
        filter.apply {
            val newCursorIds = listOf(1L, 2L, 3L)
            turnPage(newCursorIds)

            assertFalse(isFirstPageOnlySynced)

            reset()

            assert(isFirstPageToSync)
        }
    }

    @Test
    fun `Received correct cpp filter on build`() {
        var cppFilter: TestCppCursorFilter
        filter.apply {
            token = token1
            cppFilter = build()
            incPosition(getCursorBuilder(positionId = 1L))

            assertEquals(cppFilter.token, token1)
            assertNull(cppFilter.navigation.position)

            token = token2
            cppFilter = build()

            assertEquals(cppFilter.token, token2)
            assertEquals(cppFilter.navigation.position?.id, 1L)
        }
    }

    @Test
    fun `Constant false for shownPage state of filter`() {
        filter.apply {
            val newCursorIds = listOf(1L, 2L, 3L)
            turnPage(newCursorIds)
            build()

            assertFalse(shownPage)

            incPosition(getCursorBuilder(positionId = 4L))

            assertFalse(shownPage)
        }
    }

    @Test
    fun `Return correct synced filter on build by hash`() {
        filter.apply {
            token = token1
            turnPage(listOf(1L, 2L))

            val askedPageCppFilter = build()
            val hash = "${askedPageCppFilter.getHash()}"

            token = token2
            turnPage(listOf(3L, 4L))

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
            turnPage(listOf(1L, 2L))

            // фильтр контроллера использованный для 2й пагинации
            val askedPageCppFilter = build()

            token = token2
            turnPage(listOf(3L))

            // "прогнозный" фильтр контроллера для 3й пагинации
            val predictNextPageCppFilter = build(markAsUsed = false)
            // "построенный" фильтр контроллера для 3й пагинации
            val actualNextPageCppFilter = build()

            // фильтры для последней синх и сл. синх. страниц НЕ равны
            assertNotEquals(predictNextPageCppFilter.getHash(), askedPageCppFilter.getHash())
            assertNotEquals(predictNextPageCppFilter.token, askedPageCppFilter.token)
            assertNotEquals(predictNextPageCppFilter, askedPageCppFilter)
            // прогнозный фильтр контроллера равен фактически построенному
            assertEquals(predictNextPageCppFilter.getHash(), actualNextPageCppFilter.getHash())
            assertEquals(predictNextPageCppFilter.navigation, actualNextPageCppFilter.navigation)
        }
    }

    @Test
    fun `Correct update next cursor position when it is different before and after refresh callback`() {
        filter.apply {
            var cppFilter = build()
            // проверяем навигацию для 0-го разворота
            assertNull(cppFilter.navigation.position)
            assertEquals(cppFilter.navigation.offset, 0)

            // сдвигаем курсор до и после синхронизации
            incPosition(getCursorBuilder(positionId = 1L), 5)
            incPosition(getCursorBuilder(positionId = 2L), 10, true)
            cppFilter = build()
            // проверяем навигацию для 1-го разворота (offset 10)
            assertNotNull(cppFilter.navigation.position)
            assertEquals(cppFilter.navigation.position!!.id, 2L)
            assertEquals(cppFilter.navigation.offset, 10)

            // сдвигаем курсор только до синхронизации (offset 10 + 15)
            incPosition(getCursorBuilder(positionId = 3L), 15)
            cppFilter = build()
            // проверяем навигацию для 2-го разворота
            assertEquals(cppFilter.navigation.position!!.id, 3L)
            assertEquals(cppFilter.navigation.offset, 25)

            // сдвигаем курсор по фейковому сценарию (offset 25 + 6)
            incPosition(getCursorBuilder(positionId = 4L), 3)
            incPosition(getCursorBuilder(positionId = 5L), 6, true)
            incPosition(getCursorBuilder(positionId = 6L), 9)
            cppFilter = build()
            // проверяем навигацию для 2-го разворота
            assertEquals(cppFilter.navigation.position!!.id, 5L)
            assertEquals(cppFilter.navigation.offset, 31)
        }
    }
}

private fun TestCursorListFilter.turnPage(time: List<Long>) {
    repeat(time.size) { index ->
        build()
        val newCursorBuilder = getCursorBuilder(time[index])
        incPosition(newCursorBuilder)
    }
}

private class TestCursorListFilter(hashProvider: HashFilterProvider) :
    CursorListFilterImpl<TestCppCursorFilter, TestCppCursor>(hashProvider) {

    var token: Int = 0

    override fun getLastSyncNavigation(lastFilter: TestCppCursorFilter): CursorNavigation<TestCppCursor> =
        lastFilter.navigation.run { CursorNavigation(limit, position = position) }

    override fun innerBuild(): TestCppCursorFilter {
        val navigation = getNavigation().run {
            TestCppCursorFilter.CppNavigation(position, limit, offset)
        }
        return TestCppCursorFilter(token, navigation)
    }

    fun getCursorBuilder(positionId: Long): CursorBuilder<TestCppCursor> =
        object : CursorBuilder<TestCppCursor> {
            override fun createCursor() = TestCppCursor(positionId)
        }
}

private data class TestCppCursor(val id: Long)

private data class TestCppCursorFilter(
    var token: Int = 0,
    var navigation: CppNavigation = CppNavigation()
) {
    fun getHash(): Int {
        var result = token.hashCode()
        result = 31 * result + (navigation.position?.hashCode() ?: 0)
        result = 31 * result + navigation.limit
        result = 31 * result + navigation.offset
        return result
    }

    data class CppNavigation(
        var position: TestCppCursor? = null,
        var limit: Int = 0,
        var offset: Int = 0
    )
}
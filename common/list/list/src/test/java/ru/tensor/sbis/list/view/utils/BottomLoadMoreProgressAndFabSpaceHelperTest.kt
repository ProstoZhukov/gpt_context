package ru.tensor.sbis.list.view.utils

import androidx.recyclerview.widget.LinearLayoutManager
import org.mockito.kotlin.atLeastOnce
import org.mockito.kotlin.clearInvocations
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import junitparams.JUnitParamsRunner
import junitparams.Parameters
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.mockito.quality.Strictness
import ru.tensor.sbis.list.view.SbisList
import ru.tensor.sbis.list.view.adapter.SbisAdapter
import ru.tensor.sbis.list.view.decorator.DecoratorHolder

@RunWith(JUnitParamsRunner::class)
internal class BottomLoadMoreProgressHelperTest {

    private val itemProgress = mock<ProgressItem>()
    private val bottomLoadMoreProgressHelper = BottomLoadMoreProgressHelper(itemProgressBottom = itemProgress)
    private val mockSbisList = mock<SbisList>()
    private val mockLayoutManager = mock<LinearLayoutManager>()
    private val mockAdapter = mock<SbisAdapter>()
    private val mockDecoratorHolder = mock<DecoratorHolder>()

    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS)

    @Before
    fun setUp() {
        bottomLoadMoreProgressHelper.attach(mockSbisList, mockLayoutManager, mockAdapter, mockDecoratorHolder)
        clearInvocations(mockSbisList, mockLayoutManager, mockAdapter, mockDecoratorHolder)
    }

    @Test
    @Parameters("true, true", "false, true")
    fun `Add item progress`(fabPadding: Boolean, hasLoadMore: Boolean) {
        bottomLoadMoreProgressHelper.fabPadding(fabPadding)
        bottomLoadMoreProgressHelper.setShowProgress(hasLoadMore)

        verify(mockAdapter).addLast(itemProgress)
    }

    @Test
    @Parameters("true, false", "false, false")
    fun `Remove item progress`(fabPadding: Boolean, hasLoadMore: Boolean) {
        bottomLoadMoreProgressHelper.fabPadding(fabPadding)
        bottomLoadMoreProgressHelper.setShowProgress(hasLoadMore)

        verify(mockAdapter).removeLast(itemProgress)
    }

    @Test
    fun `Add bottom space`() {
        val lastItemPosition = 42
        whenever(mockLayoutManager.findLastCompletelyVisibleItemPosition()) doReturn lastItemPosition
        whenever(mockAdapter.itemCount) doReturn 55//больше 42 - не мотаем в конец

        bottomLoadMoreProgressHelper.fabPadding(true)
        bottomLoadMoreProgressHelper.setShowProgress(false)

        verify(mockDecoratorHolder, atLeastOnce()).makeSureLastItemPaddingDecoratorIsAdded(mockSbisList, false, true)
    }

    @Test
    @Ignore("TODO: 2/25/2021 https://online.sbis.ru/opendoc.html?guid=e2a3e71f-471e-45e6-8593-a9f8c9d42950")
    fun `Scroll to end`() {
        val lastItemPosition = 42
        whenever(mockLayoutManager.findLastCompletelyVisibleItemPosition()) doReturn lastItemPosition
        whenever(mockAdapter.itemCount) doReturn 43

        bottomLoadMoreProgressHelper.fabPadding(true)
        bottomLoadMoreProgressHelper.setShowProgress(false)

        verify(mockSbisList).smoothScrollToPosition(43)
    }

    @Test
    @Parameters("true, true", "false, true", "false, false")
    fun `Remove bottom space`(fabPadding: Boolean, hasLoadMore: Boolean) {
        bottomLoadMoreProgressHelper.fabPadding(fabPadding)
        bottomLoadMoreProgressHelper.setShowProgress(hasLoadMore)

        verify(mockDecoratorHolder, atLeastOnce()).removeLastItemBottomPadding(mockSbisList)
    }
}
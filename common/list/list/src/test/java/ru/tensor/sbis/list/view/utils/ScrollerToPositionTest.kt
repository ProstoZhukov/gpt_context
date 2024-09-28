package ru.tensor.sbis.list.view.utils

import androidx.recyclerview.widget.LinearLayoutManager
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
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.mockito.quality.Strictness
import ru.tensor.sbis.list.view.SbisList

@RunWith(JUnitParamsRunner::class)
@Ignore("TODO: 19.01.21 https://online.sbis.ru/opendoc.html?guid=9e5ca78a-1ab1-4710-ac59-09753dbaf7af")
internal class ScrollerToPositionTest {

    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS)
    private val mockList = mock<SbisList>()
    private val mockLayoutManager = mock<LinearLayoutManager>()
    private lateinit var scrollerToPosition: ScrollerToPosition

    @Before
    fun setUp() {
        scrollerToPosition = ScrollerToPosition()
        scrollerToPosition.setListAndLayoutManager(mockList, mockLayoutManager)
    }

    @Test
    @Parameters("0,3", "0,3", "2,5", "2,5")
    fun `Given first visible position is 0 and moveToAdded is false, when items are moved, should scroll to start`(
        positionStart: Int,
        itemCount: Int
    ) {
        whenever(mockLayoutManager.findFirstCompletelyVisibleItemPosition()).doReturn(0)
        scrollerToPosition.moveToAdded = false
        scrollerToPosition.rememberFirstVisibleItemPosition()
        //act
        scrollerToPosition.onItemRangeMoved(positionStart, itemCount, 2)
        //verify
        verify(mockList).scrollToPosition(0)
        verify(mockList).scrollToPosition(anyInt())
    }

    @Test
    @Parameters("0,3", "0,3", "2,5", "2,5")
    fun `Given first visible position is 0 and moveToAdded is false, when items are inserted, should scroll to start`(
        positionStart: Int,
        itemCount: Int
    ) {
        whenever(mockLayoutManager.findFirstCompletelyVisibleItemPosition()).doReturn(0)
        scrollerToPosition.moveToAdded = false
        scrollerToPosition.rememberFirstVisibleItemPosition()
        //act
        scrollerToPosition.onItemRangeInserted(positionStart, itemCount)
        //verify
        verify(mockList).scrollToPosition(0)
        verify(mockList).scrollToPosition(anyInt())
    }

    @Test
    @Parameters("0, 3, 0", "0, 3, 2", "2, 5, 0")
    fun `Given scroll to added is set, when items are inserted, should scroll to added`(
        positionStart: Int,
        itemCount: Int,
        firstVisibleItemPosition: Int
    ) {
        whenever(mockLayoutManager.findFirstCompletelyVisibleItemPosition()).doReturn(firstVisibleItemPosition)
        scrollerToPosition.moveToAdded = true
        scrollerToPosition.rememberFirstVisibleItemPosition()
        //act
        scrollerToPosition.onItemRangeInserted(positionStart, itemCount)
        //verify
        verify(mockList).scrollToPosition(positionStart)
        verify(mockList).scrollToPosition(anyInt())
    }

    @Test
    @Parameters("0, 3, 0", "0, 3, 2", "2, 5, 0")
    fun `Given scroll to added is set, when items are moved, should scroll to added`(
        positionStart: Int,
        itemCount: Int,
        firstVisibleItemPosition: Int
    ) {
        whenever(mockLayoutManager.findFirstCompletelyVisibleItemPosition()).doReturn(firstVisibleItemPosition)
        scrollerToPosition.moveToAdded = true
        scrollerToPosition.rememberFirstVisibleItemPosition()
        //act
        scrollerToPosition.onItemRangeMoved(positionStart, itemCount, 2)
        //verify
        verify(mockList).scrollToPosition(positionStart)
        verify(mockList).scrollToPosition(anyInt())
    }
}
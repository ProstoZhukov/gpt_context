package ru.tensor.sbis.list.base.domain.entity.paging

import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever
import junitparams.JUnitParamsRunner
import junitparams.Parameters
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import ru.tensor.sbis.list.base.data.ResultHelper

@RunWith(JUnitParamsRunner::class)
class PagingDataTest {

    private val pagingData = PagingData<String>(mock())
    private val data0 = "data0"
    private val data1 = "data1"
    private val data2 = "data2"
    private val data3 = "data3"
    private val data4 = "data4"
    private val data5 = "data5"
    val isEmpty = mock<CheckTree>()
    val hasPrevious = mock<CheckTree>()
    val hasNext = mock<CheckHasNext<String>>()

    @Test
    fun default() {
        PagingData<String>(mock())
    }

    @Test
    fun `hasPrevious false`() {
        pagingData.update(0, data0)

        assertFalse(pagingData.hasPrevious())
        assertFalse(PagingData<String>(mock()).hasPrevious())
    }

    @Test
    fun `hasPrevious true`() {
        pagingData.update(1, data0)

        assertTrue(pagingData.hasPrevious())
    }

    @Test
    fun clear() {
        pagingData.update(0, data0)

        pagingData.clear()

        assertTrue(pagingData.map.isEmpty())
    }

    @Test
    fun `isEmpty false`() {
        pagingData.update(0, data0)

        pagingData.isEmpty()

        assertFalse(pagingData.isEmpty())
    }

    @Test
    fun `isEmpty true`() {
        pagingData.isEmpty()

        assertTrue(pagingData.isEmpty())
    }

    @Test
    fun listOfValues() {
        pagingData.update(0, data0)
        pagingData.update(1, data1)

        assertEquals(data0, pagingData.listOfValues()[0])
        assertEquals(data1, pagingData.listOfValues()[1])
        assertEquals(2, pagingData.listOfValues().size)
    }

    @Test
    fun `Update first`() {
        pagingData.update(0, data0)
        pagingData.update(0, data1)

        assertEquals(data1, pagingData.map[0])
    }

    @Test
    fun `Update last`() {
        pagingData.update(0, data0)
        pagingData.update(1, data1)
        pagingData.update(1, data2)

        assertEquals(data2, pagingData.map[1])
    }


    @Test
    fun `Given has data, when receive new one with key less then min key, then do not change data`() {
        pagingData.update(2, data0)
        pagingData.update(3, data1)
        pagingData.update(0, data2)

        assertEquals(data0, pagingData.map[2])
        assertEquals(data1, pagingData.map[3])
        assertNull(pagingData.map[0])
    }

    @Test
    fun `Given has data, when receive new one with key more then max key, then do not change data`() {
        pagingData.update(2, data0)
        pagingData.update(3, data1)
        pagingData.update(5, data2)

        assertEquals(data0, pagingData.map[2])
        assertEquals(data1, pagingData.map[3])
        assertNull(pagingData.map[4])
    }

    @Test
    fun `Given has data pieces count equals maxPages, when receive new one to end, then do not change pieces count`() {
        val pagingData = PagingData<String>(mock(), 4)
        pagingData.update(0, data0)
        pagingData.update(1, data1)
        pagingData.update(2, data2)
        pagingData.update(3, data3)
        //act
        pagingData.update(4, data4)
        //verify
        assertNull(pagingData.map[0])
        assertEquals(data1, pagingData.map[1])
        assertEquals(data2, pagingData.map[2])
        assertEquals(data3, pagingData.map[3])
        assertEquals(data4, pagingData.map[4])
    }

    @Test
    fun `Given has data pieces count equals maxPages, when receive new one to start, then do not change pieces count`() {
        val pagingData = PagingData<String>(mock(), 4)
        pagingData.update(2, data2)
        pagingData.update(3, data3)
        pagingData.update(4, data4)
        pagingData.update(5, data5)
        //act
        pagingData.update(1, data1)
        //verify
        assertNull(pagingData.map[5])
        assertEquals(data1, pagingData.map[1])
        assertEquals(data2, pagingData.map[2])
        assertEquals(data3, pagingData.map[3])
        assertEquals(data4, pagingData.map[4])
    }

    @Test
    fun `firstPageData null`() {
        assertNull(pagingData.firstPageData())
    }

    @Test
    fun firstPageData() {
        pagingData.update(2, data0)
        pagingData.update(3, data1)

        assertEquals(data0, pagingData.firstPageData())
    }

    @Test
    fun `lastPageData null`() {
        assertNull(pagingData.lastPageData())
    }

    @Test
    fun lastPageData() {
        pagingData.update(2, data0)
        pagingData.update(3, data1)

        assertEquals(data1, pagingData.lastPageData())
    }

    @Test
    fun `lastKeyOrZeroIfEmpty 0`() {
        assertEquals(0, pagingData.lastKeyOrZeroIfEmpty())
    }

    @Test
    fun lastKeyOrZeroIfEmpty() {
        pagingData.update(2, data0)
        pagingData.update(3, data1)

        assertEquals(3, pagingData.lastKeyOrZeroIfEmpty())
    }

    @Test
    fun `firstKeyOrZeroIfEmpty 0`() {
        assertEquals(0, pagingData.firstKeyOrZeroIfEmpty())
    }

    @Test
    fun firstKeyOrZeroIfEmpty() {
        pagingData.update(2, data0)
        pagingData.update(3, data1)

        assertEquals(2, pagingData.firstKeyOrZeroIfEmpty())
    }

    @Test
    fun `Given maxPages less than 3 and have 2 pieces of data, when receive third to end, have 3 pieces of data`() {
        val pagingData = PagingData<String>(mock(), 2)
        pagingData.update(0, data1)
        pagingData.update(1, data1)
        //act
        pagingData.update(2, data1)
        //verify
        assertEquals(pagingData.map.size, 3)
    }

    @Test
    fun `Given maxPages less than 3 and have 2 pieces of data, when receive third to start, have 3 pieces of data`() {
        val pagingData = PagingData<String>(mock(), 2)
        pagingData.update(1, data1)
        pagingData.update(2, data1)
        //act
        pagingData.update(0, data1)
        //verify
        assertEquals(pagingData.map.size, 3)
    }

    @Test
    fun `When paging data contains one item and it is stub service result, then paging data should be a stub`() {
        val resultHelper: ResultHelper<*, String> = mock()
        whenever(resultHelper.isEmpty(data0)).thenReturn(true)
        whenever(resultHelper.isStub(data0)).thenReturn(true)

        val pagingData = PagingData(resultHelper, 2)
        pagingData.update(0, data0)
        assertTrue(pagingData.isStub())
    }

    @Test
    fun `When paging data contains stub service result and something else, then paging data should not be a stub`() {
        val resultHelper: ResultHelper<*, String> = mock()
        whenever(resultHelper.isEmpty(data0)).thenReturn(false)
        whenever(resultHelper.isStub(data0)).thenReturn(false)
        whenever(resultHelper.isEmpty(data1)).thenReturn(true)
        whenever(resultHelper.isStub(data1)).thenReturn(true)

        val pagingData = PagingData(resultHelper, 2)
        pagingData.update(0, data0)
        pagingData.update(1, data1)
        assertFalse(pagingData.isStub())
    }

    @Test
    fun `When paging data is empty, then it should return null data for stub`() {
        val resultHelper: ResultHelper<*, Any> = mock()
        val pagingData = PagingData(resultHelper, 2)

        assertNull(pagingData.getStubData())
        verifyNoMoreInteractions(resultHelper)
    }

    @Test
    fun `When paging data contains one item, then paging data should return it as stub data`() {
        val resultHelper: ResultHelper<*, String> = mock()

        val pagingData = PagingData(resultHelper, 2)
        pagingData.update(0, data0)
        assertEquals(data0, pagingData.getStubData())
        verifyNoMoreInteractions(resultHelper)
    }


    @Test
    @Parameters(
        "true, false, true, false",
        "true, true, true, false",
        "true, true, false, false",
        "true, false, false, true",
        "false, false, true, false",
        "false, true, true, false",
        "false, true, false, false",
        "false, false, false, false"
    )
    fun isEmptyAndNoNextOrPreviousData(
        isEmptyValue: Boolean,
        hasNextValue: Boolean,
        hasPreviousValue: Boolean,
        result: Boolean
    ) {
        whenever(isEmpty(pagingData.map)).doReturn(isEmptyValue)
        whenever(hasNext(pagingData.map)).doReturn(hasNextValue)
        whenever(hasPrevious(pagingData.map)).doReturn(hasPreviousValue)
        val helper = mock<ResultHelper<*, String>>()
        val pagingData = PagingData(
            helper = helper,
            isEmpty = isEmpty,
            checkHasNext = hasNext,
            hasPrevious = hasPrevious
        )

        assertEquals(
            "isEmptyValue = $isEmptyValue; hasNextValue = $hasNextValue; hasPreviousValue = $hasPreviousValue",
            result, pagingData.isEmptyAndNoNextOrPreviousData()
        )
    }

    @Test
    fun `When page manually removed, then it state should be DELETED`() {
        pagingData.update(0, data0)

        pagingData.removePage(0)

        assertEquals(PageState.DELETED, pagingData.pagingState[0])
    }

}

interface CheckTree : (Map<Int, String>) -> Boolean
package ru.tensor.sbis.list.base.domain.entity.paging

import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import junitparams.JUnitParamsRunner
import junitparams.Parameters
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import ru.tensor.sbis.list.base.data.ResultHelper
import java.util.TreeMap

@RunWith(JUnitParamsRunner::class)
class CheckHasNextTest {

    private val helper = mock<ResultHelper<Int, String>>()

    @Test
    fun `When no data, then return false`() {
        assertTrue(CheckHasNext(helper) { 0 }(TreeMap<Int, String>()))
    }

    @Test
    @Parameters("true", "false")
    fun `When one empty page then see to hasNext flag`(hasNext: Boolean) {
        val data = "Data0"
        val treeMap = TreeMap<Int, String>()
        treeMap.apply {
            put(0, data)
        }
        whenever(helper.hasNext(data)).doReturn(hasNext)

        assertEquals(hasNext, CheckHasNext(helper) { 0 }(treeMap))
    }

    @Test
    @Parameters(
        "true, true",
        "true, false",
        "false, false",
        "false, true",
    )
    fun `When more then one not empty page then see to hasNext flag of last`(hasNext0: Boolean, hasNext1: Boolean) {
        val data0 = "Data0"
        val data1 = "Data1"
        val treeMap = TreeMap<Int, String>()
        treeMap.apply {
            put(0, data0)
            put(1, data1)
        }
        whenever(helper.hasNext(data0)).doReturn(hasNext0)
        whenever(helper.isEmpty(data0)).doReturn(false)
        whenever(helper.hasNext(data1)).doReturn(hasNext1)
        whenever(helper.isEmpty(data1)).doReturn(false)

        assertEquals(hasNext1, CheckHasNext(helper) { 0 }(treeMap))
    }
}
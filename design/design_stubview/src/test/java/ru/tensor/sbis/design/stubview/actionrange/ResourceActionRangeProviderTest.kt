package ru.tensor.sbis.design.stubview.actionrange

import android.content.Context
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * @author ma.kolpakov
 */
class ResourceActionRangeProviderTest {

    private companion object {
        const val STRING_RES_HELLO_RES = 0
        const val STRING_RES_HELLO = "hello"
    }

    private val mockContext: Context = mock {
        on { getString(STRING_RES_HELLO_RES) } doReturn STRING_RES_HELLO
    }

    @Test
    fun `Given details containing substring, when call getRange(), then return real range`() {
        val testDetailsText = "click hello button"
        val testRange = IntRange(6, 11)
        val rangeProvider =
            ResourceActionRangeProvider(STRING_RES_HELLO_RES)

        assertEquals(testRange, rangeProvider.getRange(mockContext, testDetailsText))
    }

    @Test
    fun `Given not details containing substring, when call getRange(), then return empty range`() {
        val testDetailsText = "click urvneu347uhfn3 button"
        val rangeProvider =
            ResourceActionRangeProvider(STRING_RES_HELLO_RES)

        val resultRange = rangeProvider.getRange(mockContext, testDetailsText)

        assertTrue(resultRange.isEmpty())
    }
}

package ru.tensor.sbis.design.stubview.actionrange

import org.mockito.kotlin.mock
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * @author ma.kolpakov
 */
class StringActionRangeProviderTest {

    @Test
    fun `Given details containing substring, when call getRange(), then return real range`() {
        val testDetailsText = "click hello button"
        val testRange = IntRange(6, 11)
        val rangeProvider =
            StringActionRangeProvider("hello")

        assertEquals(testRange, rangeProvider.getRange(mock(), testDetailsText))
    }

    @Test
    fun `Given not details containing substring, when call getRange(), then return empty range`() {
        val testDetailsText = "click urvneu347uhfn3 button"
        val rangeProvider =
            StringActionRangeProvider("hello")

        val resultRange = rangeProvider.getRange(mock(), testDetailsText)

        assertTrue(resultRange.isEmpty())
    }
}

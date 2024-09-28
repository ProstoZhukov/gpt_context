package ru.tensor.sbis.base_components.adapter.selectable

import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.junit.Assert.assertTrue
import org.junit.Test

class ThrottleUntilChangedPredicateTest {

    @Test
    fun `test shouldThrottle is false`() {
        assertTrue(ThrottleUntilChangedPredicate<String>(shouldThrottle = false).test("test"))
    }

    @Test
    fun `test shouldThrottle is true`() {
        assertTrue(
            ThrottleUntilChangedPredicate<String>(shouldThrottle = true, timeStampProvider = { 800 }).test("test")
        )
    }

    @Test
    fun `test ignoreSelectionThrottling is true`() {
        val predicate = ThrottleUntilChangedPredicate<String>(shouldThrottle = true)
        predicate.ignoreSelectionThrottling = true
        assertTrue(predicate.test("test"))
    }

    @Test
    fun `test throttleDuration is respected`() {
        val mockTimeStampProvider = mock<() -> Long>()
        whenever(mockTimeStampProvider()).thenReturn(900)
        val predicate = ThrottleUntilChangedPredicate<String>(
            shouldThrottle = true,
            timeStampProvider = mockTimeStampProvider
        )
        assertTrue(predicate.test("test"))
    }

    @Test
    fun `test throttleDuration is respected2`() {
        val mockTimeStampProvider = mock<() -> Long>()
        val currentTimeMillis = 900L
        whenever(mockTimeStampProvider()).thenReturn(currentTimeMillis)
        val predicate =
            ThrottleUntilChangedPredicate<String>(shouldThrottle = true, timeStampProvider = mockTimeStampProvider)
        whenever(mockTimeStampProvider()).thenReturn(currentTimeMillis + 800L + 1)

        assertTrue(predicate.test("test"))
    }
}
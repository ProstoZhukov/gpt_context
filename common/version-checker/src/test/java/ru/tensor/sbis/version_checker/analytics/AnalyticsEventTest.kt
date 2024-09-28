package ru.tensor.sbis.version_checker.analytics

import junitparams.JUnitParamsRunner
import junitparams.Parameters
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import ru.tensor.sbis.common.testing.params

@RunWith(JUnitParamsRunner::class)
internal class AnalyticsEventTest {

    @Test
    @Parameters(method = "testParamsDifferentKeys")
    fun `Not equals with different key`(event1: AnalyticsEvent, event2: AnalyticsEvent) {
        assertFalse(event1 == event2)
    }

    @Test
    @Parameters(method = "testParamsSameKeys")
    fun `Equals with the same keys`(event1: AnalyticsEvent, event2: AnalyticsEvent) {
        assertTrue(event1 == event2)
    }

    @Test
    fun `Hashcode is the key hashcode`() {
        val testEvent = AnalyticsEvent.ShowCriticalScreen()
        assertEquals(testEvent.action.hashCode(), testEvent.hashCode())
    }

    @Suppress("unused")
    private fun testParamsDifferentKeys() = params {
        add(AnalyticsEvent.ShowCriticalScreen(), AnalyticsEvent.ShowRecommendedScreen())
        add(AnalyticsEvent.ClickRecommendedUpdate(), AnalyticsEvent.ClickCriticalUpdate())
        add(AnalyticsEvent.ShowRecommendedScreen(), AnalyticsEvent.ClickRecommendedUpdate())
        add(AnalyticsEvent.ClickCriticalUpdate(), AnalyticsEvent.ShowCriticalScreen())
    }

    @Suppress("unused")
    private fun testParamsSameKeys() = params {
        add(AnalyticsEvent.ShowCriticalScreen(), AnalyticsEvent.ShowCriticalScreen())
        add(AnalyticsEvent.ShowRecommendedScreen(), AnalyticsEvent.ShowRecommendedScreen())
        add(AnalyticsEvent.ClickRecommendedUpdate(), AnalyticsEvent.ClickRecommendedUpdate())
        add(AnalyticsEvent.ClickCriticalUpdate(), AnalyticsEvent.ClickCriticalUpdate())
    }
}
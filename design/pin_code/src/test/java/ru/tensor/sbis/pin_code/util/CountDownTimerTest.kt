package ru.tensor.sbis.pin_code.util

import io.reactivex.functions.Consumer
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import ru.tensor.sbis.common.testing.TestSchedulerRule
import java.util.concurrent.TimeUnit

/**
 * Тест для [createCountDownTimer].
 *
 * @author as.stafeev
 */
class CountDownTimerTest {

    @get:Rule
    val rxRule = TestSchedulerRule()

    @Test
    fun `Given savedStartTimeMs zero and defaultStartTimeSec two sec, when subscribe, then get event three times`() {
        val captor = argumentCaptor<Long>()
        val consumer = mock<Consumer<Long>>()
        val savedStartTimeMs = 0L
        val defaultStartTimeSec = 2L

        createCountDownTimer(savedStartTimeMs, defaultStartTimeSec).subscribe(consumer)
        rxRule.scheduler.advanceTimeBy(defaultStartTimeSec, TimeUnit.SECONDS)

        val expected = listOf(2L, 1L, 0L) // обратный отсчет 2,1,0
        verify(consumer, times(expected.size)).accept(captor.capture())
        assertThat(captor.allValues, equalTo(expected))
    }

    @Test
    fun `Given difference between saved and current times two sec and defaultStartTimeSec three sec, when subscribe, then get event two times`() {
        val captor = argumentCaptor<Long>()
        val consumer = mock<Consumer<Long>>()
        val defaultStartTimeSec = 3L
        val currentTimeMs = 5000L
        val savedStartTimeMs = 3000L

        createCountDownTimer(savedStartTimeMs, defaultStartTimeSec, currentTimeMs).subscribe(consumer)
        rxRule.scheduler.advanceTimeBy(defaultStartTimeSec, TimeUnit.SECONDS)

        val expected = listOf(1L, 0L) // обратный отсчет 1,0
        verify(consumer, times(expected.size)).accept(captor.capture())
        assertThat(captor.allValues, equalTo(expected))
    }

    @Test
    fun `Given difference between saved and current negative and defaultStartTimeSec three sec, when subscribe, then get event four times`() {
        val captor = argumentCaptor<Long>()
        val consumer = mock<Consumer<Long>>()
        val defaultStartTimeSec = 3L
        val currentTimeMs = 0L
        val savedStartTimeMs = 6000L

        createCountDownTimer(savedStartTimeMs, defaultStartTimeSec, currentTimeMs).subscribe(consumer)
        rxRule.scheduler.advanceTimeBy(defaultStartTimeSec, TimeUnit.SECONDS)

        val expected = listOf(3L, 2L, 1L, 0L) // обратный отсчет 3,2,1,0
        verify(consumer, times(expected.size)).accept(captor.capture())
        assertThat(captor.allValues, equalTo(expected))
    }
}
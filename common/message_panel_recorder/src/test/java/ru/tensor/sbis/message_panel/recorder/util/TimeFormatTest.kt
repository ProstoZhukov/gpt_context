package ru.tensor.sbis.message_panel.recorder.util

import org.junit.Test
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat

/**
 * @author vv.chekurda
 * Создан 8/2/2019
 */
class TimeFormatTest {

    @Test
    fun `Test default format on zero time`() {
        assertThat(0L.toTimeString(), equalTo(DEFAULT_TIME))
    }

    @Test
    fun `One second`() {
        assertThat(1L.toTimeString(), equalTo("00:01"))
    }

    @Test
    fun `One minute`() {
        assertThat(60L.toTimeString(), equalTo("01:00"))
    }

    @Test
    fun `Upper limit test`() {
        assertThat(TIME_FORMAT_LIMIT.toTimeString(), equalTo("99:59"))
    }

    @Test(expected = IllegalArgumentException::class)
    fun `Upper limit overhead test`() {
        (TIME_FORMAT_LIMIT + 1L).toTimeString()
    }

    @Test(expected = IllegalArgumentException::class)
    fun `Down limit test`() {
        (-1L).toTimeString()
    }
}
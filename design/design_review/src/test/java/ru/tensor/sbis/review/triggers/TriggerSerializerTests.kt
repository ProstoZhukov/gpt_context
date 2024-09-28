package ru.tensor.sbis.review.triggers

import android.content.SharedPreferences
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import junitparams.JUnitParamsRunner
import org.junit.Test
import org.junit.runner.RunWith
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author ma.kolpakov
 */
@RunWith(JUnitParamsRunner::class)
class TriggerSerializerTests {
    private val mockSharedPreferences: SharedPreferences = mock()

    private val mockEditor: SharedPreferences.Editor = mock()

    @Test
    fun `When CountTrigger serialize, then value incremented`() {
        val eventKey = "eventKey"

        whenever(mockSharedPreferences.getLong(eventKey, 0)).thenReturn(2)
        whenever(mockSharedPreferences.edit()).thenReturn(mockEditor)

        CountTrigger(TestEvent.FIRST, 2)
            .serializer.serialize(eventKey, mockSharedPreferences)

        verify(mockEditor).putLong(eventKey, 3)
    }

    @Test
    fun `When DailyCountTrigger serialize, then value incremented`() {
        val eventKey = "eventKey"
        val dateFormat = SimpleDateFormat("dd/M/yyyy")

        whenever(mockSharedPreferences.getLong(eventKey + DAILY_COUNT_SUFFIX, 0)).thenReturn(2)
        whenever(mockSharedPreferences.getString(eventKey + DAILY_COUNT_DATE, "")).thenReturn(dateFormat.format(Date()))
        whenever(mockSharedPreferences.edit()).thenReturn(mockEditor)

        DailyCountTrigger(TestEvent.FIRST, 2)
            .serializer.serialize(eventKey, mockSharedPreferences)

        verify(mockEditor).putLong(eventKey + DAILY_COUNT_SUFFIX, 3)
    }
}

package ru.tensor.sbis.review.triggers

import android.content.SharedPreferences
import org.mockito.kotlin.*
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author ma.kolpakov
 */
@RunWith(MockitoJUnitRunner.StrictStubs::class)
class TriggersTests {
    @Mock
    private lateinit var mockSharedPreferences: SharedPreferences

    @Test
    fun `When count trigger check 2 times, then return true`() {
        whenever(mockSharedPreferences.getLong(any(), any())).thenReturn(2)

        val countTrigger = CountTrigger(TestEvent.FIRST, 2)

        assertTrue(countTrigger.checkEvent(mockSharedPreferences))
    }

    @Test
    fun `When count trigger check 1 times, then return false`() {
        whenever(mockSharedPreferences.getLong(any(), any())).thenReturn(1)

        val countTrigger = CountTrigger(TestEvent.FIRST, 2)

        assertFalse(countTrigger.checkEvent(mockSharedPreferences))
    }

    @Test
    fun `When DailyCountTrigger trigger check 2 times, then return true`() {
        whenever(mockSharedPreferences.getLong(any(), any())).thenReturn(2)

        val countTrigger = DailyCountTrigger(TestEvent.FIRST, 2)

        assertTrue(countTrigger.checkEvent(mockSharedPreferences))
    }

    @Test
    fun `When count DailyCountTrigger check 1 times, then return false`() {
        whenever(mockSharedPreferences.getLong(any(), any())).thenReturn(1)

        val countTrigger = DailyCountTrigger(TestEvent.FIRST, 2)

        assertFalse(countTrigger.checkEvent(mockSharedPreferences))
    }

    @Test
    fun `When DaysCountTrigger check 1 time and threshold = 2, then return false`() {
        val countTrigger = DaysCountTrigger(TestEvent.FIRST, 2)
        countTrigger.checkEvent(mockSharedPreferences)

        assertFalse(countTrigger.checkEvent(mockSharedPreferences))
    }

    @Test
    fun `When DaysCountTrigger check 1 time and threshold = 1, then return true`() {
        val countTrigger = DaysCountTrigger(TestEvent.FIRST, 1)
        whenever(mockSharedPreferences.getLong(any(), any())).thenReturn(1)

        assertTrue(countTrigger.checkEvent(mockSharedPreferences))
    }

    @Test
    fun `When DaysCountTrigger emmit 2 events in same day, save count 1 times`() {
        val eventKey = "eventKey"
        val date = Date()
        val dateFormat = SimpleDateFormat("dd/M/yyyy")

        val editor: SharedPreferences.Editor = mock()
        whenever(mockSharedPreferences.edit()).thenReturn(editor)
        DaysCountTrigger.currentDayProvider = { Date() }
        val countTrigger = DaysCountTrigger(TestEvent.FIRST, 2)
        // сначала сохраняем как будто у нас разные дни
        countTrigger.serializer.serialize(eventKey, mockSharedPreferences)
        // возвращаем тот же день
        whenever(mockSharedPreferences.getString(any(), any())).thenReturn(dateFormat.format(date))
        countTrigger.serializer.serialize(eventKey, mockSharedPreferences)

        verify(editor).putLong(eventKey + DAYS_COUNT_SUFFIX, 1)
    }

    @Test
    fun `When DaysCountTrigger emmit 2 events in different days, save count 2 times`() {
        val eventKey = "eventKey"
        val editor: SharedPreferences.Editor = mock()
        whenever(mockSharedPreferences.edit()).thenReturn(editor)

        val countTrigger = DaysCountTrigger(TestEvent.FIRST, 2)
        countTrigger.serializer.serialize(eventKey, mockSharedPreferences)
        countTrigger.serializer.serialize(eventKey, mockSharedPreferences)

        verify(editor, times(2)).putLong(eventKey + DAYS_COUNT_SUFFIX, 1)
    }

    @Test
    fun `When DaysCountTrigger serializer  event, then save incremented value`() {
        val eventKey = "eventKey"
        val editor: SharedPreferences.Editor = mock()
        whenever(mockSharedPreferences.edit()).thenReturn(editor)
        whenever(mockSharedPreferences.getLong(any(), any())).thenReturn(4)

        val countTrigger = DaysCountTrigger(TestEvent.FIRST, 2)
        countTrigger.serializer.serialize(eventKey, mockSharedPreferences)

        verify(editor).putLong(eventKey + DAYS_COUNT_SUFFIX, 5)
    }
}

package ru.tensor.sbis.design.list_header

import org.joda.time.LocalDateTime
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import ru.tensor.sbis.design.list_header.format.isToday

/**
 * @author ra.petrov
 */
class ListDateFormatterTest {

    @Test
    fun `When date is today then true`() {
        val now = LocalDateTime.now()
        assertTrue(now.toDate().isToday())

        val todayButNotNow = LocalDateTime(now.year, now.monthOfYear, now.dayOfMonth, (now.hourOfDay + 1) % 23, 0)

        assertTrue("Now is $now and today is $todayButNotNow", todayButNotNow.toDate().isToday())
    }

    @Test
    fun `When date is not today then false`() {
        assertFalse(LocalDateTime.now().minusDays(1).toDate().isToday())
    }
}
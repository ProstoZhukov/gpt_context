package ru.tensor.sbis.business.common.utils

import org.junit.Assert.assertEquals
import org.junit.Test
import ru.tensor.sbis.business.common.ui.utils.set
import java.util.Date

class DateExtensionsKtTest {

    @Test
    fun `correct date to string parsing`() {
        assertEquals("2019-01-01", Date().set(2019, 0, 1).formatFreeZoneDate())
        assertEquals("2019-11-01", Date().set(2019, 10, 1).formatFreeZoneDate())
        assertEquals("2019-11-11", Date().set(2019, 10, 11).formatFreeZoneDate())
        assertEquals("2019-03-28", Date().set(2019, 2, 28).formatFreeZoneDate())
        assertEquals("2019-03-28", Date().set(2019, 2, 28).formatFreeZoneDate())
        assertEquals("2019-12-31", Date().set(2019, 11, 31).formatFreeZoneDate())
        assertEquals("1970-01-01", Date(0).formatFreeZoneDate())
    }
}
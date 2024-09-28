package ru.tensor.sbis.date_picker

import nl.jqno.equalsverifier.EqualsVerifier
import nl.jqno.equalsverifier.Warning
import org.junit.Test

class PeriodTest {
    @Test
    fun testEqualsAndHashCode() {
        EqualsVerifier.forClass(Period::class.java)
            .withIgnoredFields("fakeDateTo")
            .suppress(Warning.NONFINAL_FIELDS).verify()
    }
}
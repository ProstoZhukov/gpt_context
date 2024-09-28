package ru.tensor.sbis.date_picker.month.items

import nl.jqno.equalsverifier.EqualsVerifier
import nl.jqno.equalsverifier.Warning
import org.junit.Test

class DayVMTest {
    @Test
    fun testEqualsAndHashCode() {
        EqualsVerifier.forClass(DayVM::class.java)
            .withIgnoredFields("clickAction", "isUnavailable")
            .suppress(Warning.NONFINAL_FIELDS).verify()
    }
}
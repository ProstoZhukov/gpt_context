package ru.tensor.sbis.date_picker.month.items

import nl.jqno.equalsverifier.EqualsVerifier
import nl.jqno.equalsverifier.Warning
import org.junit.Test

class DayLabelVMTest {
    @Test
    fun testEqualsAndHashCode() {
        EqualsVerifier.forClass(DayLabelVM::class.java)
            .suppress(Warning.NONFINAL_FIELDS).verify()
    }
}
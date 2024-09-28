package ru.tensor.sbis.list.view.item

import nl.jqno.equalsverifier.EqualsVerifier
import nl.jqno.equalsverifier.Warning
import org.junit.Test

class OptionsTest {

    @Test
    fun testEqualsAndHashCode() {
        EqualsVerifier.forClass(Options::class.java).suppress(Warning.NONFINAL_FIELDS).withIgnoredFields(
            "longClickAction", "clickAction"
        ).verify()
    }
}
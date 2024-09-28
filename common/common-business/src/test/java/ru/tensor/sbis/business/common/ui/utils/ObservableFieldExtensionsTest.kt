package ru.tensor.sbis.business.common.ui.utils

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ObservableFieldExtensionsTest {

    private companion object {
        const val testString = "some string"
        const val testEmptyString = ""
    }

    @Test
    fun `ObservableBoolean isTrue true`() {
        assertTrue(ObservableBoolean(true).isTrue)
    }

    @Test
    fun `ObservableBoolean isTrue false`() {
        assertFalse(ObservableBoolean(false).isTrue)
    }

    @Test
    fun `ObservableBoolean isFalse true`() {
        assertTrue(ObservableBoolean(false).isFalse)
    }

    @Test
    fun `ObservableBoolean isFalse false`() {
        assertFalse(ObservableBoolean(true).isFalse)
    }

    @Test
    fun `ObservableBoolean toggle from true to false`() {
        val observable = ObservableBoolean(true)

        observable.toggle()

        assertEquals(false, observable.get())
    }

    @Test
    fun `ObservableBoolean toggle from false to true`() {
        val observable = ObservableBoolean(false)

        observable.toggle()

        assertEquals(true, observable.get())
    }

    @Test
    fun `ObservableField with String getOrEmpty string value if not null`() {
        val observable = ObservableField(testString)

        assertEquals(testString, observable.getOrEmpty())
        assertEquals(observable.get(), observable.getOrEmpty())
    }

    @Test
    fun `ObservableField with String getOrEmpty string empty if null`() {
        val observable = ObservableField<String>()

        assertEquals(testEmptyString, observable.getOrEmpty())
    }
}

package ru.tensor.sbis.design.text_span.text.masked.formatter

import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import ru.tensor.sbis.design.text_span.text.masked.formatter.phone.formatPhone

private const val NON_BREAKING_SPACE = '\u00A0'

/**
 * Тест утилиты для форматирования телефонных номеров
 *
 * @author us.bessonov
 */
@RunWith(MockitoJUnitRunner.StrictStubs::class)
class PhoneFormatUtilsTest {

    @Test
    fun `When phone number length is equal to digit positions count in suitable mask, number formatted correctly`() {
        val phone = "+71234567890"

        assertEquals("+7$NON_BREAKING_SPACE(123)${NON_BREAKING_SPACE}456-78-90", formatPhone(phone))
    }

    @Test
    fun `When phone number length is less than digit positions count in suitable mask, number formatted correctly`() {
        val phone = "12345678"

        assertEquals("(123)${NON_BREAKING_SPACE}456-78", formatPhone(phone))
    }

    @Test
    fun `When phone number length is greater than digit positions count in suitable mask, number formatted correctly`() {
        val phone = "+71234567890123"

        assertEquals("+7${NON_BREAKING_SPACE}1234567890123", formatPhone(phone))
    }
}
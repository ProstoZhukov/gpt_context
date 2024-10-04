package ru.tensor.sbis.design.text_span.text.masked.phone

import android.text.Spanned
import org.mockito.kotlin.whenever
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.mockito.quality.Strictness
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * @author ma.kolpakov
 * @since 12/6/2019
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class PhoneLengthFilterRuTest {

    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS)

    @Mock
    private lateinit var dest: Spanned

    private val filter = PhoneLengthFilter(RU_LEN)

    @Test
    fun `When ru phone start with 7 and it has les than 11 numbers, then string shouldn't be cut`() {
        // будем добавлять по одной цифре и проверять результат
        "1234567890-".fold("7") { phone, c ->
            // по контракту возвращается null, если не должно измениться
            assertNull(filter.filter(phone, 0, phone.length, dest, 0, 0))
            phone + c
        }
    }

    @Test
    fun `When ru phone start with 7 and it has more than 11 numbers, then string should be cut`() {
        val phone = "712345678901"
        val expectedPhone = "71234567890"
        val destLen = 1
        whenever(dest.length).thenReturn(destLen)
        whenever(dest[0]).thenReturn('7')

        assertEquals(expectedPhone, filter.filter(phone, 0, phone.length, dest, 0, destLen))
    }

    @Test
    fun `When ru phone start with +7 and it has les than 11 numbers, then string shouldn't be cut`() {
        "1234567890-".fold("+7") { phone, c ->
            assertNull(filter.filter(phone, 0, phone.length, dest, 0, 0))
            phone + c
        }
    }

    /**
     * Fix https://online.sbis.ru/opendoc.html?guid=08a3c962-cadd-4f19-8acd-57cc0b8b85c2
     */
    @Test
    fun `When ru phone start with +7 and it has more than 11 numbers, then string should be cut`() {
        val phone = "+712345678901"
        val expectedPhone = "+71234567890"
        val destLen = 2
        whenever(dest.length).thenReturn(destLen)
        whenever(dest[0]).thenReturn('+')
        whenever(dest[1]).thenReturn('7')

        assertEquals(expectedPhone, filter.filter(phone, 0, phone.length, dest, 0, destLen))
    }
}
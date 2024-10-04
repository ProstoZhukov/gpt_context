package ru.tensor.sbis.design.text_span.text.masked.phone

import android.text.Spanned
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
import ru.tensor.sbis.design.text_span.text.masked.formatter.phone.COMMON_PHONE_MASK

/**
 * @author ma.kolpakov
 * @since 12/6/2019
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class PhoneLengthFilterCommonTest {

    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS)

    @Mock
    private lateinit var dest: Spanned

    private val filter = PhoneLengthFilter(COMMON_FORMAT)

    @Test
    fun `When phone length les than common limit, then it shouldn't be cut`() {
        val phone = "1".repeat(COMMON_PHONE_MASK.length)
        assertNull(filter.filter(phone, 0, phone.length, dest, 0, 0))
    }

    @Test
    fun `When phone length more than common limit, then it should be cut`() {
        val expectedPhone = "1".repeat(COMMON_PHONE_MASK.length)
        val phone = expectedPhone + "1"
        assertEquals(expectedPhone, filter.filter(phone, 0, phone.length, dest, 0, 0))
    }
}
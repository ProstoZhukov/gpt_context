package ru.tensor.sbis.design.custom_view_tools.utils.text_highlight

import android.graphics.Color
import android.text.Spannable
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.junit.MockitoJUnitRunner
import ru.tensor.sbis.design.custom_view_tools.utils.setHighlightSpan

/**
 * Тесты метода [setHighlightSpan].
 *
 * @author vv.chekurda
 */
@RunWith(MockitoJUnitRunner.StrictStubs::class)
class TextHighlightKtHighlightSpanTest {

    @Mock
    private lateinit var spannable: Spannable
    private val highlightColor = Color.YELLOW

    @Test
    fun `When end position bigger than start position, then set span`() {
        val start = 0
        val end = 5

        spannable.setHighlightSpan(highlightColor, start, end)

        verify(spannable).setSpan(any(), eq(start), eq(end), anyInt())
        verifyNoMoreInteractions(spannable)
    }

    @Test
    fun `When end position smaller than start position, then don't set span`() {
        val start = 5
        val end = 0

        spannable.setHighlightSpan(highlightColor, start, end)

        verifyNoMoreInteractions(spannable)
    }

    @Test
    fun `When end position equals start position, then don't set span`() {
        val start = 5
        val end = 5

        spannable.setHighlightSpan(highlightColor, start, end)

        verifyNoMoreInteractions(spannable)
    }
}
package ru.tensor.sbis.design.sbis_text_view.delegates

import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.text.Layout
import android.text.TextPaint
import android.text.TextUtils.TruncateAt
import android.view.Gravity
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import ru.tensor.sbis.design.custom_view_tools.TextLayout
import ru.tensor.sbis.design.sbis_text_view.SbisTextView
import ru.tensor.sbis.design.sbis_text_view.utils.AllCapsTransformationMethod

/**
 * Тесты делегирования вызовов API [SbisTextView] к [TextLayout].
 *
 * @author vv.chekurda
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class SbisTextViewTextLayoutDelegateTest {

    private lateinit var textLayout: TextLayout
    private lateinit var sbisTextView: SbisTextView

    @Before
    fun setUp() {
        textLayout = mock()
        sbisTextView = SbisTextView(ApplicationProvider.getApplicationContext(), textLayout)
    }

    @Test
    fun `When call setText, then call configure on TextLayout`() {
        sbisTextView.text = "12345"

        verify(textLayout).configure(any())
        verifyNoMoreInteractions(textLayout)
    }

    @Test
    fun `When call getText, then return text from TextLayout`() {
        val expectedText = "12345"
        whenever(textLayout.text).thenReturn(expectedText)

        val text = sbisTextView.text

        assertEquals(expectedText, text)
        verify(textLayout).text
        verifyNoMoreInteractions(textLayout)
    }

    @Test
    fun `When call setTextSize, then call configure on TextLayout`() {
        sbisTextView.textSize = 100f

        verify(textLayout).configure(any())
        verifyNoMoreInteractions(textLayout)
    }

    @Test
    fun `When call getTextSize, then return textSize from paint of TextLayout`() {
        val expectedTextSize = 1000f
        val mockPaint = mock<TextPaint> {
            on { textSize } doReturn expectedTextSize
        }
        whenever(textLayout.textPaint) doReturn mockPaint

        val textSize = sbisTextView.textSize

        assertEquals(expectedTextSize, textSize)
        verify(mockPaint).textSize
        verify(textLayout).textPaint
        verifyNoMoreInteractions(textLayout)
        verifyNoMoreInteractions(mockPaint)
    }

    @Test
    fun `When call getTextColor, then return color from paint of TextLayout`() {
        val expectedTextColor = Color.RED
        val mockPaint = mock<TextPaint> {
            on { color } doReturn expectedTextColor
        }
        whenever(textLayout.textPaint) doReturn mockPaint

        val textColor = sbisTextView.textColor

        assertEquals(expectedTextColor, textColor)
        verify(mockPaint).color
        verify(textLayout).textPaint
        verifyNoMoreInteractions(textLayout)
        verifyNoMoreInteractions(mockPaint)
    }

    @Test
    fun `When call getTextColors, then call getColorStateList and getColor on paint of TextLayout as default`() {
        val mockPaint = mock<TextPaint> {
            on { color } doReturn Color.RED
        }
        whenever(textLayout.textPaint) doReturn mockPaint

        sbisTextView.textColors

        verify(textLayout).colorStateList
        verify(textLayout).textPaint
        verify(mockPaint).color
        verifyNoMoreInteractions(textLayout)
        verifyNoMoreInteractions(mockPaint)
    }

    @Test
    fun `When call getLinkTextColor, then return color from paint of TextLayout as default`() {
        val expectedLinkColor = Color.RED
        val mockPaint = mock<TextPaint> {
            on { color } doReturn expectedLinkColor
        }
        whenever(textLayout.textPaint) doReturn mockPaint

        val linkColor = sbisTextView.linkTextColor

        assertEquals(expectedLinkColor, linkColor)
        verify(mockPaint).color
        verify(textLayout).textPaint
        verifyNoMoreInteractions(textLayout)
        verifyNoMoreInteractions(mockPaint)
    }

    @Test
    fun `When call setIsSingleLine, then call configure on TextLayout`() {
        sbisTextView.isSingleLine = true

        verify(textLayout).configure(any())
        verifyNoMoreInteractions(textLayout)
    }

    @Test
    fun `When call getIsSingleLine, then return isSingleLine from TextLayout`() {
        val expectedIsSingleLine = true
        whenever(textLayout.isSingleLine).thenReturn(expectedIsSingleLine)

        val isSingleLine = sbisTextView.isSingleLine

        assertEquals(expectedIsSingleLine, isSingleLine)
        verify(textLayout).isSingleLine
        verifyNoMoreInteractions(textLayout)
    }

    @Test
    fun `When call setLines, then call configure on TextLayout`() {
        sbisTextView.lines = 100

        verify(textLayout).configure(any())
        verifyNoMoreInteractions(textLayout)
    }

    @Test
    fun `When call setMaxLines, then call configure on TextLayout`() {
        sbisTextView.maxLines = 100

        verify(textLayout).configure(any())
        verifyNoMoreInteractions(textLayout)
    }

    @Test
    fun `When call getMaxLines, then return maxLines from TextLayout`() {
        val expectedMaxLines = 100
        whenever(textLayout.maxLines).thenReturn(expectedMaxLines)

        val maxLines = sbisTextView.maxLines

        assertEquals(expectedMaxLines, maxLines)
        verify(textLayout).maxLines
        verifyNoMoreInteractions(textLayout)
    }

    @Test
    fun `When call setMinLines, then call configure on TextLayout`() {
        sbisTextView.minLines = 100

        verify(textLayout).configure(any())
        verifyNoMoreInteractions(textLayout)
    }

    @Test
    fun `When call getMinLines, then return minLines from TextLayout`() {
        val expectedMinLines = 100
        whenever(textLayout.minLines).thenReturn(expectedMinLines)

        val minLines = sbisTextView.minLines

        assertEquals(expectedMinLines, minLines)
        verify(textLayout).minLines
        verifyNoMoreInteractions(textLayout)
    }

    @Test
    fun `When call getLineCount, then return lineCount from TextLayout`() {
        val expectedLineCount = 100
        whenever(textLayout.lineCount).thenReturn(expectedLineCount)

        val lineCount = sbisTextView.lineCount

        assertEquals(expectedLineCount, lineCount)
        verify(textLayout).lineCount
        verifyNoMoreInteractions(textLayout)
    }

    @Test
    fun `When call setMaxWidth, then call configure on TextLayout`() {
        sbisTextView.maxWidth = 100

        verify(textLayout).configure(any())
        verifyNoMoreInteractions(textLayout)
    }

    @Test
    fun `When call setMinWidth, then call configure on TextLayout`() {
        sbisTextView.minWidth = 100

        verify(textLayout).configure(any())
        verifyNoMoreInteractions(textLayout)
    }

    @Test
    fun `When call setMaxHeight, then call configure on TextLayout`() {
        sbisTextView.maxHeight = 100

        verify(textLayout).configure(any())
        verifyNoMoreInteractions(textLayout)
    }

    @Test
    fun `When call setMinHeight, then call configure on TextLayout`() {
        sbisTextView.minHeight = 100

        verify(textLayout).configure(any())
        verifyNoMoreInteractions(textLayout)
    }

    @Test
    fun `When call setGravity, then call configure on TextLayout`() {
        sbisTextView.gravity = Gravity.BOTTOM

        verify(textLayout).configure(any())
        verifyNoMoreInteractions(textLayout)
    }

    @Test
    fun `When call setTypeface, then call configure on TextLayout`() {
        sbisTextView.typeface = Typeface.SERIF

        verify(textLayout).configure(any())
        verifyNoMoreInteractions(textLayout)
    }

    @Test
    fun `When call setEllipsize, then call configure on TextLayout`() {
        sbisTextView.ellipsize = TruncateAt.MARQUEE

        verify(textLayout).configure(any())
        verifyNoMoreInteractions(textLayout)
    }

    @Test
    fun `When call getEllipsize, then return ellipsize from TextLayout`() {
        val expectedEllipsize = TruncateAt.END
        whenever(textLayout.ellipsize).thenReturn(expectedEllipsize)

        val ellipsize = sbisTextView.ellipsize

        assertEquals(expectedEllipsize, ellipsize)
        verify(textLayout).ellipsize
        verifyNoMoreInteractions(textLayout)
    }

    @Test
    fun `When call getEllipsizedWidth, then return ellipsizedWidth from TextLayout`() {
        val expectedEllipsizedWidth = 100
        whenever(textLayout.ellipsizedWidth).thenReturn(expectedEllipsizedWidth)

        val ellipsizedWidth = sbisTextView.ellipsizedWidth

        assertEquals(expectedEllipsizedWidth, ellipsizedWidth)
        verify(textLayout).ellipsizedWidth
        verifyNoMoreInteractions(textLayout)
    }

    @Test
    fun `When call setIncludeFontPadding, then call configure on TextLayout`() {
        sbisTextView.includeFontPadding = true

        verify(textLayout).configure(any())
        verifyNoMoreInteractions(textLayout)
    }

    @Test
    fun `When call getIncludeFontPadding, then return includeFontPad from TextLayout`() {
        val expectedIncludeFontPad = false
        whenever(textLayout.includeFontPad).thenReturn(expectedIncludeFontPad)

        val includeFontPadding = sbisTextView.includeFontPadding

        assertEquals(expectedIncludeFontPad, includeFontPadding)
        verify(textLayout).includeFontPad
        verifyNoMoreInteractions(textLayout)
    }

    @Test
    fun `When call getPaint, then return paint of TextLayout`() {
        val mockPaint = mock<TextPaint>()
        whenever(textLayout.textPaint).thenReturn(mockPaint)

        val paint = sbisTextView.paint

        assertEquals(mockPaint, paint)

        verify(textLayout).textPaint
        verifyNoMoreInteractions(textLayout)
    }

    @Test
    fun `When call setPaintFlags, then call configure on TextLayout`() {
        sbisTextView.paintFlags = 2123

        verify(textLayout).configure(any())
        verifyNoMoreInteractions(textLayout)
    }

    @Test
    fun `When call setTransformationMethod, then call configure on TextLayout`() {
        sbisTextView.transformationMethod = AllCapsTransformationMethod()

        verify(textLayout).configure(any())
        verify(textLayout).text
        verifyNoMoreInteractions(textLayout)
    }

    @Test
    fun `When call setBreakStrategy, then call configure on TextLayout`() {
        sbisTextView.breakStrategy = Layout.BREAK_STRATEGY_BALANCED

        verify(textLayout).configure(any())
        verifyNoMoreInteractions(textLayout)
    }

    @Test
    fun `When call getBreakStrategy, then return breakStrategy from TextLayout`() {
        val expectedBreakStrategy = Layout.BREAK_STRATEGY_HIGH_QUALITY
        whenever(textLayout.breakStrategy).thenReturn(expectedBreakStrategy)

        val breakStrategy = sbisTextView.breakStrategy

        assertEquals(expectedBreakStrategy, breakStrategy)
        verify(textLayout).breakStrategy
        verifyNoMoreInteractions(textLayout)
    }

    @Test
    fun `When call setHyphenationFrequency, then call configure on TextLayout`() {
        sbisTextView.hyphenationFrequency = Layout.HYPHENATION_FREQUENCY_FULL

        verify(textLayout).configure(any())
        verifyNoMoreInteractions(textLayout)
    }

    @Test
    fun `When call getHyphenationFrequency, then return hyphenationFrequency from TextLayout`() {
        val expectedHyphenationFrequency = Layout.HYPHENATION_FREQUENCY_FULL
        whenever(textLayout.hyphenationFrequency).thenReturn(expectedHyphenationFrequency)

        val hyphenationFrequency = sbisTextView.hyphenationFrequency

        assertEquals(expectedHyphenationFrequency, hyphenationFrequency)
        verify(textLayout).hyphenationFrequency
        verifyNoMoreInteractions(textLayout)
    }

    @Test
    fun `When call getLayout, then return layout from TextLayout`() {
        val expectedLayout = mock<Layout>()
        whenever(textLayout.requireLayout()).thenReturn(expectedLayout)

        val layout = sbisTextView.layout

        assertEquals(expectedLayout, layout)
        verify(textLayout).requireLayout()
        verifyNoMoreInteractions(textLayout)
    }

    @Test
    fun `When call setWidth, then call configure twice on TextLayout for max and min width`() {
        sbisTextView.width = 100

        verify(textLayout, times(2)).configure(any())
        verifyNoMoreInteractions(textLayout)
    }

    @Test
    fun `When call setHeight, then call configure twice on TextLayout for max and min height`() {
        sbisTextView.height = 100

        verify(textLayout, times(2)).configure(any())
        verifyNoMoreInteractions(textLayout)
    }
}
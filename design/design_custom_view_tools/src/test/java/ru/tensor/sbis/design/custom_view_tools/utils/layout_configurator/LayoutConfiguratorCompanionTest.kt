package ru.tensor.sbis.design.custom_view_tools.utils.layout_configurator

import android.graphics.Color
import android.graphics.Paint
import android.os.Build
import android.text.BoringLayout
import android.text.Layout
import android.text.Spanned
import android.text.TextPaint
import android.text.TextUtils.TruncateAt
import android.text.style.BackgroundColorSpan
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.apache.commons.lang3.StringUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import ru.tensor.sbis.design.custom_view_tools.utils.HighlightSpan
import ru.tensor.sbis.design.custom_view_tools.utils.TextHighlights
import ru.tensor.sbis.design.custom_view_tools.utils.getTextWidth
import ru.tensor.sbis.design.custom_view_tools.utils.layout.LayoutConfigurator.createLayout
import ru.tensor.sbis.design.custom_view_tools.utils.layout.LayoutConfigurator

/**
 * Тесты [LayoutConfigurator].
 *
 * @see LayoutConfigurator.createLayout
 *
 * @author vv.chekurda
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class LayoutConfiguratorCompanionTest {

    private val textPaint = TextPaint()
    private val testText = "Test string"
    private val testBoring = BoringLayout.isBoring(testText, textPaint)

    @Test
    fun `When width is default, then layout width is equals full text width`() {
        val expectedTextWidth = textPaint.getTextWidth(testText)

        val layout = createLayout(testText, textPaint)

        assertEquals(expectedTextWidth, layout.width)
        assertNotEquals(0, layout.width)
        assertTrue(testText.isNotBlank())
    }

    @Test
    fun `When width is default and boring is set, then layout width is equals full text width`() {
        val expectedTextWidth = textPaint.getTextWidth(testText)

        val layout = createLayout(testText, textPaint) {
            boring = testBoring
        }

        assertEquals(expectedTextWidth, layout.width)
        assertNotEquals(0, layout.width)
        assertTrue(testText.isNotBlank())
    }

    @Test
    fun `When custom width is bigger than text width, then layout width is equals custom width`() {
        val fullTextWidth = textPaint.getTextWidth(testText)
        val customWidth = fullTextWidth + 100

        val layout = createLayout(testText, textPaint) {
            width = customWidth
        }

        assertEquals(customWidth, layout.width)
        assertTrue(layout.width > fullTextWidth)
        assertNotEquals(0, fullTextWidth)
    }

    @Test
    fun `When custom width is bigger than text width and boring is set, then layout width is equals custom width`() {
        val fullTextWidth = textPaint.getTextWidth(testText)
        val customWidth = fullTextWidth + 100

        val layout = createLayout(testText, textPaint) {
            boring = testBoring
            width = customWidth
        }

        assertEquals(customWidth, layout.width)
        assertTrue(layout.width > fullTextWidth)
        assertNotEquals(0, fullTextWidth)
    }

    @Test
    fun `When custom width is negative, then layout width is equals 0`() {
        val customWidth = -50

        val layout = createLayout(testText, textPaint) {
            width = customWidth
        }

        assertEquals(0, layout.width)
        assertTrue(testText.isNotBlank())
    }

    @Test
    fun `When custom width is smaller than text width, then layout width is equals custom width`() {
        val fullTextWidth = textPaint.getTextWidth(testText)
        val customWidth = fullTextWidth - 1

        val layout = createLayout(testText, textPaint) {
            width = customWidth
        }

        assertEquals(customWidth, layout.width)
        assertTrue(layout.width < fullTextWidth)
        assertNotEquals(0, fullTextWidth)
    }

    @Test
    fun `When custom width is smaller than text width and boring is set, then layout width is equals custom width`() {
        val fullTextWidth = textPaint.getTextWidth(testText)
        val customWidth = fullTextWidth - 1

        val layout = createLayout(testText, textPaint) {
            width = customWidth
            boring = testBoring
        }

        assertEquals(customWidth, layout.width)
        assertTrue(layout.width < fullTextWidth)
        assertNotEquals(0, fullTextWidth)
    }

    @Test
    fun `When text is empty, then layout width is equals custom width`() {
        val text = StringUtils.EMPTY
        val customWidth = 50

        val layout = createLayout(text, textPaint) {
            width = customWidth
        }

        assertEquals(customWidth, layout.width)
        assertTrue(text.isEmpty())
    }

    @Test
    fun `When text is empty and boring is set, then layout width is equals custom width`() {
        val text = StringUtils.EMPTY
        val customWidth = 50

        val layout = createLayout(text, textPaint) {
            width = customWidth
            boring = testBoring
        }

        assertEquals(customWidth, layout.width)
        assertTrue(text.isEmpty())
    }

    @Test
    fun `When maxLines is set and ellipsize is END, then layout line count don't bigger than custom`() {
        val customMaxLines = 5
        val testText = "1\n2\n3\n4\n5\n6\n7"

        val layout = createLayout(testText, textPaint) {
            maxLines = customMaxLines
            ellipsize = TruncateAt.END
        }

        assertEquals(customMaxLines, layout.lineCount)
        assertTrue(testText.isNotBlank())
    }

    @Test
    fun `When maxLines is set and ellipsize is null, then layout line count can be bigger than custom`() {
        val customMaxLines = 5
        val testText = "1\n2\n3\n4\n5\n6\n7"

        val layout = createLayout(testText, textPaint) {
            maxLines = customMaxLines
            ellipsize = null
        }

        assertTrue(layout.lineCount > customMaxLines)
        assertTrue(testText.isNotBlank())
    }

    @Test
    fun `When maxLines is equals 0, then layout line count is equals 1`() {
        val customMaxLines = 0
        val expectedLineCount = 1

        val layout = createLayout(testText, textPaint) {
            maxLines = customMaxLines
        }

        assertEquals(expectedLineCount, layout.lineCount)
    }

    @Test
    fun `When maxLines is negative, then layout line count is equals 1`() {
        val customMaxLines = -5
        val expectedLineCount = 1

        val layout = createLayout(testText, textPaint) {
            maxLines = customMaxLines
        }

        assertEquals(expectedLineCount, layout.lineCount)
    }

    @Test
    fun `When maxHeight is set, then layout line count divided by text height`() {
        val testText = "1\n2\n3\n4\n5\n6\n7"
        val textHeight = 10f
        val mockTextPaint = mockTextPaint(textHeight = textHeight)
        val customMaxHeight = 25
        val expectedLineCount = 2

        val layout = createLayout(testText, mockTextPaint) {
            maxHeight = customMaxHeight
        }

        assertEquals(expectedLineCount, layout.lineCount)
    }

    @Test
    fun `When maxHeight is negative, then layout line count is equals 1`() {
        val mockTextPaint = mockTextPaint(textHeight = 10f)
        val customMaxHeight = -50
        val expectedLineCount = 1

        val layout = createLayout(testText, mockTextPaint) {
            maxHeight = customMaxHeight
        }

        assertEquals(expectedLineCount, layout.lineCount)
    }

    @Test
    fun `When maxHeight is smaller than one line, then layout line count is equals 1`() {
        val textHeight = 20f
        val mockTextPaint = mockTextPaint(textHeight = textHeight)
        val customMaxHeight = 10
        val expectedLineCount = 1

        val layout = createLayout(testText, mockTextPaint) {
            maxHeight = customMaxHeight
        }

        assertEquals(expectedLineCount, layout.lineCount)
    }

    @Test
    fun `When maxHeight is set, then maxLines will be ignored`() {
        val testText = "1\n2\n3\n4\n5\n6\n7"
        val mockTextPaint = mockTextPaint(textHeight = 10f)
        val customMaxHeight = 10
        val customMaxLines = 3
        val expectedLineCount = 1

        val layout = createLayout(testText, mockTextPaint) {
            maxHeight = customMaxHeight
            maxLines = customMaxLines
        }

        assertEquals(expectedLineCount, layout.lineCount)
    }

    @Test
    fun `When alignment is default, then layout alignment is equals ALIGN_NORMAL`() {
        val expectedAlignment = Layout.Alignment.ALIGN_NORMAL

        val layout = createLayout(testText, textPaint)

        assertEquals(expectedAlignment, layout.alignment)
    }

    @Test
    fun `When alignment is set, then layout alignment is equals custom alignment`() {
        val customAlignment = Layout.Alignment.ALIGN_CENTER

        val layout = createLayout(testText, textPaint) {
            alignment = customAlignment
        }

        assertEquals(customAlignment, layout.alignment)
    }

    @Test
    fun `When ellipsize is default and text bigger than width, then layout ellipsized width is smaller than text width`() {
        val textWidth = textPaint.measureText(testText).toInt()
        val customWidth = textWidth - 1

        val layout = createLayout(testText, textPaint) {
            width = customWidth
        }

        assertTrue(layout.ellipsizedWidth < textWidth)
        assertTrue(customWidth > 0)
        assertTrue(textWidth > customWidth)
    }

    @Test
    fun `When text is String and highlights is default, then layout text is not Spanned`() {
        val layout = createLayout(testText, textPaint)

        assertFalse(layout.text is Spanned)
    }

    @Test
    fun `When highlights is set, but positions is empty, then layout text is not Spanned`() {
        val customHighlights = TextHighlights(listOf(), Color.YELLOW)

        val layout = createLayout(testText, textPaint) {
            highlights = customHighlights
        }

        assertFalse(layout.text is Spanned)
    }

    @Test
    fun `When highlights is set with positions, then layout text is Spanned`() {
        val highlightSpan = HighlightSpan(0, 1)
        val customHighlights = TextHighlights(listOf(highlightSpan), Color.YELLOW)

        val layout = createLayout(testText, textPaint) {
            highlights = customHighlights
        }

        assertTrue(layout.text is Spanned)
    }

    @Test
    fun `When highlights is set with positions, then layout text contains this spans`() {
        val highlightSpan1 = HighlightSpan(0, 1)
        val highlightSpan2 = HighlightSpan(1, 2)
        val customHighlights = TextHighlights(listOf(highlightSpan1, highlightSpan2), Color.YELLOW)
        val expectedSpansCount = 2

        val layout = createLayout(testText, textPaint) {
            highlights = customHighlights
        }
        val resultSpansCount = (layout.text as? Spanned)?.getSpans(
            highlightSpan1.start,
            highlightSpan2.end,
            BackgroundColorSpan::class.java
        )?.size ?: 0

        assertTrue(layout.text is Spanned)
        assertEquals(expectedSpansCount, resultSpansCount)
    }

    @Test
    fun `When isSingleLine is true, then line count is 1 for multi line text`() {
        val testText = "1\n2\n3\n4\n5\n6\n7"
        val expectedLineCount = 1

        val layout = createLayout(testText, textPaint) {
            isSingleLine = true
        }

        assertEquals(expectedLineCount, layout.lineCount)
    }

    @Test
    fun `When isSingleLine is true and boring is set, then line count is 1 for multi line text`() {
        val testText = "1\n2\n3\n4\n5\n6\n7"
        val expectedLineCount = 1

        val layout = createLayout(testText, textPaint) {
            isSingleLine = true
            boring = testBoring
        }

        assertEquals(expectedLineCount, layout.lineCount)
    }

    private fun mockTextPaint(textHeight: Float): TextPaint =
        mock {
            val fontMetrics = Paint.FontMetrics().apply {
                descent = textHeight
                ascent = 0f
            }
            on { this.fontMetrics } doReturn fontMetrics
        }
}
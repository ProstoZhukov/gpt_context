package ru.tensor.sbis.design.sbis_text_view.measure

import android.content.Context
import android.os.Build
import androidx.core.view.updatePadding
import androidx.test.core.app.ApplicationProvider
import org.apache.commons.lang3.StringUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import ru.tensor.sbis.design.custom_view_tools.utils.MeasureSpecUtils.makeAtMostSpec
import ru.tensor.sbis.design.custom_view_tools.utils.MeasureSpecUtils.makeExactlySpec
import ru.tensor.sbis.design.custom_view_tools.utils.MeasureSpecUtils.makeUnspecifiedSpec
import ru.tensor.sbis.design.custom_view_tools.utils.layout.LayoutCreator
import ru.tensor.sbis.design.sbis_text_view.SbisTextView

/**
 * Тесты измерений компонента [SbisTextView].
 *
 * @author vv.chekurda
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class SbisTextViewMeasureTest {

    private val context: Context = ApplicationProvider.getApplicationContext()
    private lateinit var sbisTextView: SbisTextView

    @Before
    fun setUp() {
        sbisTextView = SbisTextView(context)
    }

    @Test
    fun `Measure exactly`() {
        val expectedWidth = 100
        val expectedHeight = 1000
        val widthSpec = makeExactlySpec(expectedWidth)
        val heightSpec = makeExactlySpec(expectedHeight)

        sbisTextView.measure(widthSpec, heightSpec)

        assertEquals(expectedWidth, sbisTextView.measuredWidth)
        assertEquals(expectedHeight, sbisTextView.measuredHeight)
    }

    @Test
    fun `When measure exactly, then ignore paddings`() {
        val expectedWidth = 100
        val expectedHeight = 1000
        val widthSpec = makeExactlySpec(expectedWidth)
        val heightSpec = makeExactlySpec(expectedHeight)

        sbisTextView.updatePadding(
            left = expectedWidth * 2,
            top = expectedHeight * 2,
            right = expectedWidth * 2,
            bottom = expectedHeight * 2
        )
        sbisTextView.measure(widthSpec, heightSpec)

        assertEquals(expectedWidth, sbisTextView.measuredWidth)
        assertEquals(expectedHeight, sbisTextView.measuredHeight)

        assertTrue(sbisTextView.paddingStart > sbisTextView.measuredWidth)
        assertTrue(sbisTextView.paddingEnd > sbisTextView.measuredWidth)
        assertTrue(sbisTextView.paddingTop > sbisTextView.measuredHeight)
        assertTrue(sbisTextView.paddingBottom > sbisTextView.measuredHeight)
    }

    @Test
    fun `When measure unspecified width with empty text, then width is equals 0`() {
        sbisTextView.text = StringUtils.EMPTY
        sbisTextView.measure(makeUnspecifiedSpec(), makeUnspecifiedSpec())

        assertEquals(0, sbisTextView.measuredWidth)
    }

    @Test
    fun `When measure unspecified height with empty text, then height is equals height of empty Layout`() {
        val expectedHeight = LayoutCreator.createLayout(
            StringUtils.EMPTY,
            sbisTextView.paint,
            0,
            includeFontPad = true
        ).height

        sbisTextView.text = StringUtils.EMPTY
        sbisTextView.measure(makeUnspecifiedSpec(), makeUnspecifiedSpec())

        assertEquals(expectedHeight, sbisTextView.measuredHeight)
        assertTrue(expectedHeight > 0)
    }

    @Test
    fun `When measure unspecified width with empty text and horizontal paddings, then width is equals horizontal paddings`() {
        val paddingStart = 100
        val paddingEnd = 200
        val expectedWidth = paddingStart + paddingEnd

        sbisTextView.updatePadding(left = paddingStart, right = paddingEnd)
        sbisTextView.measure(makeUnspecifiedSpec(), makeUnspecifiedSpec())

        assertEquals(expectedWidth, sbisTextView.measuredWidth)
    }

    @Test
    fun `When measure unspecified width with empty text and vertical paddings, then height is equals vertical paddings + Layout height`() {
        val paddingTop = 100
        val paddingBottom = 200
        val layoutHeight = LayoutCreator.createLayout(
            StringUtils.EMPTY,
            sbisTextView.paint,
            0,
            includeFontPad = true
        ).height
        val expectedHeight = paddingTop + layoutHeight + paddingBottom

        sbisTextView.updatePadding(top = paddingTop, bottom = paddingBottom)
        sbisTextView.measure(makeUnspecifiedSpec(), makeUnspecifiedSpec())

        assertEquals(expectedHeight, sbisTextView.measuredHeight)
    }

    @Test
    fun `When measure unspecified width with text, then width is equals text width`() {
        val text = "12345"
        val textWidth = sbisTextView.measureText(text).toInt()

        sbisTextView.text = text
        sbisTextView.measure(makeUnspecifiedSpec(), makeUnspecifiedSpec())

        assertEquals(textWidth, sbisTextView.measuredWidth)
        assertTrue(textWidth > 0)
    }

    @Test
    fun `When measure unspecified and text has two lines, then height is equals height of Layout with 2 lines`() {
        val text = "12345\n12345"
        val expectedLayoutHeight = LayoutCreator.createLayout(
            text = text,
            paint = sbisTextView.paint,
            width = Int.MAX_VALUE,
            includeFontPad = true
        ).height

        sbisTextView.text = text
        sbisTextView.measure(makeUnspecifiedSpec(), makeUnspecifiedSpec())

        assertEquals(expectedLayoutHeight, sbisTextView.measuredHeight)
        assertEquals(2, sbisTextView.lineCount)
        assertTrue(sbisTextView.measuredHeight > 0)
    }

    @Test
    fun `When set some width and measure exactly, then width is equals spec width`() {
        val width = 400
        val specWidth = width * 2

        sbisTextView.width = width
        sbisTextView.measure(makeExactlySpec(specWidth), makeUnspecifiedSpec())

        assertEquals(specWidth, sbisTextView.measuredWidth)
    }

    @Test
    fun `When set some height and measure exactly, then height is equals spec height`() {
        val height = 400
        val specHeight = height * 2

        sbisTextView.height = height
        sbisTextView.measure(makeUnspecifiedSpec(), makeExactlySpec(specHeight))

        assertEquals(specHeight, sbisTextView.measuredHeight)
    }

    @Test
    fun `When set some width and measure unspecified, then width is equals some width`() {
        val width = 400

        sbisTextView.width = width
        sbisTextView.measure(makeUnspecifiedSpec(), makeUnspecifiedSpec())

        assertEquals(width, sbisTextView.measuredWidth)
    }

    @Test
    fun `When set some height and measure unspecified, then height is equals some height`() {
        val height = 400

        sbisTextView.height = height
        sbisTextView.measure(makeUnspecifiedSpec(), makeUnspecifiedSpec())

        assertEquals(height, sbisTextView.measuredHeight)
    }

    @Test
    fun `When set width and view has paddings, then width included this paddings`() {
        val padding = 500
        val width = 400

        sbisTextView.width = width
        sbisTextView.updatePadding(left = padding, right = padding)
        sbisTextView.measure(makeUnspecifiedSpec(), makeUnspecifiedSpec())

        assertEquals(width, sbisTextView.measuredWidth)
    }

    @Test
    fun `When set height and view has paddings, then height included this paddings`() {
        val padding = 500
        val height = 400

        sbisTextView.height = height
        sbisTextView.updatePadding(top = padding, bottom = padding)
        sbisTextView.measure(makeUnspecifiedSpec(), makeUnspecifiedSpec())

        assertEquals(height, sbisTextView.measuredHeight)
    }

    @Test
    fun `When set maxWidth and view has paddings, then width included this paddings`() {
        val padding = 500
        val maxWidth = 400

        sbisTextView.maxWidth = maxWidth
        sbisTextView.updatePadding(left = padding, right = padding)
        sbisTextView.measure(makeUnspecifiedSpec(), makeUnspecifiedSpec())

        assertEquals(maxWidth, sbisTextView.measuredWidth)
    }

    @Test
    fun `When set maxHeight and view has paddings, then height included this paddings`() {
        val padding = 500
        val maxHeight = 400

        sbisTextView.maxHeight = maxHeight
        sbisTextView.updatePadding(top = padding, bottom = padding)
        sbisTextView.measure(makeUnspecifiedSpec(), makeUnspecifiedSpec())

        assertEquals(maxHeight, sbisTextView.measuredHeight)
    }

    @Test
    fun `When set minWidth and view is empty, then measuredWidth is equals minWidth`() {
        val minWidth = 400

        sbisTextView.minWidth = minWidth
        sbisTextView.measure(makeUnspecifiedSpec(), makeUnspecifiedSpec())

        assertEquals(minWidth, sbisTextView.measuredWidth)
    }

    @Test
    fun `When set minHeight and view is empty, then measuredHeight is equals minHeight`() {
        val minHeight = 400

        sbisTextView.minHeight = minHeight
        sbisTextView.measure(makeUnspecifiedSpec(), makeUnspecifiedSpec())

        assertEquals(minHeight, sbisTextView.measuredHeight)
    }

    @Test
    fun `When measure atMost width and text is empty, then width is equals 0`() {
        val atMostWidth = 500

        sbisTextView.text = StringUtils.EMPTY
        sbisTextView.measure(makeAtMostSpec(atMostWidth), makeUnspecifiedSpec())

        assertEquals(0, sbisTextView.measuredWidth)
    }

    @Test
    fun `When measure atMost width and text width is bigger, then width is equals size from spec`() {
        val text = "99999999999999999999999999999999"
        val textWidth = sbisTextView.measureText(text).toInt()
        val atMostWidth = textWidth / 2

        sbisTextView.text = text
        sbisTextView.measure(makeAtMostSpec(atMostWidth), makeUnspecifiedSpec())

        assertEquals(atMostWidth, sbisTextView.measuredWidth)
        assertTrue(textWidth > atMostWidth)
    }

    @Test
    fun `When measure atMost height and text is empty, then height is equals Layout one line height`() {
        val atMostHeight = 500
        val expectedHeight = LayoutCreator.createLayout(
            StringUtils.EMPTY,
            sbisTextView.paint,
            0,
            includeFontPad = true
        ).height

        sbisTextView.text = StringUtils.EMPTY
        sbisTextView.measure(makeUnspecifiedSpec(), makeAtMostSpec(atMostHeight))

        assertEquals(expectedHeight, sbisTextView.measuredHeight)
        assertTrue(atMostHeight > expectedHeight)
    }

    @Test
    fun `When measure atMost height and text height is bigger, then height is equals size from spec`() {
        val text = "9\n9\n9\n9\n9"
        val textHeight = LayoutCreator.createLayout(
            text = text,
            paint = sbisTextView.paint,
            width = Int.MAX_VALUE,
            includeFontPad = true
        ).height
        val atMostHeight = textHeight / 2

        sbisTextView.text = text
        sbisTextView.measure(makeUnspecifiedSpec(), makeAtMostSpec(atMostHeight))

        assertEquals(atMostHeight, sbisTextView.measuredHeight)
        assertTrue(textHeight > atMostHeight)
    }

    @Test
    fun `When measure atMost width and view has big paddings, then width is equals size from spec`() {
        val atMostWidth = 500
        val padding = atMostWidth * 2

        sbisTextView.updatePadding(left = padding)
        sbisTextView.measure(makeAtMostSpec(atMostWidth), makeUnspecifiedSpec())

        assertEquals(atMostWidth, sbisTextView.measuredWidth)
        assertTrue(sbisTextView.paddingStart > atMostWidth)
    }

    @Test
    fun `When measure atMost height and view has big paddings, then height is equals size from spec`() {
        val atMostHeight = 500
        val padding = atMostHeight * 2

        sbisTextView.updatePadding(top = padding)
        sbisTextView.measure(makeUnspecifiedSpec(), makeAtMostSpec(atMostHeight))

        assertEquals(atMostHeight, sbisTextView.measuredHeight)
        assertTrue(sbisTextView.paddingTop > atMostHeight)
    }
}
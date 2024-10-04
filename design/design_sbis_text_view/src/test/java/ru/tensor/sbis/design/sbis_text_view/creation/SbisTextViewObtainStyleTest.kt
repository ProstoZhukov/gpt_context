package ru.tensor.sbis.design.sbis_text_view.creation

import android.content.Context
import android.os.Build
import android.text.Layout
import android.text.TextUtils.TruncateAt
import android.view.Gravity
import androidx.core.content.ContextCompat
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import ru.tensor.sbis.design.custom_view_tools.utils.dp
import ru.tensor.sbis.design.sbis_text_view.R
import ru.tensor.sbis.design.R as RDesign
import ru.tensor.sbis.design.sbis_text_view.SbisTextView

/**
 * Тесты считывания атрибутов при инициализации компонента [SbisTextView].
 *
 * @author vv.chekurda
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class SbisTextViewObtainStyleTest {

    private val context: Context = ApplicationProvider.getApplicationContext()

    @Test
    fun `Obtain attrs`() {
        val sbisTextView = SbisTextView(context, R.style.SbisTextViewTestStyle_AllAttrs)
        val expectedText = "12345"
        val expectedTextSize = 100f
        val expectedTextColor = ContextCompat.getColor(context, RDesign.color.palette_color_white0)
        val expectedMaxLines = 5
        val expectedMinLines = 4
        val expectedAllCaps = true
        val expectedIncludeFontPad = false
        val expectedMaxLength = 400
        val expectedMinHeight = 200
        val expectedMaxHeight = 400
        val expectedMinWidth = 300
        val expectedMaxWidth = 500
        val expectedEnabled = false
        val expectedFadingEdgeLength = 9
        val expectedRequiredFadingEdge = true
        val expectedGravity = Gravity.BOTTOM
        val expectedEllipsize = TruncateAt.MARQUEE
        val expectedBreakStrategy = Layout.BREAK_STRATEGY_HIGH_QUALITY
        val expectedHyphenationFrequency = Layout.HYPHENATION_FREQUENCY_FULL

        assertEquals(expectedText, sbisTextView.text)
        assertEquals(expectedTextSize, sbisTextView.textSize)
        assertEquals(expectedTextColor, sbisTextView.textColor)
        assertEquals(expectedMaxLines, sbisTextView.maxLines)
        assertEquals(expectedMinLines, sbisTextView.minLines)
        assertEquals(expectedAllCaps, sbisTextView.allCaps)
        assertEquals(expectedIncludeFontPad, sbisTextView.includeFontPadding)
        assertEquals(expectedMaxLength, sbisTextView.maxLength)
        assertEquals(expectedMinHeight, sbisTextView.minHeight)
        assertEquals(expectedMaxHeight, sbisTextView.maxHeight)
        assertEquals(expectedMinWidth, sbisTextView.minWidth)
        assertEquals(expectedMaxWidth, sbisTextView.maxWidth)
        assertEquals(expectedEnabled, sbisTextView.isEnabled)
        assertEquals(expectedFadingEdgeLength, sbisTextView.horizontalFadingEdgeLength)
        assertEquals(expectedRequiredFadingEdge, sbisTextView.isHorizontalFadingEdgeEnabled)
        assertEquals(expectedGravity, sbisTextView.gravity)
        assertEquals(expectedEllipsize, sbisTextView.ellipsize)
        assertEquals(expectedBreakStrategy, sbisTextView.breakStrategy)
        assertEquals(expectedHyphenationFrequency, sbisTextView.hyphenationFrequency)
    }

    @Test
    fun `When obtain singleLine true, then isSingleLine is true and ellipsize is END`() {
        val sbisTextView = SbisTextView(context, R.style.SbisTextViewTestStyle_SingleLine)

        assertTrue(sbisTextView.isSingleLine)
        assertEquals(TruncateAt.END, sbisTextView.ellipsize)
    }

    @Test
    fun `When obtain singleLine true with ellipsize none, then isSingleLine is true and ellipsize is none`() {
        val sbisTextView = SbisTextView(context, R.style.SbisTextViewTestStyle_SingleLine_WithEllipsize)

        assertTrue(sbisTextView.isSingleLine)
        assertEquals(null, sbisTextView.ellipsize)
    }

    @Test
    fun `When obtain singleLine true with max and min lines, then maxLines and minLines is 1`() {
        val sbisTextView = SbisTextView(context, R.style.SbisTextViewTestStyle_SingleLine_WithMaxMinLines)

        assertEquals(1, sbisTextView.maxLines)
        assertEquals(1, sbisTextView.minLines)
    }

    @Test
    fun `When style has textAppearance and textSize attr, then apply textSize attr`() {
        val sbisTextView = SbisTextView(context, R.style.SbisTextViewTestStyle_WithAppearance_WithTextSize)
        val expectedTextSize = context.resources.dp(100).toFloat()

        assertEquals(expectedTextSize, sbisTextView.textSize)
    }

    @Test
    fun `When style has only textAppearance, then apply textSize from textAppearance`() {
        val sbisTextView = SbisTextView(context, R.style.SbisTextViewTestStyle_WithAppearance)
        val expectedTextSize = context.resources.dp(25).toFloat()

        assertEquals(expectedTextSize, sbisTextView.textSize)
    }

    @Test
    fun `When style has textAppearance and textColor attr, then apply textColor from attr`() {
        val sbisTextView = SbisTextView(context, R.style.SbisTextViewTestStyle_WithAppearance_WithTextColor)
        val expectedTextColor = ContextCompat.getColor(context, RDesign.color.palette_color_white0)

        assertEquals(expectedTextColor, sbisTextView.textColor)
    }

    @Test
    fun `When style has textAppearance, then apply textColor from textAppearance`() {
        val sbisTextView = SbisTextView(context, R.style.SbisTextViewTestStyle_WithAppearance)
        val expectedTextColor = ContextCompat.getColor(context, RDesign.color.palette_color_black3)

        assertEquals(expectedTextColor, sbisTextView.textColor)
    }

    @Test
    fun `When style has lines, minLines and maxLines attrs, then apply max and min lines is equals lines`() {
        val sbisTextView = SbisTextView(context, R.style.SbisTextViewTestStyle_WithLines)
        val expectedLines = 100

        assertEquals(expectedLines, sbisTextView.maxLines)
        assertEquals(expectedLines, sbisTextView.minLines)
    }
}
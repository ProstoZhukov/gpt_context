package ru.tensor.sbis.design.sbis_text_view

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Build
import android.text.BoringLayout
import android.text.Layout
import android.text.Layout.Alignment
import android.text.Spanned
import android.text.TextUtils
import android.text.style.BackgroundColorSpan
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import androidx.core.content.ContextCompat
import androidx.test.core.app.ApplicationProvider
import org.apache.commons.lang3.StringUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import ru.tensor.sbis.design.TypefaceManager
import ru.tensor.sbis.design.custom_view_tools.utils.HighlightSpan
import ru.tensor.sbis.design.custom_view_tools.utils.TextHighlights
import ru.tensor.sbis.design.custom_view_tools.utils.dp
import ru.tensor.sbis.design.sbis_text_view.utils.AllCapsTransformationMethod
import ru.tensor.sbis.design.R as RDesign

/**
 * Тесты API компонента [SbisTextView].
 *
 * @author vv.chekurda
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class SbisTextViewTest {

    private val context: Context = ApplicationProvider.getApplicationContext()

    private lateinit var sbisTextView: SbisTextView

    @Before
    fun setUp() {
        sbisTextView = SbisTextView(context)
    }

    @Test
    fun `Default text is empty string`() {
        val emptyString = StringUtils.EMPTY

        assertEquals(sbisTextView.text, emptyString)
        assertTrue(emptyString.isEmpty())
    }

    @Test
    fun `Text can't return null`() {
        sbisTextView.text = "abc"
        sbisTextView.text = null

        assertNotNull(sbisTextView.text)
        assertEquals(StringUtils.EMPTY, sbisTextView.text)
    }

    @Test
    fun `Set text`() {
        val text = "123"

        sbisTextView.text = text

        assertEquals(text, sbisTextView.text)
        assertEquals(text, sbisTextView.layout.text)
    }

    @Test
    fun `Set textSize`() {
        val textSize = 100f

        sbisTextView.textSize = textSize

        assertEquals(textSize, sbisTextView.textSize)
        assertEquals(textSize, sbisTextView.layout.paint.textSize)
    }

    @Test
    fun `Set textColor`() {
        val textColor = Color.RED

        sbisTextView.setTextColor(textColor)

        assertEquals(textColor, sbisTextView.textColor)
        assertEquals(textColor, sbisTextView.layout.paint.color)
    }

    @Test
    fun `When set textColor, then textColors contains new color`() {
        val textColor = Color.RED

        sbisTextView.setTextColor(textColor)

        assertEquals(textColor, sbisTextView.textColors.defaultColor)
    }

    @Test
    fun `Set textColors`() {
        val textColors = ColorStateList.valueOf(Color.RED)

        sbisTextView.setTextColor(textColors)

        assertEquals(textColors, sbisTextView.textColors)
    }

    @Test
    fun `When set textColors, then textColor is equals default color`() {
        val textColors = ColorStateList.valueOf(Color.RED)

        sbisTextView.setTextColor(textColors)

        assertEquals(textColors.defaultColor, sbisTextView.textColor)
    }

    @Test
    fun `When linkTextColors is null, then default linkTextColor is textColor`() {
        sbisTextView.linkTextColors = null

        assertEquals(sbisTextView.textColor, sbisTextView.linkTextColor)
    }

    @Test
    fun `Set linkText color`() {
        val linkTextColor = Color.RED

        sbisTextView.linkTextColor = linkTextColor

        assertEquals(linkTextColor, sbisTextView.linkTextColor)
    }

    @Test
    fun `When set linkTextColor, then linkTextColors contains new color`() {
        val linkTextColor = Color.RED

        sbisTextView.linkTextColor = linkTextColor

        assertEquals(linkTextColor, sbisTextView.linkTextColors!!.defaultColor)
    }

    @Test
    fun `Set linkColors`() {
        val linkTextColors = ColorStateList.valueOf(Color.RED)

        sbisTextView.linkTextColors = linkTextColors

        assertEquals(linkTextColors, sbisTextView.linkTextColors)
    }

    @Test
    fun `When set linkTextColors, then linkColor is equals default color`() {
        val linkTextColors = ColorStateList.valueOf(Color.RED)

        sbisTextView.linkTextColors = linkTextColors

        assertEquals(linkTextColors.defaultColor, sbisTextView.linkTextColor)
    }

    @Test
    fun `Default allCaps is false`() {
        assertFalse(sbisTextView.allCaps)
    }

    @Test
    fun `When allCaps is false, then text is not caps`() {
        val lowerCaseText = "abc"
        sbisTextView.text = lowerCaseText

        sbisTextView.allCaps = false

        assertEquals(lowerCaseText, sbisTextView.text)
    }

    @Test
    fun `When allCaps is false, then text is not all lower case`() {
        val someText = "aBc"
        sbisTextView.text = someText

        sbisTextView.allCaps = false

        assertEquals(someText, sbisTextView.text)
    }

    @Test
    fun `When allCaps is true, then text is upper cased`() {
        val lowerCaseText = "abc"
        val upperCaseText = "ABC"
        sbisTextView.text = lowerCaseText

        sbisTextView.allCaps = true

        assertEquals(upperCaseText, sbisTextView.text)
        assertNotEquals(upperCaseText, lowerCaseText)
    }

    @Test
    fun `When allCaps is true, then transformation method is AllCapsTransformationMethod`() {
        val text = "abc"
        val defaultTransformationMethod = sbisTextView.transformationMethod
        sbisTextView.text = text

        sbisTextView.allCaps = true

        assertTrue(sbisTextView.transformationMethod is AllCapsTransformationMethod)
        assertNull(defaultTransformationMethod)
    }

    @Test
    fun `When allCaps is false after true, then transformation method is null`() {
        val text = "abc"
        sbisTextView.text = text

        sbisTextView.allCaps = true
        sbisTextView.allCaps = false

        assertNull(sbisTextView.transformationMethod)
    }

    @Test
    fun `Default isSingleLine is false`() {
        assertFalse(sbisTextView.isSingleLine)
    }

    @Test
    fun `Set is single line`() {
        sbisTextView.isSingleLine = true

        assertTrue(sbisTextView.isSingleLine)
    }

    @Test
    fun `When set isSingleLine true, then max lines is 1, min lines is 1`() {
        val customLines = 4
        sbisTextView.minLines = customLines
        sbisTextView.maxLines = customLines
        val expectedLines = 1

        sbisTextView.isSingleLine = true

        assertEquals(expectedLines, sbisTextView.minLines)
        assertEquals(expectedLines, sbisTextView.maxLines)
    }

    @Test
    fun `When set isSingleLine false, then max lines and min lines become default`() {
        sbisTextView.isSingleLine = true
        sbisTextView.isSingleLine = false

        assertEquals(1, sbisTextView.minLines)
        assertEquals(Int.MAX_VALUE, sbisTextView.maxLines)
    }

    @Test
    fun `Default lines is null`() {
        assertNull(sbisTextView.lines)
    }

    @Test
    fun `Set lines`() {
        val lines = 3
        sbisTextView.lines = lines

        assertEquals(lines, sbisTextView.lines)
        assertEquals(lines, sbisTextView.minLines)
        assertEquals(lines, sbisTextView.maxLines)
    }

    @Test
    fun `When set lines, then min and max lines is equals lines`() {
        val lines = 3
        sbisTextView.lines = lines

        assertEquals(lines, sbisTextView.minLines)
        assertEquals(lines, sbisTextView.maxLines)
    }

    @Test
    fun `When set lines and maxLines is changed, then lines returns null`() {
        sbisTextView.lines = 3
        sbisTextView.maxLines = 4

        assertNull(sbisTextView.lines)
    }

    @Test
    fun `When set lines and minLines is changed, then lines returns null`() {
        sbisTextView.lines = 3
        sbisTextView.minLines = 4

        assertNull(sbisTextView.lines)
    }

    @Test
    fun `When set lines and minLines is not changed, then lines returns lines`() {
        val lines = 3
        sbisTextView.lines = lines
        sbisTextView.minLines = sbisTextView.minLines

        assertEquals(lines, sbisTextView.lines)
    }

    @Test
    fun `Default max lines is unlimited`() {
        assertEquals(Int.MAX_VALUE, sbisTextView.maxLines)
    }

    @Test
    fun `Set max lines`() {
        val maxLines = 100
        sbisTextView.maxLines = maxLines

        assertEquals(maxLines, sbisTextView.maxLines)
    }

    @Test
    fun `When set max lines null, then maxLines is default`() {
        sbisTextView.maxLines = 1
        sbisTextView.maxLines = null

        assertEquals(Int.MAX_VALUE, sbisTextView.maxLines)
    }

    @Test
    fun `Default min lines is 1`() {
        assertEquals(1, sbisTextView.minLines)
    }

    @Test
    fun `Set min lines`() {
        val minLines = 100
        sbisTextView.minLines = minLines

        assertEquals(minLines, sbisTextView.minLines)
    }

    @Test
    fun `When set min lines null, then minLines is default`() {
        sbisTextView.minLines = 100
        sbisTextView.minLines = null

        assertEquals(1, sbisTextView.minLines)
    }

    @Test
    fun `Default max height is null`() {
        assertNull(sbisTextView.maxHeight)
    }

    @Test
    fun `Set max height`() {
        val maxHeight = 100
        sbisTextView.maxHeight = maxHeight

        assertEquals(maxHeight, sbisTextView.maxHeight)
    }

    @Test
    fun `Default min height is 0`() {
        assertEquals(0, sbisTextView.minHeight)
    }

    @Test
    fun `Set min height`() {
        val minHeight = 100
        sbisTextView.minHeight = minHeight

        assertEquals(minHeight, sbisTextView.minHeight)
    }

    @Test
    fun `When set minHeight null, then minHeight is 0`() {
        sbisTextView.minHeight = 100
        sbisTextView.minHeight = null

        assertEquals(0, sbisTextView.minHeight)
    }

    @Test
    fun `Default maxLength is unlimited`() {
        assertEquals(Int.MAX_VALUE, sbisTextView.maxLength)
    }

    @Test
    fun `Set maxLength`() {
        val maxLength = 100
        sbisTextView.maxLength = maxLength

        assertEquals(maxLength, sbisTextView.maxLength)
    }

    @Test
    fun `When set maxLength null, then maxLength is unlimited`() {
        sbisTextView.maxLength = 100
        sbisTextView.maxLength = null

        assertEquals(Int.MAX_VALUE, sbisTextView.maxLength)
    }

    @Test
    fun `Default gravity is no gravity`() {
        assertEquals(Gravity.NO_GRAVITY, sbisTextView.gravity)
    }

    @Test
    fun `Set gravity`() {
        val gravity = Gravity.CENTER_HORIZONTAL
        sbisTextView.gravity = gravity

        assertEquals(gravity, sbisTextView.gravity)
    }

    @Test
    fun `When set gravity, then layout alignment changed`() {
        val gravity = Gravity.END
        val beforeLayoutAlignment = sbisTextView.layout.alignment
        sbisTextView.gravity = gravity

        assertEquals(Alignment.ALIGN_OPPOSITE, sbisTextView.layout.alignment)
        assertNotEquals(beforeLayoutAlignment, sbisTextView.layout.alignment)
    }

    @Test
    fun `Default typeface is paint typeface`() {
        sbisTextView.paint.typeface = TypefaceManager.getRobotoBoldFont(context)

        assertEquals(sbisTextView.paint.typeface, sbisTextView.typeface)
        assertNotNull(sbisTextView.typeface)
    }

    @Test
    fun `Set typeface`() {
        val typeface = TypefaceManager.getRobotoBoldFont(context)
        sbisTextView.typeface = typeface

        assertEquals(typeface, sbisTextView.typeface)
        assertEquals(typeface, sbisTextView.layout.paint.typeface)
    }

    @Test
    fun `Default ellipsize is null`() {
        assertNull(sbisTextView.ellipsize)
    }

    @Test
    fun `Set ellipsize`() {
        val ellipsize = TextUtils.TruncateAt.END
        sbisTextView.ellipsize = ellipsize

        assertEquals(ellipsize, sbisTextView.ellipsize)
    }

    @Test
    fun `Default ellipsized width is 0`() {
        assertEquals(0, sbisTextView.ellipsizedWidth)
    }

    @Test
    fun `Default includeFontPadding is true`() {
        assertTrue(sbisTextView.includeFontPadding)
    }

    @Test
    fun `Set includeFontPadding`() {
        sbisTextView.includeFontPadding = false

        assertFalse(sbisTextView.includeFontPadding)
    }

    @Test
    fun `Set paintFlags`() {
        val flags = Paint.ANTI_ALIAS_FLAG
        sbisTextView.paintFlags = flags

        assertEquals(flags, sbisTextView.paintFlags)
    }

    @Test
    fun `Default transformationMethod is null`() {
        assertNull(sbisTextView.transformationMethod)
    }

    @Test
    fun `Set transformationMethod`() {
        val method = AllCapsTransformationMethod()
        sbisTextView.transformationMethod = method

        assertEquals(method, sbisTextView.transformationMethod)
    }

    @Test
    fun `Default breakStrategy is simple`() {
        assertEquals(Layout.BREAK_STRATEGY_SIMPLE, sbisTextView.breakStrategy)
    }

    @Test
    fun `Set breakStrategy`() {
        val breakStrategy = Layout.BREAK_STRATEGY_HIGH_QUALITY
        sbisTextView.breakStrategy = breakStrategy

        assertEquals(breakStrategy, sbisTextView.breakStrategy)
    }

    @Test
    fun `Default hyphenationFrequency is none`() {
        assertEquals(Layout.HYPHENATION_FREQUENCY_NONE, sbisTextView.hyphenationFrequency)
    }

    @Test
    fun `Set hyphenationFrequency`() {
        val hyphenationFrequency = Layout.HYPHENATION_FREQUENCY_NORMAL
        sbisTextView.hyphenationFrequency = hyphenationFrequency

        assertEquals(hyphenationFrequency, sbisTextView.hyphenationFrequency)
    }

    @Test
    fun `Default layout is BoringLayout`() {
        assertTrue(sbisTextView.layout is BoringLayout)
    }

    @Test
    fun `Default layout contains empty text`() {
        assertTrue(sbisTextView.layout.text.isEmpty())
    }

    @Test
    fun `When set text, then layout contains this text`() {
        val text = "Test string"
        sbisTextView.text = text

        assertEquals(text, sbisTextView.layout.text)
    }

    @Test
    fun `Set text res`() {
        val textRes = R.string.design_sbis_text_view_test_text
        val expectedText = context.resources.getString(textRes)

        sbisTextView.setText(textRes)

        assertEquals(expectedText, sbisTextView.text)
    }

    @Test
    fun `Set text with highlights`() {
        val spanStart = 0
        val spanEnd = 1
        val highlightSpan = HighlightSpan(spanStart, spanEnd)
        val highlights = TextHighlights(listOf(highlightSpan), Color.RED)
        val text = "Test string"

        sbisTextView.setTextWithHighlights(text, highlights)

        assertEquals(text, sbisTextView.text)

        val layoutText = sbisTextView.layout.text
        val colorSpans = (layoutText as Spanned).getSpans(0, layoutText.length, BackgroundColorSpan::class.java)

        assertEquals(1, colorSpans.size)
        assertEquals(spanStart, layoutText.getSpanStart(colorSpans.first()))
        assertEquals(spanEnd, layoutText.getSpanEnd(colorSpans.first()))
    }

    @Test
    fun `Set textSize with unit px`() {
        val textSize = 20f
        sbisTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)

        assertEquals(textSize, sbisTextView.textSize)
    }

    @Test
    fun `Set textSize with unit dip`() {
        val textSize = 20f
        val density = sbisTextView.resources.displayMetrics.density
        val expectedSizePx = textSize / density

        sbisTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, textSize)

        assertEquals(expectedSizePx, sbisTextView.textSize)
        assertTrue(density > 0)
    }

    @Test
    fun `Set lineSpacing`() {
        val spacingAdd = 20f
        val spacingMulti = 40f

        sbisTextView.setLineSpacing(spacingAdd, spacingMulti)

        assertEquals(spacingAdd, sbisTextView.layout.spacingAdd)
        assertEquals(spacingMulti, sbisTextView.layout.spacingMultiplier)
    }

    @Test
    fun `Set typeface with style`() {
        val typeface = TypefaceManager.getRobotoRegularFont(context)
        val style = Typeface.BOLD

        sbisTextView.setTypeface(typeface, style)

        assertNotEquals(typeface, sbisTextView.typeface)
        assertEquals(style, sbisTextView.typeface!!.style)
    }

    @Test
    fun `Set width`() {
        val width = 200
        sbisTextView.width = width

        assertEquals(width, sbisTextView.minWidth)
        assertEquals(width, sbisTextView.maxWidth)
    }

    @Test
    fun `When set width is negative, then minWidth and maxWidth is equals 0`() {
        sbisTextView.width = 200
        sbisTextView.width = -1

        assertEquals(0, sbisTextView.minWidth)
        assertEquals(0, sbisTextView.maxWidth)
    }

    @Test
    fun `Set height`() {
        val height = 200
        sbisTextView.height = height

        assertEquals(height, sbisTextView.minHeight)
        assertEquals(height, sbisTextView.maxHeight)
    }

    @Test
    fun `When set height is negative, then minHeight and maxHeight is equals 0`() {
        sbisTextView.height = 200
        sbisTextView.height = -1

        assertEquals(0, sbisTextView.minHeight)
        assertEquals(0, sbisTextView.maxHeight)
    }

    @Test
    fun `When call measureText, then return text width`() {
        val text = "Test string"
        val textWidth = sbisTextView.paint.measureText(text)

        val result = sbisTextView.measureText(text)

        assertEquals(textWidth, result)
        assertTrue(result > 0)
    }

    @Test
    fun `When call measureText, then text doesn't set`() {
        sbisTextView.measureText("Test string")

        assertTrue(sbisTextView.text!!.isEmpty())
    }

    @Test
    fun `When call measureText with null text, then use current view text`() {
        val text = "Test string"
        val textWidth = sbisTextView.measureText(text)

        sbisTextView.text = text
        val result = sbisTextView.measureText(null)

        assertEquals(textWidth, result)
        assertTrue(result > 0)
    }

    @Test
    fun `When call setTextAppearance, then properties applied from style`() {
        val expectedTextSize = context.resources.dp(25).toFloat()
        val expectedTextColor = ContextCompat.getColor(context, RDesign.color.palette_color_black3)
        val expectedTextStyle = Typeface.BOLD
        val expectedAllCaps = true

        sbisTextView.setTextAppearance(R.style.SbisTextViewTestStyle_TextAppearance)

        assertEquals(expectedTextSize, sbisTextView.textSize)
        assertEquals(expectedTextColor, sbisTextView.textColor)
        assertEquals(expectedTextStyle, sbisTextView.typeface?.style)
        assertEquals(expectedAllCaps, sbisTextView.allCaps)
    }

    @Test
    fun `When call setTextAppearance with empty style, then properties does not changed`() {
        val expectedTextSize = sbisTextView.textSize
        val expectedTextColor = sbisTextView.textColor
        val expectedTextStyle = sbisTextView.typeface?.style
        val expectedAllCaps = sbisTextView.allCaps

        sbisTextView.setTextAppearance(R.style.SbisTextViewTestStyle)

        assertEquals(expectedTextSize, sbisTextView.textSize)
        assertEquals(expectedTextColor, sbisTextView.textColor)
        assertEquals(expectedTextStyle, sbisTextView.typeface?.style)
        assertEquals(expectedAllCaps, sbisTextView.allCaps)
    }

    @Test
    fun `When text is not ellipsized, then getEllipsisCount return 0`() {
        sbisTextView.text = "12345"

        assertEquals(0, sbisTextView.getEllipsisCount(0))
    }

    @Test
    fun `Default layout alignment is ALIGN_NORMAL`() {
        assertEquals(Alignment.ALIGN_NORMAL, sbisTextView.layout.alignment)
    }

    @Test
    fun `When set TEXT_ALIGNMENT_TEXT_START, then layout alignment is ALIGN_NORMAL`() {
        sbisTextView.textAlignment = View.TEXT_ALIGNMENT_TEXT_START

        assertEquals(Alignment.ALIGN_NORMAL, sbisTextView.layout.alignment)
    }

    @Test
    fun `When set TEXT_ALIGNMENT_VIEW_START, then layout alignment is ALIGN_NORMAL`() {
        sbisTextView.textAlignment = View.TEXT_ALIGNMENT_VIEW_START

        assertEquals(Alignment.ALIGN_NORMAL, sbisTextView.layout.alignment)
    }

    @Test
    fun `When set TEXT_ALIGNMENT_TEXT_END, then layout alignment is ALIGN_OPPOSITE`() {
        sbisTextView.textAlignment = View.TEXT_ALIGNMENT_TEXT_END

        assertEquals(Alignment.ALIGN_OPPOSITE, sbisTextView.layout.alignment)
    }

    @Test
    fun `When set TEXT_ALIGNMENT_VIEW_END, then layout alignment is ALIGN_OPPOSITE`() {
        sbisTextView.textAlignment = View.TEXT_ALIGNMENT_VIEW_END

        assertEquals(Alignment.ALIGN_OPPOSITE, sbisTextView.layout.alignment)
    }

    @Test
    fun `When set TEXT_ALIGNMENT_CENTER, then layout alignment is ALIGN_CENTER`() {
        sbisTextView.textAlignment = View.TEXT_ALIGNMENT_CENTER

        assertEquals(Alignment.ALIGN_CENTER, sbisTextView.layout.alignment)
    }

    @Test
    fun `When set TEXT_ALIGNMENT_GRAVITY and gravity is CENTER_HORIZONTAL, then layout alignment is ALIGN_CENTER`() {
        sbisTextView.textAlignment = View.TEXT_ALIGNMENT_GRAVITY
        sbisTextView.gravity = Gravity.CENTER_HORIZONTAL

        assertEquals(Alignment.ALIGN_CENTER, sbisTextView.layout.alignment)
    }

    @Test
    fun `When set TEXT_ALIGNMENT_GRAVITY and gravity is END, then layout alignment is ALIGN_OPPOSITE`() {
        sbisTextView.textAlignment = View.TEXT_ALIGNMENT_GRAVITY
        sbisTextView.gravity = Gravity.END

        assertEquals(Alignment.ALIGN_OPPOSITE, sbisTextView.layout.alignment)
    }

    @Test
    fun `When set TEXT_ALIGNMENT_GRAVITY and gravity is START, then layout alignment is ALIGN_NORMAL`() {
        sbisTextView.textAlignment = View.TEXT_ALIGNMENT_GRAVITY
        sbisTextView.gravity = Gravity.START

        assertEquals(Alignment.ALIGN_NORMAL, sbisTextView.layout.alignment)
    }

    @Test
    fun `When set TEXT_ALIGNMENT_GRAVITY and gravity is default, then layout alignment is ALIGN_NORMAL`() {
        sbisTextView.textAlignment = View.TEXT_ALIGNMENT_GRAVITY

        assertEquals(Alignment.ALIGN_NORMAL, sbisTextView.layout.alignment)
    }

    @Test
    fun `Default isEnabled is true`() {
        assertTrue(sbisTextView.isEnabled)
    }

    @Test
    fun `Set isEnabled`() {
        sbisTextView.isEnabled = false

        assertFalse(sbisTextView.isEnabled)
    }

    @Test
    fun `Default isSelected is false`() {
        assertFalse(sbisTextView.isSelected)
    }

    @Test
    fun `Set isSelected`() {
        sbisTextView.isSelected = true

        assertTrue(sbisTextView.isSelected)
    }

    @Test
    fun `Default isPressed is false`() {
        assertFalse(sbisTextView.isPressed)
    }

    @Test
    fun `When call setPressed and view does not clickable, then isPressed false`() {
        sbisTextView.isClickable = false
        sbisTextView.isPressed = true

        assertFalse(sbisTextView.isPressed)
        assertFalse(sbisTextView.isClickable)
    }

    @Test
    fun `When call setPressed and view is clickable, then isPressed true`() {
        sbisTextView.isClickable = true
        sbisTextView.isPressed = true

        assertTrue(sbisTextView.isPressed)
        assertTrue(sbisTextView.isClickable)
    }

    @Test
    fun `Default isHorizontalFadingEdgeEnabled is false`() {
        assertFalse(sbisTextView.isHorizontalFadingEdgeEnabled)
    }

    @Test
    fun `Set isHorizontalFadingEdgeEnabled`() {
        sbisTextView.isHorizontalFadingEdgeEnabled = true

        assertTrue(sbisTextView.isHorizontalFadingEdgeEnabled)
    }

    @Test
    fun `Default fadingEdgeLength is 0 `() {
        assertEquals(0, sbisTextView.horizontalFadingEdgeLength)
    }

    @Test
    fun `Set fadingEdgeLength`() {
        val fadingEdgeLength = 100
        sbisTextView.setFadingEdgeLength(fadingEdgeLength)

        assertEquals(fadingEdgeLength, sbisTextView.horizontalFadingEdgeLength)
    }
}

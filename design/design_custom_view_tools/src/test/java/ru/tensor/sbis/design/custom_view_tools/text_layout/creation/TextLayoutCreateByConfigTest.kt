package ru.tensor.sbis.design.custom_view_tools.text_layout.creation

import android.graphics.Color
import android.os.Build
import android.text.Layout
import android.text.TextPaint
import android.text.TextUtils.TruncateAt
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import ru.tensor.sbis.design.custom_view_tools.TextLayout
import ru.tensor.sbis.design.custom_view_tools.TextLayout.TextLayoutParams
import ru.tensor.sbis.design.custom_view_tools.TextLayoutConfig
import ru.tensor.sbis.design.custom_view_tools.utils.TextHighlights
import ru.tensor.sbis.design.custom_view_tools.text_layout.assertNotEqualsExcludingPaint
import ru.tensor.sbis.design.custom_view_tools.text_layout.assertParamsEquals

/**
 * Тесты создания [TextLayout] через [TextLayoutConfig].
 *
 * @author vv.chekurda
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class TextLayoutCreateByConfigTest {

    private lateinit var defaultParams: TextLayoutParams

    @Before
    fun setUp() {
        defaultParams = TextLayoutParams()
    }

    @Test
    fun `When TextLayout created by empty constructor, then TextLayout has default parameters`() {
        val layout = TextLayout()

        assertParamsEquals(defaultParams, layout.stateSnapshot.params)
    }

    @Test
    fun `When TextLayout created by config, then TextLayout's parameters has modifications from config`() {
        val params = mockTextLayoutParams()

        val layout = TextLayout {
            text = params.text
            paint.textSize = params.paint.textSize
            paint.color = params.paint.color
            layoutWidth = params.layoutWidth
            alignment = params.alignment
            ellipsize = params.ellipsize
            includeFontPad = params.includeFontPad
            maxLines = params.maxLines
            padding = params.padding
            isVisible = params.isVisible
            highlights = params.highlights
            minWidth = params.minWidth
            minHeight = params.minHeight
            maxWidth = params.maxWidth
            maxHeight = params.maxHeight
            isVisibleWhenBlank = params.isVisibleWhenBlank
        }
        val resultParams = layout.stateSnapshot.params

        assertParamsEquals(params, resultParams)
        assertNotEqualsExcludingPaint(defaultParams, resultParams)
    }

    @Test
    fun `When config modify the same parameters, then other TextLayout't parameters is default`() {
        val testText = "Test string"

        val layout = TextLayout { text = testText }
        val resultParams = layout.stateSnapshot.params

        assertEquals(testText, resultParams.text)
        assertEquals(defaultParams.alignment, resultParams.alignment)
        assertEquals(defaultParams.ellipsize, resultParams.ellipsize)
        assertEquals(defaultParams.maxLines, resultParams.maxLines)
        assertNotEqualsExcludingPaint(defaultParams, resultParams)
    }

    private fun mockTextLayoutParams() =
        TextLayoutParams(
            text = "Test string",
            paint = TextPaint().apply {
                textSize = 255f
                color = 244
            },
            layoutWidth = 400,
            alignment = Layout.Alignment.ALIGN_OPPOSITE,
            ellipsize = TruncateAt.MARQUEE,
            includeFontPad = false,
            maxLines = 200,
            padding = TextLayout.TextLayoutPadding(1, 2, 3, 4),
            isVisible = false,
            highlights = TextHighlights(listOf(), Color.YELLOW),
            minWidth = 0,
            minHeight = 0,
            maxWidth = 200,
            maxHeight = 400,
            isVisibleWhenBlank = false
        )
}
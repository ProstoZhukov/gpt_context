package ru.tensor.sbis.design.custom_view_tools.text_layout.size.width

import android.os.Build
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import ru.tensor.sbis.design.custom_view_tools.TextLayout
import ru.tensor.sbis.design.custom_view_tools.TextLayout.TextLayoutPadding
import ru.tensor.sbis.design.custom_view_tools.utils.getTextWidth

/**
 * Тесты [TextLayout.width].
 *
 * @author vv.chekurda
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class TextLayoutWidthTest {

    private lateinit var textLayout: TextLayout

    @Before
    fun setUp() {
        textLayout = TextLayout { text = "Test text" }
    }

    @Test
    fun `Default TextLayout width is equals text width`() {
        val expectedTextWidth = textLayout.textPaint.getTextWidth(textLayout.text)

        assertEquals(expectedTextWidth, textLayout.width)
        assertNotEquals(0, expectedTextWidth)
    }

    @Test
    fun `When TextLayout configured width is equals null, then width is equals text width`() {
        val expectedTextWidth = textLayout.textPaint.getTextWidth(textLayout.text)

        textLayout.configure { layoutWidth = null }

        assertEquals(expectedTextWidth, textLayout.width)
        assertNotEquals(0, expectedTextWidth)
    }

    @Test
    fun `TextLayout width is equals configured width`() {
        val width = 200

        textLayout.configure { layoutWidth = width }

        assertEquals(width, textLayout.width)
    }

    @Test
    fun `When TextLayout contains padding, then width is equals configured width`() {
        val width = 200

        textLayout.configure {
            layoutWidth = width
            padding = TextLayoutPadding(start = 25, end = 45)
        }

        assertEquals(width, textLayout.width)
    }

    @Test
    fun `When TextLayout isVisible is equals false, then width is equals 0`() {
        val width = 200

        textLayout.configure {
            layoutWidth = width
            isVisible = false
        }

        assertEquals(0, textLayout.width)
    }

    @Test
    fun `When TextLayout contains padding and width is wrapped, then width is equals text width + horizontal padding`() {
        val paddingStart = 25
        val paddingEnd = 50
        val textWidth = textLayout.textPaint.getTextWidth(textLayout.text)
        val expectedWidth = textWidth + paddingStart + paddingEnd

        textLayout.configure {
            padding = TextLayoutPadding(start = paddingStart, end = paddingEnd)
        }

        assertEquals(expectedWidth, textLayout.width)
        assertNotEquals(0, textWidth)
    }
}
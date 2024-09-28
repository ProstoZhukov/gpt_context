package ru.tensor.sbis.design.custom_view_tools.text_layout

import android.os.Build
import android.text.TextUtils.TruncateAt
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import ru.tensor.sbis.design.custom_view_tools.TextLayout

/**
 * Тесты [TextLayout] на предмет работы параметров по затемнению сокращения текста.
 *
 * @see TextLayout.fadeEdgeSize
 * @see TextLayout.requiresFadingEdge
 *
 * @author vv.chekurda
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class TextLayoutFadingEdgeTest {

    private lateinit var textLayout: TextLayout

    @Before
    fun setUp() {
        textLayout = TextLayout {
            layoutWidth = 100
            isSingleLine = true
            ellipsize = null
        }

        textLayout.requiresFadingEdge = true
        textLayout.fadeEdgeSize = 50
    }

    @Test
    fun `Happy path of fading edge rule`() {
        assertTrue(textLayout.stateSnapshot.fadingEdgeRule)
    }

    @Test
    fun `When layoutWidth is null, then fadingEdgeRule is false`() {
        textLayout.configure { layoutWidth = null }

        assertFalse(textLayout.stateSnapshot.fadingEdgeRule)
    }

    @Test
    fun `When isSingleLine is false, then fadingEdgeRule is false`() {
        textLayout.configure { isSingleLine = false }

        assertFalse(textLayout.stateSnapshot.fadingEdgeRule)
    }

    @Test
    fun `When ellipsize is END, then fadingEdgeRule is false`() {
        textLayout.configure { ellipsize = TruncateAt.END }

        assertFalse(textLayout.stateSnapshot.fadingEdgeRule)
    }

    @Test
    fun `When requiresFadingEdge is false, then fadingEdgeRule is false`() {
        textLayout.requiresFadingEdge = false

        assertFalse(textLayout.stateSnapshot.fadingEdgeRule)
    }

    @Test
    fun `When fadeEdgeSize is 0, then fadingEdgeRule is false`() {
        textLayout.fadeEdgeSize = 0

        assertFalse(textLayout.stateSnapshot.fadingEdgeRule)
    }

    @Test
    fun `When fadeEdgeSize is negative, then fadeEdgeSize is 0 and fadingEdgeRule is false`() {
        textLayout.fadeEdgeSize = -50

        assertFalse(textLayout.stateSnapshot.fadingEdgeRule)
    }

    @Test
    fun `When fadingEdgeRule is true and text is bigger than layoutWidth, then isFadeEdgeVisible is true`() {
        val testText = "Test string"
        val textWidth = textLayout.getDesiredWidth(testText)
        val customWidth = textWidth / 2

        textLayout.buildLayout {
            text = testText
            layoutWidth = customWidth
        }

        assertTrue(textWidth > customWidth)
        assertTrue(textLayout.stateSnapshot.fadingEdgeRule)
        assertTrue(textLayout.stateSnapshot.isFadeEdgeVisible)
    }

    @Test
    fun `When isFadeEdgeVisible, then inner layout width is equals sum of layoutWidth and fadeEdgeSize`() {
        val testText = "Test string"
        val textWidth = textLayout.getDesiredWidth(testText)
        val customWidth = textWidth / 2
        val expectedLayoutWidth = customWidth + textLayout.fadeEdgeSize

        textLayout.buildLayout {
            text = testText
            layoutWidth = customWidth
        }

        assertTrue(textLayout.stateSnapshot.isFadeEdgeVisible)
        assertTrue(textLayout.fadeEdgeSize > 0)
        assertEquals(expectedLayoutWidth, textLayout.requireLayout().width)
    }

    @Test
    fun `When isFadeEdgeVisible, then TextLayout width is equals layoutWidth`() {
        val testText = "Test string"
        val textWidth = textLayout.getDesiredWidth(testText)
        val customWidth = textWidth / 2

        textLayout.buildLayout {
            text = testText
            layoutWidth = customWidth
        }

        assertTrue(textLayout.stateSnapshot.isFadeEdgeVisible)
        assertTrue(textLayout.fadeEdgeSize > 0)
        assertEquals(customWidth, textLayout.width)
    }
}
package ru.tensor.sbis.design.custom_view_tools.text_layout

import android.os.Build
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import ru.tensor.sbis.design.custom_view_tools.TextLayout
import ru.tensor.sbis.design.custom_view_tools.TextLayout.TextLayoutParams

/**
 * Тесты [TextLayout.isVisible].
 *
 * @author vv.chekurda
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class TextLayoutIsVisibleTest {

    private lateinit var textLayout: TextLayout

    @Before
    fun setUp() {
        textLayout = TextLayout { text = "Test text" }
    }

    @Test
    fun `Default isVisible is equals true`() {
        assertTrue(TextLayout().isVisible)
    }

    @Test
    fun `When TextLayout isVisible is equals false, then width is equals 0`() {
        val widthBeforeChanges = textLayout.width

        textLayout.configure { isVisible = false }

        assertEquals(0, textLayout.width)
        assertNotEquals(0, widthBeforeChanges)
    }

    @Test
    fun `When TextLayout isVisible is equals false, then height is equals 0`() {
        val heightBeforeChanges = textLayout.height

        textLayout.configure { isVisible = false }

        assertEquals(0, textLayout.height)
        assertNotEquals(0, heightBeforeChanges)
    }

    @Test
    fun `Default isVisibleWhenBlank is equals true`() {
        assertTrue(TextLayoutParams().isVisibleWhenBlank)
    }

    @Test
    fun `When TextLayout isVisibleWhenBlank is equals true, then isVisible is equals true`() {
        textLayout.buildLayout { isVisibleWhenBlank = true }

        assertTrue(textLayout.isVisible)
    }

    @Test
    fun `When TextLayout isVisibleWhenBlank is equals false and text is not blank, then isVisible is equals true`() {
        textLayout.buildLayout { isVisibleWhenBlank = false }

        assertTrue(textLayout.isVisible)
        assertTrue(textLayout.text.isNotBlank())
    }

    @Test
    fun `When TextLayout isVisibleWhenBlank is equals false and text is blank, then isVisible is equals false`() {
        textLayout.buildLayout {
            text = "               "
            isVisibleWhenBlank = false
        }

        assertFalse(textLayout.isVisible)
        assertTrue(textLayout.text.isBlank())
    }
}
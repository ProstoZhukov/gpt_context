package ru.tensor.sbis.design.custom_view_tools.text_layout.size.height

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

/**
 * Тесты [TextLayout.height].
 *
 * @author vv.chekurda
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class TextLayoutHeightTest {

    private lateinit var textLayout: TextLayout

    @Before
    fun setUp() {
        textLayout = TextLayout { text = "Test text" }
    }

    @Test
    fun `TextLayout height is equals Layout height`() {
        textLayout.buildLayout()
        val staticHeight = textLayout.stateSnapshot.layout!!.height
        val layoutHeight = textLayout.height

        assertEquals(staticHeight, layoutHeight)
        assertNotEquals(0, layoutHeight)
    }

    @Test
    fun `When TextLayout isVisible is equals false, then height is equals 0`() {
        textLayout.configure { isVisible = false }
        val layoutHeight = textLayout.height

        assertEquals(0, layoutHeight)
        assertFalse(textLayout.isVisible)
    }

    @Test
    fun `When TextLayout width is equals 0, then height is equals text height`() {
        textLayout.configure { layoutWidth = 0 }
        val layoutHeight = textLayout.height
        val textHeight = textLayout.stateSnapshot.layout!!.height

        assertEquals(layoutHeight, textHeight)
        assertEquals(0, textLayout.width)
    }

    @Test
    fun `When TextLayout text is empty, then height is equals text height`() {
        textLayout.configure { text = "" }
        val layoutHeight = textLayout.height
        val textHeight = textLayout.stateSnapshot.layout!!.height

        assertEquals(layoutHeight, textHeight)
        assertTrue(textLayout.text.isEmpty())
    }

    @Test
    fun `When TextLayout is exists padding, then height is equals Layout height + vertical padding`() {
        textLayout.buildLayout()
        val paddingTop = 25
        val paddingBottom = 50
        val staticHeight = textLayout.stateSnapshot.layout!!.height
        val expectedHeight = staticHeight + paddingTop + paddingBottom

        textLayout.updatePadding(top = paddingTop, bottom = paddingBottom)

        assertEquals(expectedHeight, textLayout.height)
        assertNotEquals(0, expectedHeight)
    }

    @Test
    fun `When TextLayout isVisible is equals false and padding is exists, then height is equals 0`() {
        textLayout.buildLayout { isVisible = false }
        textLayout.updatePadding(top = 25, bottom = 50)

        assertEquals(0, textLayout.height)
        assertNotEquals(0, textLayout.paddingTop)
        assertNotEquals(0, textLayout.paddingBottom)
        assertFalse(textLayout.isVisible)
    }

    @Test
    fun `When TextLayout text is empty and padding is exists, then height is equals text height + vertical paddings`() {
        textLayout.buildLayout { text = "" }
        textLayout.updatePadding(top = 25, bottom = 50)
        val textHeight = textLayout.stateSnapshot.layout!!.height
        val expectedHeight = textHeight + textLayout.paddingTop + textLayout.paddingBottom

        assertEquals(expectedHeight, textLayout.height)
        assertTrue(textLayout.text.isEmpty())
        assertNotEquals(0, textLayout.paddingTop)
        assertNotEquals(0, textLayout.paddingBottom)
    }
}
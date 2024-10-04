package ru.tensor.sbis.design.custom_view_tools.utils.layout_configurator

import android.os.Build
import android.text.Layout
import android.text.TextUtils.TruncateAt
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import ru.tensor.sbis.design.custom_view_tools.utils.SimpleTextPaint
import ru.tensor.sbis.design.custom_view_tools.utils.layout.LayoutConfigurator
import ru.tensor.sbis.design.custom_view_tools.utils.layout.LayoutConfigurator.createLayout

/**
 * Тесты [LayoutConfigurator] на предмет работы параметров по затемнению сокращения текста.
 *
 * @see LayoutConfigurator.Params.fadingEdgeSize
 *
 * @author vv.chekurda
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class LayoutConfiguratorFadingEdgeTest {

    private val testText = "Test string"
    private val testPaint = SimpleTextPaint()

    @Test
    fun `Happy path of fading edge rule`() {
        val textWidth = Layout.getDesiredWidth(testText, testPaint).toInt()
        val width = textWidth / 2
        val fadingEdgeSize = 50
        val expectedLayoutWidth = width + fadingEdgeSize

        val layout = createLayout(testText, testPaint) {
            this.width = width
            isSingleLine = true
            ellipsize = null
            this.fadingEdgeSize = fadingEdgeSize
        }

        assertEquals(expectedLayoutWidth, layout.width)
        assertTrue(width < textWidth)
    }

    @Test
    fun `When isSingleLine is false, then fading edge size is ignored`() {
        val textWidth = Layout.getDesiredWidth(testText, testPaint).toInt()
        val width = textWidth / 2
        val fadingEdgeSize = 50

        val layout = createLayout(testText, testPaint) {
            this.width = width
            isSingleLine = false
            ellipsize = null
            this.fadingEdgeSize = fadingEdgeSize
        }

        assertEquals(width, layout.width)
        assertTrue(width < textWidth)
    }

    @Test
    fun `When ellipsize is END, then fading edge size is ignored`() {
        val textWidth = Layout.getDesiredWidth(testText, testPaint).toInt()
        val width = textWidth / 2
        val fadingEdgeSize = 50

        val layout = createLayout(testText, testPaint) {
            this.width = width
            isSingleLine = true
            this.fadingEdgeSize = fadingEdgeSize

            ellipsize = TruncateAt.END
        }

        assertEquals(width, layout.width)
        assertTrue(width < textWidth)
    }

    @Test
    fun `When width is null, then fading edge size is ignored`() {
        val textWidth = Layout.getDesiredWidth(testText, testPaint).toInt()
        val fadingEdgeSize = 50

        val layout = createLayout(testText, testPaint) {
            isSingleLine = true
            ellipsize = null
            this.fadingEdgeSize = fadingEdgeSize

            this.width = null
        }

        assertEquals(textWidth, layout.width)
        assertTrue(textWidth > 0)
    }
}
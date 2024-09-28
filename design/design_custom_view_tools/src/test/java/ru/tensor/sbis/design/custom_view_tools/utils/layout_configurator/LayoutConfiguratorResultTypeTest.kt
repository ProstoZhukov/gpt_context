package ru.tensor.sbis.design.custom_view_tools.utils.layout_configurator

import android.os.Build
import android.text.BoringLayout
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.text.TextUtils.TruncateAt
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import ru.tensor.sbis.design.custom_view_tools.utils.layout.LayoutConfigurator.createLayout

/**
 * Тест методов [createLayout] на предмет возвращаемого типа [BoringLayout] или [StaticLayout],
 * в зависимости от параметров.
 *
 * @author vv.chekurda
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class LayoutConfiguratorResultTypeTest {

    private val textPaint = TextPaint()
    private val testText = "Test text test text"
    private val testBoring = BoringLayout.isBoring(testText, textPaint)

    @Test
    fun `When params is default, then create StaticLayout`() {
        val layout = createLayout(text = testText, paint = textPaint)

        assertTrue(layout is StaticLayout)
    }

    @Test
    fun `When set BoringMetrics, then create BoringLayout`() {
        val layout = createLayout(text = testText, paint = textPaint) {
            boring = testBoring
        }

        assertTrue(layout is BoringLayout)
    }

    @Test
    fun `When set BoringMetrics and BoringLayout, then return updated BoringLayout`() {
        val lastBoringLayout = createLayout(text = testText, paint = textPaint) {
            boring = testBoring
        } as BoringLayout

        val layout = createLayout(text = testText, paint = textPaint) {
            boring = testBoring
            boringLayout = lastBoringLayout
        }

        assertTrue(layout is BoringLayout)
        assertSame(lastBoringLayout, layout)
    }

    @Test
    fun `When set only BoringLayout, then create StaticLayout`() {
        val lastBoringLayout = createLayout(text = testText, paint = textPaint) {
            boring = testBoring
        } as BoringLayout

        val layout = createLayout(text = testText, paint = textPaint) {
            boringLayout = lastBoringLayout
        }

        assertTrue(layout is StaticLayout)
    }

    @Test
    fun `When width is smaller than text width, maxLines is 1, ellipsize is null, then create StaticLayout`() {
        val expectedTextWidth = Layout.getDesiredWidth(testText, textPaint).toInt()
        val smallerWidth = expectedTextWidth - 1

        val layout = createLayout(text = testText, paint = textPaint) {
            boring = testBoring
            isSingleLine = false
            maxLines = 1
            ellipsize = null
            width = smallerWidth
        }

        assertTrue(layout is StaticLayout)
    }

    @Test
    fun `When width is smaller than text width, maxLines is 1, ellipsize is END, then create BoringLayout`() {
        val expectedTextWidth = Layout.getDesiredWidth(testText, textPaint).toInt()
        val smallerWidth = expectedTextWidth - 1

        val layout = createLayout(text = testText, paint = textPaint) {
            boring = testBoring
            isSingleLine = false
            maxLines = 1
            ellipsize = TruncateAt.END
            width = smallerWidth
        }

        assertTrue(layout is BoringLayout)
    }

    @Test
    fun `When width is smaller than text width, singleLine is true, ellipsize is null, then create BoringLayout`() {
        val expectedTextWidth = Layout.getDesiredWidth(testText, textPaint).toInt()
        val smallerWidth = expectedTextWidth - 1

        val layout = createLayout(text = testText, paint = textPaint) {
            boring = testBoring
            isSingleLine = true
            ellipsize = null
            width = smallerWidth
        }

        assertTrue(layout is BoringLayout)
    }

    @Test
    fun `When width is smaller than text width, singleLine is true, ellipsize is END, then create BoringLayout`() {
        val expectedTextWidth = Layout.getDesiredWidth(testText, textPaint).toInt()
        val smallerWidth = expectedTextWidth - 1

        val layout = createLayout(text = testText, paint = textPaint) {
            boring = testBoring
            isSingleLine = true
            ellipsize = TruncateAt.END
            width = smallerWidth
        }

        assertTrue(layout is BoringLayout)
    }

    @Test
    fun `When singleLine is true and ellipsize is null, then create BoringLayout`() {
        val layout = createLayout(text = testText, paint = textPaint) {
            boring = testBoring
            ellipsize = null
            isSingleLine = true
        }

        assertTrue(layout is BoringLayout)
    }

    @Test
    fun `When singleLine is true and ellipsize is END, then create BoringLayout`() {
        val layout = createLayout(text = testText, paint = textPaint) {
            boring = testBoring
            ellipsize = TruncateAt.END
            isSingleLine = true
        }

        assertTrue(layout is BoringLayout)
    }

    @Test
    fun `When singleLine is true and maxLines is 1, then create BoringLayout`() {
        val layout = createLayout(text = testText, paint = textPaint) {
            boring = testBoring
            maxLines = 1
            isSingleLine = true
        }

        assertTrue(layout is BoringLayout)
    }

    @Test
    fun `When singleLine is false and maxLines is 2, then create BoringLayout`() {
        val layout = createLayout(text = testText, paint = textPaint) {
            boring = testBoring
            maxLines = 2
            isSingleLine = true
        }

        assertTrue(layout is BoringLayout)
    }

    @Test
    fun `When text has multi lines, then create StaticLayout`() {
        val testText = "1\n2\n3\n4\n5\n6\n7"
        val isBoring = BoringLayout.isBoring(testText, textPaint)

        val layout = createLayout(testText, textPaint) {
            boring = isBoring
            isSingleLine = true
        }

        assertTrue(layout is StaticLayout)
    }
}
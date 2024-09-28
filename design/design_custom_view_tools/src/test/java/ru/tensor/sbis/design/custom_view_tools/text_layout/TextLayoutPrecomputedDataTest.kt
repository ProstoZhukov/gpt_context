package ru.tensor.sbis.design.custom_view_tools.text_layout

import android.os.Build
import android.text.Layout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import ru.tensor.sbis.design.custom_view_tools.TextLayout

/**
 * Тесты методы [TextLayout.getPrecomputedWidth] на предвычисления параметров текста
 * для построения внутренней разметки [TextLayout],
 * а также на количество вызовов обновления этих данных в различных сценариях.
 *
 * @author vv.chekurda
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class TextLayoutPrecomputedDataTest {

    private val testText = "Test string text"

    @Test
    fun `When precompute and available width is null, then return text width + horizontal paddings`() {
        val layout = TextLayout { text = testText }
        layout.updatePadding(start = 10, end = 20)
        val textWidth = Layout.getDesiredWidth(layout.text, layout.textPaint).toInt()
        val expectedWidth = textWidth + layout.paddingStart + layout.paddingEnd

        val precomputedWidth = layout.getPrecomputedWidth(availableWidth = null)

        assertEquals(expectedWidth, precomputedWidth)
    }

    @Test
    fun `When precompute and available width is bigger than text width, then return text width`() {
        val layout = TextLayout { text = testText }
        val textWidth = Layout.getDesiredWidth(layout.text, layout.textPaint).toInt()
        val availableWidth = textWidth + 100

        val precomputedWidth = layout.getPrecomputedWidth(availableWidth = availableWidth)

        assertEquals(textWidth, precomputedWidth)
        assertTrue(availableWidth > textWidth)
    }

    @Test
    fun `When precompute and available width is smaller than text width, then return available width`() {
        val layout = TextLayout { text = testText }
        val textWidth = Layout.getDesiredWidth(layout.text, layout.textPaint).toInt()
        val availableWidth = textWidth / 2

        val precomputedWidth = layout.getPrecomputedWidth(availableWidth = availableWidth)

        assertEquals(availableWidth, precomputedWidth)
        assertTrue(availableWidth < textWidth)
    }

    @Test
    fun `When precompute and text is empty, then return 0`() {
        val layout = TextLayout { text = "" }

        val precomputedWidth = layout.getPrecomputedWidth(availableWidth = null)

        assertEquals(0, precomputedWidth)
    }

    @Test
    fun `When precompute and maxWidth is smaller than desiredWidth and availableWidth, then return maxTextWidth`() {
        val layout = TextLayout { text = testText }
        val desiredWidth = layout.getDesiredWidth()
        val availableWidth = desiredWidth / 2
        val customMaxWidth = availableWidth / 2

        layout.configure { maxWidth = customMaxWidth }
        val precomputedWidth = layout.getPrecomputedWidth(availableWidth = availableWidth)

        assertEquals(customMaxWidth, precomputedWidth)
        assertTrue(customMaxWidth > 0)
        assertTrue(customMaxWidth < availableWidth)
        assertTrue(customMaxWidth < desiredWidth)
    }

    @Test
    fun `When precompute and minWidth is bigger than desiredWidth and availableWidth, then return minTextWidth`() {
        val layout = TextLayout { text = testText }
        val desiredWidth = layout.getDesiredWidth()
        val availableWidth = desiredWidth / 2
        val customMinWidth = desiredWidth * 2

        layout.configure { minWidth = customMinWidth }
        val precomputedWidth = layout.getPrecomputedWidth(availableWidth = availableWidth)

        assertEquals(customMinWidth, precomputedWidth)
        assertTrue(customMinWidth > 0)
        assertTrue(customMinWidth > availableWidth)
        assertTrue(customMinWidth > desiredWidth)
    }

    @Test
    fun `When precompute, layout not changed, and available width is equals, then return last precomputed width`() {
        val layout = TextLayout { text = testText }
        val textWidth = Layout.getDesiredWidth(layout.text, layout.textPaint).toInt()
        val availableWidth = textWidth / 2
        val expectedRefreshPrecomputedCount = 1

        val precomputedWidth1 = layout.getPrecomputedWidth(availableWidth = availableWidth)
        val precomputedWidth2 = layout.getPrecomputedWidth(availableWidth = availableWidth)

        assertEquals(availableWidth, precomputedWidth1)
        assertEquals(availableWidth, precomputedWidth2)
        assertEquals(expectedRefreshPrecomputedCount, layout.stateSnapshot.refreshPrecomputedCount)
    }

    @Test
    fun `When precompute, layout is changed, and available width is equals, then return new precomputed width`() {
        val layout = TextLayout { text = testText }
        val textWidth = Layout.getDesiredWidth(layout.text, layout.textPaint).toInt()
        val availableWidth = textWidth / 2
        val expectedRefreshPrecomputedCount = 2

        val precomputedWidth1 = layout.getPrecomputedWidth(availableWidth = availableWidth)
        val isChanged = layout.configure { maxHeight = 100 }
        val precomputedWidth2 = layout.getPrecomputedWidth(availableWidth = availableWidth)

        assertTrue(isChanged)
        assertEquals(availableWidth, precomputedWidth1)
        assertEquals(availableWidth, precomputedWidth2)
        assertEquals(expectedRefreshPrecomputedCount, layout.stateSnapshot.refreshPrecomputedCount)
    }

    @Test
    fun `When precompute, layout not changed, and available width is different, then return new precomputed width`() {
        val layout = TextLayout { text = testText }
        val textWidth = Layout.getDesiredWidth(layout.text, layout.textPaint).toInt()
        val availableWidth1 = textWidth / 2
        val availableWidth2 = textWidth / 3
        val expectedRefreshPrecomputedCount = 2

        val precomputedWidth1 = layout.getPrecomputedWidth(availableWidth = availableWidth1)
        val precomputedWidth2 = layout.getPrecomputedWidth(availableWidth = availableWidth2)

        assertEquals(availableWidth1, precomputedWidth1)
        assertEquals(availableWidth2, precomputedWidth2)
        assertEquals(expectedRefreshPrecomputedCount, layout.stateSnapshot.refreshPrecomputedCount)
    }

    @Test
    fun `When precompute and buildLayout with width, then precomputed data is actual`() {
        val layout = TextLayout { text = testText }
        val availableWidth = 200
        val expectedRefreshPrecomputedCount = 1

        val precomputedWidthBefore = layout.getPrecomputedWidth(availableWidth = availableWidth)

        layout.buildLayout(precomputedWidthBefore)

        val precomputedWidthAfter = layout.getPrecomputedWidth(availableWidth = availableWidth)

        assertEquals(precomputedWidthBefore, precomputedWidthAfter)
        assertEquals(expectedRefreshPrecomputedCount, layout.stateSnapshot.refreshPrecomputedCount)
    }

    @Test
    fun `When precompute and buildLayout with config, then precomputed data is refreshed`() {
        val layout = TextLayout { text = testText }
        val availableWidth = 200
        val expectedRefreshPrecomputedCount = 2

        val precomputedWidth = layout.getPrecomputedWidth(availableWidth = availableWidth)

        layout.buildLayout { layoutWidth = precomputedWidth }

        assertEquals(expectedRefreshPrecomputedCount, layout.stateSnapshot.refreshPrecomputedCount)
    }

    @Test
    fun `When precompute and buildLayout without config, then refreshPrecomputedCount is equals 2`() {
        val layout = TextLayout { text = testText }
        val availableWidth = 200
        val expectedRefreshPrecomputedCount = 2

        layout.getPrecomputedWidth(availableWidth = availableWidth)
        layout.buildLayout()

        assertEquals(expectedRefreshPrecomputedCount, layout.stateSnapshot.refreshPrecomputedCount)
    }

    @Test
    fun `When precompute without available width and buildLayout without config, then refreshPrecomputedCount is equals 1`() {
        val layout = TextLayout { text = testText }
        val expectedRefreshPrecomputedCount = 1

        layout.getPrecomputedWidth(availableWidth = null)
        layout.buildLayout()

        assertEquals(expectedRefreshPrecomputedCount, layout.stateSnapshot.refreshPrecomputedCount)
    }

    @Test
    fun `When precompute without available width, change layoutWidth, and build, then refreshPrecomputedCount is equals 2`() {
        val layout = TextLayout { text = testText }
        val expectedRefreshPrecomputedCount = 2

        layout.getPrecomputedWidth(availableWidth = null)
        layout.configure { layoutWidth = 200 }
        layout.buildLayout()

        assertEquals(expectedRefreshPrecomputedCount, layout.stateSnapshot.refreshPrecomputedCount)
    }

    @Test
    fun `When precompute without available width, result was set in config, and build, then refreshPrecomputedCount is equals 2`() {
        val layout = TextLayout { text = testText }
        val expectedRefreshPrecomputedCount = 2

        val precomputedWidth = layout.getPrecomputedWidth(availableWidth = null)
        layout.configure { layoutWidth = precomputedWidth }
        layout.buildLayout()

        assertEquals(expectedRefreshPrecomputedCount, layout.stateSnapshot.refreshPrecomputedCount)
    }

    @Test
    fun `When precompute without available width and build with resul, then refreshPrecomputedCount is equals 1`() {
        val layout = TextLayout { text = testText }
        val expectedRefreshPrecomputedCount = 1

        val precomputedWidth = layout.getPrecomputedWidth(availableWidth = null)
        layout.buildLayout(precomputedWidth)

        assertEquals(expectedRefreshPrecomputedCount, layout.stateSnapshot.refreshPrecomputedCount)
    }

    @Test
    fun `When TextLayout is configured without build, then precomputed data doesn't refreshed`() {
        val layout = TextLayout { text = testText }
        val expectedRefreshPrecomputedCount = 0

        layout.configure { ellipsize = null }

        assertEquals(expectedRefreshPrecomputedCount, layout.stateSnapshot.refreshPrecomputedCount)
        assertNull(layout.stateSnapshot.layout)
    }

    @Test
    fun `When TextLayout is configured with build, then refreshPrecomputedCount is equals 1`() {
        val layout = TextLayout { text = testText }
        val expectedRefreshPrecomputedCount = 1

        layout.configure { ellipsize = null }
        layout.buildLayout()

        assertEquals(expectedRefreshPrecomputedCount, layout.stateSnapshot.refreshPrecomputedCount)
        assertNotNull(layout.stateSnapshot.layout)
    }

    @Test
    fun `When buildLayout with equals width, then refreshPrecomputedCount is equals 1`() {
        val layout = TextLayout { text = testText }
        val customWidth = 200
        val expectedRefreshPrecomputedCount = 1

        layout.buildLayout(customWidth)
        layout.buildLayout(customWidth)

        assertEquals(expectedRefreshPrecomputedCount, layout.stateSnapshot.refreshPrecomputedCount)
        assertEquals(customWidth, layout.width)
    }

    @Test
    fun `When buildLayout with bigger width, then refreshPrecomputedCount is equals 2`() {
        val layout = TextLayout { text = testText }
        val customWidth1 = 200
        val customWidth2 = customWidth1 + 100
        val expectedRefreshPrecomputedCount = 2

        layout.buildLayout(customWidth1)
        layout.buildLayout(customWidth2)

        assertEquals(expectedRefreshPrecomputedCount, layout.stateSnapshot.refreshPrecomputedCount)
        assertNotEquals(customWidth1, customWidth2)
        assertEquals(customWidth2, layout.width)
    }

    @Test
    fun `When buildLayout with smaller width, then refreshPrecomputedCount is equals 2`() {
        val layout = TextLayout { text = testText }
        val customWidth1 = 200
        val customWidth2 = customWidth1 - 100
        val expectedRefreshPrecomputedCount = 2

        layout.buildLayout(customWidth1)
        layout.buildLayout(customWidth2)

        assertEquals(expectedRefreshPrecomputedCount, layout.stateSnapshot.refreshPrecomputedCount)
        assertNotEquals(customWidth1, customWidth2)
        assertEquals(customWidth2, layout.width)
    }

    @Test
    fun `When precompute first width and build with second width, then refreshPrecomputedCount is equals 2`() {
        val layout = TextLayout { text = testText + testText + testText }
        val availableWidth = Layout.getDesiredWidth(layout.text, layout.textPaint).toInt()
        val expectedRefreshPrecomputedCount = 2

        val precomputedWidth = layout.getPrecomputedWidth(availableWidth = availableWidth)
        val customWidth = precomputedWidth / 2

        layout.buildLayout(customWidth)

        assertEquals(expectedRefreshPrecomputedCount, layout.stateSnapshot.refreshPrecomputedCount)
        assertEquals(customWidth, layout.width)
    }
}
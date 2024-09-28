package ru.tensor.sbis.design.custom_view_tools.text_layout

import android.os.Build
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import ru.tensor.sbis.design.custom_view_tools.TextLayout

/**
 * Тесты на закешированное состояние вычислений внутренних парамтеров [TextLayout].
 * Данные проверки необходимы для защиты производительности TextLayout от лишних повторных вычислений.
 *
 * @see TextLayout.CachedState
 *
 * @author vv.chekurda
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class TextLayoutCachedStateTest {

    @Test
    fun `When use empty constructor, then resetCount is 0`() {
        val layout = TextLayout()
        val expectedResetCount = 0

        assertEquals(expectedResetCount, layout.cachedStateSnapshot.resetCount)
    }

    @Test
    fun `When build without params, then resetCount is 0`() {
        val layout = TextLayout()
        val expectedResetCount = 0

        layout.buildLayout()

        assertEquals(expectedResetCount, layout.cachedStateSnapshot.resetCount)
    }

    @Test
    fun `When configure on create, then resetCount is 0`() {
        val layout = TextLayout {
            text = "Test string"
        }
        val expectedResetCount = 0

        assertEquals(expectedResetCount, layout.cachedStateSnapshot.resetCount)
    }

    @Test
    fun `When configure, then resetCount is 1`() {
        val layout = TextLayout()
        val expectedResetCount = 1

        layout.configure { text = "123" }

        assertEquals(expectedResetCount, layout.cachedStateSnapshot.resetCount)
    }

    @Test
    fun `When configure without changes firstly, then resetCount is 1`() {
        val testText = "123"
        val layout = TextLayout { text = testText }
        val expectedResetCount = 1

        val isChanged = layout.configure { text = testText }

        assertEquals(expectedResetCount, layout.cachedStateSnapshot.resetCount)
        assertTrue(isChanged)
    }

    @Test
    fun `When configure without changes firstly with build before, then resetCount is 0`() {
        val testText = "123"
        val layout = TextLayout { text = testText }
        val expectedResetCount = 0

        layout.buildLayout()
        val isChanged = layout.configure { text = testText }

        assertEquals(expectedResetCount, layout.cachedStateSnapshot.resetCount)
        assertFalse(isChanged)
    }

    @Test
    fun `When configure with change, then resetCount is 1`() {
        val layout = TextLayout { text = "123" }
        layout.buildLayout()
        val expectedResetCount = 1

        val isChanged = layout.configure { text = "1234" }

        assertEquals(expectedResetCount, layout.cachedStateSnapshot.resetCount)
        assertTrue(isChanged)
    }

    @Test
    fun `When configure 3 times, then resetCount is 3`() {
        val layout = TextLayout()
        val expectedResetCount = 3

        val isChanged1 = layout.configure { text = "1" }
        val isChanged2 = layout.configure { text = "2" }
        val isChanged3 = layout.configure { text = "3" }

        assertEquals(expectedResetCount, layout.cachedStateSnapshot.resetCount)
        assertTrue(isChanged1)
        assertTrue(isChanged2)
        assertTrue(isChanged3)
    }

    @Test
    fun `When build with change, then resetCount is 1`() {
        val layout = TextLayout()
        val expectedResetCount = 1

        val isChanged = layout.buildLayout { text = "1" }

        assertEquals(expectedResetCount, layout.cachedStateSnapshot.resetCount)
        assertTrue(isChanged)
    }

    @Test
    fun `When configure after build with change, then resetCount is 2`() {
        val layout = TextLayout()
        val expectedResetCount = 2

        val isChanged1 = layout.buildLayout { text = "1" }
        val isChanged2 = layout.configure { text = "2" }

        assertEquals(expectedResetCount, layout.cachedStateSnapshot.resetCount)
        assertTrue(isChanged1)
        assertTrue(isChanged2)
    }

    @Test
    fun `When call width 3 times after configure, then widthCount is 1`() {
        val layout = TextLayout()
        val expectedWidthCount = 1

        layout.configure { text = "123" }
        layout.width
        layout.width
        layout.width

        assertEquals(expectedWidthCount, layout.cachedStateSnapshot.widthCount)
    }

    @Test
    fun `When call height 3 times after configure, then heightCount is 1`() {
        val layout = TextLayout()
        val expectedHeightCount = 1

        layout.configure { text = "123" }
        layout.height
        layout.height
        layout.height

        assertEquals(expectedHeightCount, layout.cachedStateSnapshot.heightCount)
    }

    @Test
    fun `When call width after configure without changed, then widthCount is 1`() {
        val testText = "123"
        val layout = TextLayout { text = testText }
        val expectedWidthCount = 1

        layout.buildLayout()
        val isChanged = layout.configure { text = testText }
        layout.width

        assertEquals(expectedWidthCount, layout.cachedStateSnapshot.widthCount)
        assertFalse(isChanged)
    }

    @Test
    fun `When call width before and after configure with changes, then widthCount is 2`() {
        val layout = TextLayout { text = "123" }
        val expectedWidthCount = 2

        layout.width
        val isChanged = layout.configure { text = "1" }
        layout.width

        assertEquals(expectedWidthCount, layout.cachedStateSnapshot.widthCount)
        assertTrue(isChanged)
    }

    @Test
    fun `When buildLayout with text, then sizes and height dimens are unused`() {
        val layout = TextLayout()
        val usedValue = 1
        val unusedValue = 0

        layout.buildLayout {
            text = "123"
        }

        assertEquals(usedValue, layout.cachedStateSnapshot.configuredTextCount)
        assertEquals(usedValue, layout.cachedStateSnapshot.textWidthCount)
        assertEquals(usedValue, layout.cachedStateSnapshot.maxTextWidthCount)
        assertEquals(usedValue, layout.cachedStateSnapshot.minTextWidthCount)
        assertEquals(usedValue, layout.cachedStateSnapshot.layoutMaxHeightCount)
        assertEquals(usedValue, layout.cachedStateSnapshot.isVisibleCount)
        assertEquals(usedValue, layout.cachedStateSnapshot.horizontalPaddingCount)

        assertEquals(unusedValue, layout.cachedStateSnapshot.widthCount)
        assertEquals(unusedValue, layout.cachedStateSnapshot.heightCount)
        assertEquals(unusedValue, layout.cachedStateSnapshot.minHeightByLinesCount)
        assertEquals(unusedValue, layout.cachedStateSnapshot.verticalPaddingCount)
    }

    @Test
    fun `When buildLayout with minWidth and maxWidth, then sizes and height dimens are unused`() {
        val layout = TextLayout {
            text = "123"
        }
        val usedValue = 1
        val unusedValue = 0

        layout.buildLayout {
            minWidth = 100
            maxWidth = 200
        }

        assertEquals(usedValue, layout.cachedStateSnapshot.configuredTextCount)
        assertEquals(usedValue, layout.cachedStateSnapshot.textWidthCount)
        assertEquals(usedValue, layout.cachedStateSnapshot.maxTextWidthCount)
        assertEquals(usedValue, layout.cachedStateSnapshot.minTextWidthCount)
        assertEquals(usedValue, layout.cachedStateSnapshot.layoutMaxHeightCount)
        assertEquals(usedValue, layout.cachedStateSnapshot.isVisibleCount)
        assertEquals(usedValue, layout.cachedStateSnapshot.horizontalPaddingCount)

        assertEquals(unusedValue, layout.cachedStateSnapshot.widthCount)
        assertEquals(unusedValue, layout.cachedStateSnapshot.heightCount)
        assertEquals(unusedValue, layout.cachedStateSnapshot.minHeightByLinesCount)
        assertEquals(unusedValue, layout.cachedStateSnapshot.verticalPaddingCount)
    }

    @Test
    fun `When buildLayout with minHeight and maxHeight, then only sizes are unused`() {
        val layout = TextLayout {
            text = "123"
        }
        val usedValue = 1
        val unusedValue = 0

        layout.buildLayout {
            minHeight = 100
            maxHeight = 200
        }

        assertEquals(usedValue, layout.cachedStateSnapshot.configuredTextCount)
        assertEquals(usedValue, layout.cachedStateSnapshot.textWidthCount)
        assertEquals(usedValue, layout.cachedStateSnapshot.maxTextWidthCount)
        assertEquals(usedValue, layout.cachedStateSnapshot.minTextWidthCount)
        assertEquals(usedValue, layout.cachedStateSnapshot.layoutMaxHeightCount)
        assertEquals(usedValue, layout.cachedStateSnapshot.isVisibleCount)
        assertEquals(usedValue, layout.cachedStateSnapshot.horizontalPaddingCount)
        assertEquals(usedValue, layout.cachedStateSnapshot.verticalPaddingCount)

        assertEquals(unusedValue, layout.cachedStateSnapshot.minHeightByLinesCount)
        assertEquals(unusedValue, layout.cachedStateSnapshot.widthCount)
        assertEquals(unusedValue, layout.cachedStateSnapshot.heightCount)
    }

    @Test
    fun `When buildLayout with text and call height, then height calculations are 1`() {
        val layout = TextLayout {
            text = "123"
        }
        val usedValue = 1
        val unusedValue = 0

        layout.buildLayout()
        layout.height

        assertEquals(usedValue, layout.cachedStateSnapshot.minHeightByLinesCount)
        assertEquals(usedValue, layout.cachedStateSnapshot.heightCount)

        assertEquals(unusedValue, layout.cachedStateSnapshot.widthCount)
    }

    @Test
    fun `When buildLayout with text and call width, then width calculations are 1, and height is 0`() {
        val layout = TextLayout {
            text = "123"
        }
        val usedValue = 1
        val unusedValue = 0

        layout.buildLayout()
        layout.width

        assertEquals(usedValue, layout.cachedStateSnapshot.widthCount)

        assertEquals(unusedValue, layout.cachedStateSnapshot.minHeightByLinesCount)
        assertEquals(unusedValue, layout.cachedStateSnapshot.heightCount)
    }

    @Test
    fun `When rebuild with diffs, then sizes and height dimens are unused, and other calculations is 2`() {
        val layout = TextLayout()
        val expectedUsed = 2
        val expectedUnused = 0

        layout.buildLayout { text = "123" }
        val isChanged = layout.buildLayout { text = "1234" }

        assertEquals(expectedUsed, layout.cachedStateSnapshot.configuredTextCount)
        assertEquals(expectedUsed, layout.cachedStateSnapshot.textWidthCount)
        assertEquals(expectedUsed, layout.cachedStateSnapshot.maxTextWidthCount)
        assertEquals(expectedUsed, layout.cachedStateSnapshot.minTextWidthCount)
        assertEquals(expectedUsed, layout.cachedStateSnapshot.layoutMaxHeightCount)
        assertEquals(expectedUsed, layout.cachedStateSnapshot.isVisibleCount)
        assertEquals(expectedUsed, layout.cachedStateSnapshot.horizontalPaddingCount)

        assertEquals(expectedUnused, layout.cachedStateSnapshot.widthCount)
        assertEquals(expectedUnused, layout.cachedStateSnapshot.heightCount)
        assertEquals(expectedUnused, layout.cachedStateSnapshot.minHeightByLinesCount)
        assertEquals(expectedUnused, layout.cachedStateSnapshot.verticalPaddingCount)

        assertTrue(isChanged)
    }

    @Test
    fun `When configure with diffs after build, then sizes and height dimens are unused, and other calculations is 1`() {
        val layout = TextLayout()
        val expectedUsed = 1
        val expectedUnused = 0

        layout.buildLayout { text = "123" }
        val isChanged = layout.configure { text = "1234" }

        assertEquals(expectedUsed, layout.cachedStateSnapshot.configuredTextCount)
        assertEquals(expectedUsed, layout.cachedStateSnapshot.textWidthCount)
        assertEquals(expectedUsed, layout.cachedStateSnapshot.maxTextWidthCount)
        assertEquals(expectedUsed, layout.cachedStateSnapshot.minTextWidthCount)
        assertEquals(expectedUsed, layout.cachedStateSnapshot.layoutMaxHeightCount)
        assertEquals(expectedUsed, layout.cachedStateSnapshot.isVisibleCount)
        assertEquals(expectedUsed, layout.cachedStateSnapshot.horizontalPaddingCount)

        assertEquals(expectedUnused, layout.cachedStateSnapshot.widthCount)
        assertEquals(expectedUnused, layout.cachedStateSnapshot.heightCount)
        assertEquals(expectedUnused, layout.cachedStateSnapshot.minHeightByLinesCount)
        assertEquals(expectedUnused, layout.cachedStateSnapshot.verticalPaddingCount)

        assertTrue(isChanged)
    }
}
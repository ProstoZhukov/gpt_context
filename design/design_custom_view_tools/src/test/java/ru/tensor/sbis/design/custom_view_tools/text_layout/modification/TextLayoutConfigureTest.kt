package ru.tensor.sbis.design.custom_view_tools.text_layout.modification

import android.graphics.Color
import android.os.Build
import android.text.TextUtils.TruncateAt
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import ru.tensor.sbis.design.custom_view_tools.TextLayout
import ru.tensor.sbis.design.custom_view_tools.TextLayout.TextLayoutPadding
import ru.tensor.sbis.design.custom_view_tools.utils.TextHighlights

/**
 * Тесты метода [TextLayout.configure] для модификации параметров [TextLayout.params].
 *
 * @author vv.chekurda
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class TextLayoutConfigureTest {

    private lateinit var textLayout: TextLayout

    @Before
    fun setUp() {
        textLayout = TextLayout { text = "Test text" }
            .apply { buildLayout() }
    }

    @Test
    fun `When call configure() and params isn't changed, then don't mark layout as changed and return false`() {
        val params = textLayout.stateSnapshot.params

        val isChanged = textLayout.configure {
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

        assertFalse(isChanged)
        assertFalse(textLayout.stateSnapshot.isLayoutChanged)
    }

    @Test
    fun `When call configure() and textColor is changed, then don't mark layout as changed and return false`() {
        val oldTextColor = textLayout.stateSnapshot.params.paint.color
        val newTextColor = -1

        val isChanged = textLayout.configure { paint.color = newTextColor }

        assertFalse(isChanged)
        assertFalse(textLayout.stateSnapshot.isLayoutChanged)
        assertNotEquals(oldTextColor, newTextColor)
    }

    @Test
    fun `When call configure() and textSize is changed, then mark layout as changed and return true`() {
        val oldTextSize = textLayout.stateSnapshot.params.paint.textSize
        val newTextSize = 200f

        assertTrue(textLayout.configure { paint.textSize = newTextSize })
        assertTrue(textLayout.stateSnapshot.isLayoutChanged)
        assertNotEquals(oldTextSize, newTextSize)
    }

    @Test
    fun `When call configure() and text is changed, then mark layout as changed and return true`() {
        val oldText = textLayout.stateSnapshot.params.text
        val newText = "TEST TEXT"

        assertTrue(textLayout.configure { text = newText })
        assertTrue(textLayout.stateSnapshot.isLayoutChanged)
        assertNotEquals(oldText, newText)
    }

    @Test
    fun `When call configure() and layoutWidth is changed, then mark layout as changed and return true`() {
        val oldLayoutWidth = textLayout.stateSnapshot.params.layoutWidth
        val newLayoutWidth = 200

        assertTrue(textLayout.configure { layoutWidth = newLayoutWidth })
        assertTrue(textLayout.stateSnapshot.isLayoutChanged)
        assertNotEquals(oldLayoutWidth, newLayoutWidth)
    }

    @Test
    fun `When call configure() and ellipsize is changed, then mark layout as changed and return true`() {
        val oldEllipsize = textLayout.stateSnapshot.params.ellipsize
        val newEllipsize = TruncateAt.START

        assertTrue(textLayout.configure { ellipsize = newEllipsize })
        assertTrue(textLayout.stateSnapshot.isLayoutChanged)
        assertNotEquals(oldEllipsize, newEllipsize)
    }

    @Test
    fun `When call configure() and maxLines is changed, then mark layout as changed and return true`() {
        val oldMaxLines = textLayout.stateSnapshot.params.maxLines
        val newMaxLines = 50

        assertTrue(textLayout.configure { maxLines = newMaxLines })
        assertTrue(textLayout.stateSnapshot.isLayoutChanged)
        assertNotEquals(oldMaxLines, newMaxLines)
    }

    @Test
    fun `When call configure() and isVisible is changed, then mark layout as changed and return true`() {
        val oldIsVisible = textLayout.stateSnapshot.params.isVisible
        val newIsVisible = false

        assertTrue(textLayout.configure { isVisible = newIsVisible })
        assertTrue(textLayout.stateSnapshot.isLayoutChanged)
        assertNotEquals(oldIsVisible, newIsVisible)
    }

    @Test
    fun `When call configure() and padding is changed, then mark layout as changed and return true`() {
        val oldPadding = textLayout.stateSnapshot.params.padding
        val newPadding = TextLayoutPadding(1, 2, 3, 4)

        assertTrue(textLayout.configure { padding = newPadding })
        assertTrue(textLayout.stateSnapshot.isLayoutChanged)
        assertNotEquals(oldPadding, newPadding)
    }

    @Test
    fun `When call configure() and highlights is changed, then mark layout as changed and return true`() {
        val oldHighlights = textLayout.stateSnapshot.params.highlights
        val newHighlights = TextHighlights(listOf(), Color.YELLOW)

        assertTrue(textLayout.configure { highlights = newHighlights })
        assertTrue(textLayout.stateSnapshot.isLayoutChanged)
        assertNotEquals(oldHighlights, newHighlights)
    }

    @Test
    fun `When call configure() and minWidth is changed, then mark layout as changed and return true`() {
        val oldMinHeight = textLayout.stateSnapshot.params.minWidth
        val newMinHeight = 50

        assertTrue(textLayout.configure { minHeight = newMinHeight })
        assertTrue(textLayout.stateSnapshot.isLayoutChanged)
        assertNotEquals(oldMinHeight, newMinHeight)
    }

    @Test
    fun `When call configure() and minHeight is changed, then mark layout as changed and return true`() {
        val oldMinHeight = textLayout.stateSnapshot.params.minHeight
        val newMinHeight = 50

        assertTrue(textLayout.configure { minHeight = newMinHeight })
        assertTrue(textLayout.stateSnapshot.isLayoutChanged)
        assertNotEquals(oldMinHeight, newMinHeight)
    }

    @Test
    fun `When call configure() and maxWidth is changed, then mark layout as changed and return true`() {
        val oldMaxWidth = textLayout.stateSnapshot.params.maxWidth
        val newMaxWidth = 50

        assertTrue(textLayout.configure { maxWidth = newMaxWidth })
        assertTrue(textLayout.stateSnapshot.isLayoutChanged)
        assertNotEquals(oldMaxWidth, newMaxWidth)
    }

    @Test
    fun `When call configure() and maxHeight is changed, then mark layout as changed and return true`() {
        val oldMaxHeight = textLayout.stateSnapshot.params.maxHeight
        val newMaxHeight = 50

        assertTrue(textLayout.configure { maxHeight = newMaxHeight })
        assertTrue(textLayout.stateSnapshot.isLayoutChanged)
        assertNotEquals(oldMaxHeight, newMaxHeight)
    }
}
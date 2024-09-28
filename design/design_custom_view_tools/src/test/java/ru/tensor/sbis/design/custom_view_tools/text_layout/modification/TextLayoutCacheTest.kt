package ru.tensor.sbis.design.custom_view_tools.text_layout.modification

import android.os.Build
import android.text.StaticLayout
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import ru.tensor.sbis.design.custom_view_tools.TextLayout
import ru.tensor.sbis.design.custom_view_tools.TextLayout.Companion.createTextLayoutByStyle
import ru.tensor.sbis.design.custom_view_tools.styles.StyleParams
import ru.tensor.sbis.design.custom_view_tools.styles.StyleParamsProvider
import ru.tensor.sbis.design.custom_view_tools.text_layout.assertLayoutNotCreated
import ru.tensor.sbis.design.custom_view_tools.text_layout.assertLayoutCreated

/**
 * Тесты [TextLayout] на предмет ленивого создания [StaticLayout] в поле [TextLayout.stateSnapshot].
 *
 * @author vv.chekurda
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class TextLayoutCacheTest {

    private lateinit var textLayout: TextLayout

    @Before
    fun setUp() {
        textLayout = TextLayout { text = "Test text" }
    }

    // region Don't create StaticLayout

    @Test
    fun `TextLayout don't create Layout after initialize by constructor`() {
        assertNull(textLayout.stateSnapshot.layout)
    }

    @Test
    fun `TextLayout don't create Layout after initialize by style`() {
        val styleKey = StyleParams.StyleKey(1)
        val textStyle = StyleParams.TextStyle(styleKey)
        val mockStylesProvider = mock<StyleParamsProvider<StyleParams.TextStyle>> {
            on { getStyleParams(any(), any<StyleParams.StyleKey>()) } doReturn textStyle
        }

        val layout = createTextLayoutByStyle(mock(), styleKey, mockStylesProvider)

        assertNull(layout.stateSnapshot.layout)
    }

    @Test
    fun `TextLayout don't create Layout after call configureLayout()`() {
        assertLayoutNotCreated(textLayout) { configure { text = "123" } }
    }

    @Test
    fun `TextLayout don't create Layout after call buildLayout(), when isVisible isEquals false`() {
        assertLayoutNotCreated(TextLayout { isVisible = false }) { buildLayout { text = "123" } }
    }

    @Test
    fun `TextLayout don't create Layout after second call buildLayout(), when params isn't changed`() {
        textLayout.buildLayout()
        val cacheAfterFirstBuild = textLayout.stateSnapshot.layout

        val isChanged = textLayout.buildLayout()
        val cachedLayout = textLayout.stateSnapshot.layout

        assertEquals(cacheAfterFirstBuild, cachedLayout)
        assertFalse(isChanged)
        assertNotNull(cachedLayout)
    }

    @Test
    fun `TextLayout don't create Layout after second call layout(), when params isn't changed`() {
        textLayout.layout(0, 0)
        val cacheAfterFirstBuild = textLayout.stateSnapshot.layout

        val isChanged = textLayout.buildLayout()
        val cachedLayout = textLayout.stateSnapshot.layout

        assertEquals(cacheAfterFirstBuild, cachedLayout)
        assertFalse(isChanged)
        assertNotNull(cachedLayout)
    }

    @Test
    fun `TextLayout don't create Layout after second request width`() {
        textLayout.width
        val cacheAfterFirstBuild = textLayout.stateSnapshot.layout

        textLayout.width
        val cachedLayout = textLayout.stateSnapshot.layout

        assertEquals(cacheAfterFirstBuild, cachedLayout)
        assertNotNull(cachedLayout)
    }

    @Test
    fun `TextLayout don't create Layout after second request height`() {
        textLayout.height
        val cacheAfterFirstBuild = textLayout.stateSnapshot.layout

        textLayout.height
        val cachedLayout = textLayout.stateSnapshot.layout

        assertEquals(cacheAfterFirstBuild, cachedLayout)
        assertNotNull(cachedLayout)
    }

    @Test
    fun `TextLayout don't create Layout after call updatePadding`() {
        assertLayoutNotCreated(textLayout) { updatePadding(1, 2, 3, 4) }
    }

    @Test
    fun `TextLayout don't create Layout after call layout, when isVisible is equals false`() {
        assertLayoutNotCreated(TextLayout { isVisible = false }) { layout(0, 0) }
    }

    @Test
    fun `TextLayout don't create Layout after call draw`() {
        assertLayoutNotCreated(textLayout) { draw(mock()) }
    }

    @Test
    fun `TextLayout don't create Layout after request text`() {
        assertLayoutNotCreated(textLayout) { text }
    }

    @Test
    fun `TextLayout don't create Layout after request textPaint`() {
        assertLayoutNotCreated(textLayout) { textPaint }
    }

    @Test
    fun `TextLayout don't create Layout after request isVisible`() {
        assertLayoutNotCreated(textLayout) { isVisible }
    }

    @Test
    fun `TextLayout don't create Layout after request maxLines`() {
        assertLayoutNotCreated(textLayout) { maxLines }
    }

    @Test
    fun `TextLayout don't create Layout after request left`() {
        assertLayoutNotCreated(textLayout) { left }
    }

    @Test
    fun `TextLayout don't create Layout after request top`() {
        assertLayoutNotCreated(textLayout) { top }
    }

    @Test
    fun `TextLayout don't create Layout after request right`() {
        assertLayoutNotCreated(textLayout) { right }
    }

    @Test
    fun `TextLayout don't create Layout after request bottom`() {
        assertLayoutNotCreated(textLayout) { bottom }
    }

    @Test
    fun `TextLayout don't create Layout after request paddingStart`() {
        assertLayoutNotCreated(textLayout) { paddingStart }
    }

    @Test
    fun `TextLayout don't create Layout after request paddingTop`() {
        assertLayoutNotCreated(textLayout) { paddingTop }
    }

    @Test
    fun `TextLayout don't create Layout after request paddingEnd`() {
        assertLayoutNotCreated(textLayout) { paddingEnd }
    }

    @Test
    fun `TextLayout don't create Layout after request paddingBottom`() {
        assertLayoutNotCreated(textLayout) { paddingBottom }
    }

    @Test
    fun `TextLayout don't create Layout after request getDesiredWidth()`() {
        assertLayoutNotCreated(textLayout) { getDesiredWidth("Test text") }
    }

    @Test
    fun `TextLayout don't create Layout after request width, when isVisible is equals false`() {
        assertLayoutNotCreated(TextLayout { isVisible = false }) { width }
    }

    @Test
    fun `TextLayout don't create Layout after request height, when isVisible is equals false`() {
        assertLayoutNotCreated(TextLayout { isVisible = false }) { height }
    }

    @Test
    fun `TextLayout create Layout after request height, when layoutWidth is equals 0`() {
        assertLayoutCreated(TextLayout { layoutWidth = 0 }) { height }
    }

    // endregion

    // region Create Layout

    @Test
    fun `TextLayout create Layout after request width`() {
        assertLayoutCreated(textLayout) { width }
    }

    @Test
    fun `TextLayout create Layout after request height`() {
        assertLayoutCreated(textLayout) { height }
    }

    @Test
    fun `TextLayout create Layout after request lineCount`() {
        assertLayoutCreated(textLayout) { lineCount }
    }

    @Test
    fun `TextLayout create Layout after request baseline`() {
        assertLayoutCreated(textLayout) { baseline }
    }

    @Test
    fun `TextLayout create Layout after call buildLayout(), when isVisible is equals true`() {
        assertLayoutCreated(textLayout) { buildLayout() }
        assertTrue(textLayout.isVisible)
    }

    @Test
    fun `TextLayout create new Layout after second call buildLayout(), when params is changed`() {
        textLayout.buildLayout()
        val layoutTextAfterFirstBuild = textLayout.stateSnapshot.layout!!.text

        val isChanged = textLayout.buildLayout { text = "123456" }

        assertNotEquals(layoutTextAfterFirstBuild, textLayout.stateSnapshot.layout!!.text)
        assertTrue(isChanged)
    }

    @Test
    fun `TextLayout create Layout after call layout(), when isVisible is equals true`() {
        assertLayoutCreated(textLayout) { layout(0, 0) }
        assertTrue(textLayout.isVisible)
    }

    // endregion
}
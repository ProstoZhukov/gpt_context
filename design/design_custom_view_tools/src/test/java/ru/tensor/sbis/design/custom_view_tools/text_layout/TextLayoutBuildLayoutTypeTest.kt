package ru.tensor.sbis.design.custom_view_tools.text_layout

import android.os.Build
import android.text.BoringLayout
import android.text.Layout
import android.text.SpannableString
import android.text.StaticLayout
import android.text.TextUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import ru.tensor.sbis.design.custom_view_tools.BORING_LAYOUT_TEXT_LENGTH_LIMIT
import ru.tensor.sbis.design.custom_view_tools.TextLayout

/**
 * Тесты [TextLayout] на предмет построения [Layout] типов [BoringLayout] или [StaticLayout],
 * в зависимости от параметров.
 *
 * @author vv.chekurda
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class TextLayoutBuildLayoutTypeTest {

    private val testText = "Test text test text"

    @Test
    fun `When text is short String, then build BoringLayout`() {
        val layout = TextLayout {
            text = testText
        }

        layout.buildLayout()

        assertTrue(layout.stateSnapshot.layout is BoringLayout)
    }

    @Test
    fun `When text is long String, then build StaticLayout`() {
        val layout = TextLayout {
            text = testText + testText + testText + testText + testText
        }

        layout.buildLayout()

        assertTrue(layout.stateSnapshot.layout is StaticLayout)
        assertTrue(layout.text.length > BORING_LAYOUT_TEXT_LENGTH_LIMIT)
    }

    @Test
    fun `When isSingleLine is true and text is long String, then build BoringLayout`() {
        val layout = TextLayout {
            isSingleLine = true
            text = testText + testText + testText + testText + testText
        }

        layout.buildLayout()

        assertTrue(layout.stateSnapshot.layout is BoringLayout)
        assertTrue(layout.text.length > BORING_LAYOUT_TEXT_LENGTH_LIMIT)
    }

    @Test
    fun `When isSingleLine is false and text is long String, then build StaticLayout`() {
        val layout = TextLayout {
            isSingleLine = false
            text = testText + testText + testText + testText + testText
        }

        layout.buildLayout()

        assertTrue(layout.stateSnapshot.layout is StaticLayout)
        assertTrue(layout.text.length > BORING_LAYOUT_TEXT_LENGTH_LIMIT)
    }

    @Test
    fun `When text is so long String and maxLines is unlimited, then build BoringLayout`() {
        val layout = TextLayout {
            text = testText + testText + testText + testText + testText
            maxLines = Int.MAX_VALUE
        }

        layout.buildLayout()

        assertTrue(layout.stateSnapshot.layout is BoringLayout)
        assertTrue(layout.text.length > BORING_LAYOUT_TEXT_LENGTH_LIMIT)
    }

    @Test
    fun `When text is changed, then TextLayout update older BoringLayout`() {
        val layout = TextLayout()

        layout.buildLayout { text = testText }
        val firstLayout = layout.stateSnapshot.layout

        layout.buildLayout { text = testText + testText + testText + testText + testText }
        val secondLayout = layout.stateSnapshot.layout

        layout.buildLayout { text = testText }
        val thirdLayout = layout.stateSnapshot.layout

        assertTrue(firstLayout is BoringLayout)
        assertTrue(secondLayout is StaticLayout)
        assertTrue(thirdLayout is BoringLayout)
        assertEquals(firstLayout, thirdLayout)
    }

    @Test
    fun `When layoutWidth is smaller than text width, maxLines is 1, ellipsize is null, then build StaticLayout`() {
        val layout = TextLayout {
            text = testText
            maxLines = 1
            ellipsize = null
        }
        val textWidth = layout.getDesiredWidth()
        val customWidth = textWidth - 1

        layout.buildLayout { layoutWidth = customWidth }

        assertTrue(layout.stateSnapshot.layout is StaticLayout)
        assertEquals(customWidth, layout.width)
    }

    @Test
    fun `When layoutWidth is smaller than text width, maxLines is 1, ellipsize is END, then build BoringLayout`() {
        val layout = TextLayout {
            text = testText
            maxLines = 1
            ellipsize = TextUtils.TruncateAt.END
        }
        val textWidth = layout.getDesiredWidth()
        val customWidth = textWidth - 1

        layout.buildLayout { layoutWidth = customWidth }

        assertTrue(layout.stateSnapshot.layout is BoringLayout)
        assertEquals(customWidth, layout.width)
    }

    @Test
    fun `When layoutWidth is smaller than text width, singleLine is true, ellipsize is null, then build BoringLayout`() {
        val layout = TextLayout {
            text = testText
            isSingleLine = true
            ellipsize = null
        }
        val textWidth = layout.getDesiredWidth()
        val customWidth = textWidth - 1

        layout.buildLayout { layoutWidth = customWidth }

        assertTrue(layout.stateSnapshot.layout is BoringLayout)
        assertEquals(customWidth, layout.width)
    }

    @Test
    fun `When layoutWidth is smaller than text width, singleLine is true, ellipsize is END, then build BoringLayout`() {
        val layout = TextLayout {
            text = testText
            isSingleLine = true
            ellipsize = TextUtils.TruncateAt.END
        }
        val textWidth = layout.getDesiredWidth()
        val customWidth = textWidth - 1

        layout.buildLayout { layoutWidth = customWidth }

        assertTrue(layout.stateSnapshot.layout is BoringLayout)
        assertEquals(customWidth, layout.width)
    }

    @Test
    fun `When singleLine is true and ellipsize is null, then build BoringLayout`() {
        val layout = TextLayout {
            text = testText
            isSingleLine = true
            ellipsize = null
        }

        layout.buildLayout()

        assertTrue(layout.stateSnapshot.layout is BoringLayout)
    }

    @Test
    fun `When singleLine is true and ellipsize is END, then build BoringLayout`() {
        val layout = TextLayout {
            text = testText
            isSingleLine = true
            ellipsize = TextUtils.TruncateAt.END
        }

        layout.buildLayout()

        assertTrue(layout.stateSnapshot.layout is BoringLayout)
    }

    @Test
    fun `When singleLine is true and maxLines is set, then build BoringLayout`() {
        val layout = TextLayout {
            text = testText
            isSingleLine = true
            maxLines = 3
        }

        layout.buildLayout()

        assertTrue(layout.stateSnapshot.layout is BoringLayout)
    }

    @Test
    fun `When text has multi lines and isSingleLine is true, then build StaticLayout`() {
        val layout = TextLayout {
            text = "1\n2\n3\n4\n5\n6\n7"
            isSingleLine = true
        }

        layout.buildLayout()

        assertTrue(layout.stateSnapshot.layout is StaticLayout)
    }

    @Test
    fun `When text is Spannable, then build StaticLayout`() {
        val layout = TextLayout {
            text = SpannableString(testText)
        }

        layout.buildLayout()

        assertTrue(layout.stateSnapshot.layout is StaticLayout)
    }
}
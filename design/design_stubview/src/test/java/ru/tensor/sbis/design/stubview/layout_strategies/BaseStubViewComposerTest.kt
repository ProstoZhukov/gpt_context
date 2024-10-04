package ru.tensor.sbis.design.stubview.layout_strategies

import android.view.View
import android.widget.TextView
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.junit.Assert.*
import org.junit.Test
import ru.tensor.sbis.design.sbis_text_view.SbisTextView
import ru.tensor.sbis.design.stubview.layout_strategies.icon_measuring_strategies.IconMeasuringStrategy

/**
 * @author ma.kolpakov
 */
class BaseStubViewComposerTest : BaseComposerTest() {

    private val mockIcon: View = mock()

    private val mockSbisTextView: SbisTextView = mock {
        on { text } doReturn "not empty"
    }
    private val mockTextView: TextView = mock {
        on { text } doReturn "not empty"
    }

    private val mockIconMeasuringStrategy: IconMeasuringStrategy = mock()

    private inner class TestViewComposer : BaseStubViewComposer(
        icon = mockIcon,
        message = mockSbisTextView,
        details = mockTextView,
        mockIconMeasuringStrategy,
        mockContext,
    ) {

        fun containerWidth() = containerWidth
        fun iconMinSize() = iconMinSize
        fun iconMaxSize() = iconMaxSize
        fun containerWidthWithPadding() = containerWidthWithPadding
        fun hasMessage() = hasMessage
        fun hasDetails() = hasDetails
        fun hasAnyText() = hasAnyText
        fun stubViewTopPadding() = stubViewTopPadding

        override fun layout(left: Int, top: Int, right: Int, bottom: Int) = Unit

        override fun maxHeight(): Int = 0
    }

    @Test
    fun `When measure() called, then IconMeasuringStrategy measure called`() {
        val composer = TestViewComposer()

        composer.measure(100, 100)

        verify(mockIconMeasuringStrategy).measure(
            icon = mockIcon,
            containerWidth = composer.containerWidth(),
            iconMinSize = composer.iconMinSize(),
            iconMaxSize = composer.iconMaxSize(),
        )
    }

    @Test
    fun `Default values test`() {
        val composer = TestViewComposer()

        assertEquals(0, composer.containerWidthWithPadding())
        assertFalse(composer.hasMessage())
        assertFalse(composer.hasDetails())
        assertFalse(composer.hasAnyText())
    }

    @Test
    fun `When measure() called, then update values`() {
        val composer = TestViewComposer()

        composer.measure(800, 600)

        assertNotEquals(0, composer.containerWidthWithPadding())
        assertTrue(composer.hasMessage())
        assertTrue(composer.hasDetails())
        assertTrue(composer.hasAnyText())
    }

    @Test
    fun `Given height 300, when measure(), then stubViewTopPadding equals to 30 - 10 percents`() {
        val composer = TestViewComposer()

        composer.measure(0, 300)

        assertEquals(30, composer.stubViewTopPadding())
    }

    @Test
    fun `Given height 1200, when measure(), then stubViewTopPadding equals to 120 - 10 percents`() {
        val composer = TestViewComposer()

        composer.measure(0, 1200)

        assertEquals(120, composer.stubViewTopPadding())
    }

    @Test
    fun `Given height 100, when measure(), then stubViewTopPadding equals to 12 - minimum padding`() {
        val composer = TestViewComposer()

        composer.measure(0, 100)

        assertEquals(STUB_VIEW_PADDING, composer.stubViewTopPadding())
    }
}

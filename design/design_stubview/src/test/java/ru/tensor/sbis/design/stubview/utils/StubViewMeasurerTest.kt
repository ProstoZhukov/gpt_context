package ru.tensor.sbis.design.stubview.utils

import android.os.Build
import android.view.View.MeasureSpec
import android.view.ViewGroup
import org.mockito.kotlin.*
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import ru.tensor.sbis.design.stubview.layout_strategies.StubViewComposer

/**
 * @author ma.kolpakov
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class StubViewMeasurerTest {

    private companion object {
        const val DEFAULT_PADDING_TOP = 0
        const val DEFAULT_PADDING_BOTTOM = 0

        const val MIN_HEIGHT = 100
        const val MAX_HEIGHT = 200
    }

    private val mockComposer: StubViewComposer = mock {
        on { maxHeight() } doReturn MAX_HEIGHT
    }

    @Test
    fun `When measure() called, then width equal to spec size`() {
        val spec = MeasureSpec.EXACTLY
        val measurer = getMeasurer(widthMeasureSpec = spec)

        val sizes = measurer.measure(null, isMinHeight = false)

        assertEquals(MeasureSpec.getSize(spec), sizes.width)
    }

    // region LayoutParams
    @Test
    fun `Given MATCH_PARENT, then then height is measured`() {
        val measurer = getMeasurer(
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(MAX_HEIGHT, MeasureSpec.EXACTLY),
            layoutParams = getLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT)
        )

        val sizes = measurer.measure(null, isMinHeight = true)

        assertEquals(MAX_HEIGHT, sizes.height)
    }

    @Test
    fun `Given WRAP_CONTENT and isMinHeight=true, then then height is min`() {
        val measurer = getMeasurer(layoutParams = getLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT))

        val sizes = measurer.measure(null, isMinHeight = true)

        assertEquals(MIN_HEIGHT, sizes.height)
    }

    @Test
    fun `Given WRAP_CONTENT and composer, then then height is max`() {
        val measurer = getMeasurer(layoutParams = getLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT))

        val sizes = measurer.measure(mockComposer, isMinHeight = false)

        assertEquals(MAX_HEIGHT, sizes.height)
    }

    @Test
    fun `Given WRAP_CONTENT and composer and isMinHeight=true, then then height is min`() {
        val measurer = getMeasurer(layoutParams = getLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT))

        val sizes = measurer.measure(mockComposer, isMinHeight = true)

        assertEquals(MIN_HEIGHT, sizes.height)
    }
    // endregion LayoutParams

    // region without composer
    @Test
    fun `Given spec=exactly, composer=null, isMinHeight=false, then height is measured`() {
        val measurer = getMeasurer(heightMeasureSpec = MeasureSpec.makeMeasureSpec(MAX_HEIGHT, MeasureSpec.EXACTLY))

        val sizes = measurer.measure(null, false)

        assertEquals(MAX_HEIGHT, sizes.height)
    }

    @Test
    fun `Given spec=at_most, composer=null, isMinHeight=false then height is min`() {
        val measurer = getMeasurer(heightMeasureSpec = MeasureSpec.AT_MOST)

        val sizes = measurer.measure(null, false)

        assertEquals(MIN_HEIGHT, sizes.height)
    }

    @Test
    fun `Given spec=unspecified, composer=null, isMinHeight=false, then height is minimal`() {
        val measurer = getMeasurer(heightMeasureSpec = MeasureSpec.UNSPECIFIED)

        val sizes = measurer.measure(null, false)

        assertEquals(MIN_HEIGHT, sizes.height)
    }

    @Test
    fun `Given spec=unspecified, composer=null, isMinHeight=true, then height is measured`() {
        val measurer = getMeasurer(heightMeasureSpec = MeasureSpec.UNSPECIFIED)

        val sizes = measurer.measure(null, true)

        assertEquals(MIN_HEIGHT, sizes.height)
    }

    // endregion without composer

    // region with composer
    @Test
    fun `Given spec=exactly, isMinHeight=false, then height is measured`() {
        val measurer = getMeasurer(heightMeasureSpec = MeasureSpec.makeMeasureSpec(MAX_HEIGHT, MeasureSpec.EXACTLY))

        val sizes = measurer.measure(mockComposer, false)

        assertEquals(MAX_HEIGHT, sizes.height)
        verify(mockComposer, times(1)).measure(any(), any())
    }

    @Test
    fun `Given spec=at_most, isMinHeight=false, then height is measured`() {
        val measurer = getMeasurer(heightMeasureSpec =  MeasureSpec.makeMeasureSpec(MAX_HEIGHT, MeasureSpec.AT_MOST))

        val sizes = measurer.measure(mockComposer, false)

        assertEquals(MAX_HEIGHT, sizes.height)
        verify(mockComposer, times(1)).measure(any(), any())
    }

    @Test
    fun `Given spec=unspecified isMinHeight=false, then height is max`() {
        val measurer = getMeasurer(heightMeasureSpec = MeasureSpec.UNSPECIFIED)

        val sizes = measurer.measure(mockComposer, false)

        assertEquals(MAX_HEIGHT, sizes.height)
        verify(mockComposer, times(2)).measure(any(), any())
    }

    @Test
    fun `Given spec=unspecified isMinHeight=true, then height is min`() {
        val measurer = getMeasurer(heightMeasureSpec = MeasureSpec.UNSPECIFIED)

        val sizes = measurer.measure(mockComposer, true)

        assertEquals(MIN_HEIGHT, sizes.height)
        verify(mockComposer, times(1)).measure(any(), any())
    }

    // endregion with composer

    private fun getMeasurer(
        layoutParams: ViewGroup.LayoutParams = getLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT),
        widthMeasureSpec: Int = MeasureSpec.EXACTLY,
        heightMeasureSpec: Int = MeasureSpec.EXACTLY,
        paddingTop: Int = DEFAULT_PADDING_TOP,
        paddingBottom: Int = DEFAULT_PADDING_BOTTOM,
    ) = StubViewMeasurer(
        minStubViewHeight = MIN_HEIGHT,
        layoutParams = layoutParams,
    ).apply {
        setSizes(
            widthMeasureSpec = widthMeasureSpec,
            heightMeasureSpec = heightMeasureSpec,
            paddingTop = paddingTop,
            paddingBottom = paddingBottom,
        )
    }

    private fun getLayoutParams(height: Int): ViewGroup.LayoutParams = ViewGroup.LayoutParams(0, height)

    private val Pair<Int, Int>.width: Int
        get() = this.first

    private val Pair<Int, Int>.height: Int
        get() = this.second
}

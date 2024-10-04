package ru.tensor.sbis.design.stubview.layout_strategies

import android.view.View
import android.widget.TextView
import org.junit.Assert.assertEquals
import org.junit.Test
import ru.tensor.sbis.design.sbis_text_view.SbisTextView
import ru.tensor.sbis.design.stubview.layout_strategies.icon_measuring_strategies.DrawableIconLandscapeMeasuringStrategy
import ru.tensor.sbis.design.stubview.layout_strategies.icon_measuring_strategies.ViewIconMeasuringStrategy

/**
 * @author ma.kolpakov
 */
class LandscapeStubViewComposerTest : BaseComposerTest() {

    private companion object {
        const val CONTAINER_WIDTH = 800
        const val CONTAINER_HEIGHT = 400
        const val TEXT_AREA_WIDTH = 440
        const val TEN_PERCENTS_OF_HEIGHT = 40
    }

    @Test
    fun `Given drawableIcon only, then icon centered horizontal`() {
        val icon = drawableIcon(mockDrawable(100, 100))
        val composer = getDrawableViewComposer(
            icon = icon,
            message = sbisTextView(""),
            details = textView(""),
        )

        composer.measureAndLayout()

        val testLeft = (CONTAINER_WIDTH - icon.measuredWidth) / 2

        assertEquals(testLeft, icon.left)
    }

    @Test
    fun `Given viewIcon only, then icon centered horizontal`() {
        val icon = viewIcon(textView("hello"))
        val composer = getViewComposer(
            icon = icon,
            message = sbisTextView(""),
            details = textView(""),
        )

        composer.measureAndLayout()

        val testLeft = (CONTAINER_WIDTH - icon.measuredWidth) / 2

        assertEquals(testLeft, icon.left)
    }

    @Test
    fun `Given texts only, then texts centered horizontal`() {
        val message = sbisTextView("Hello")
        val details = textView("My Friend")
        val composer = getDrawableViewComposer(
            icon = null,
            message = message,
            details = details,
        )

        composer.measureAndLayout()

        val testMessageLeft = (CONTAINER_WIDTH - message.measuredWidth) / 2
        val testDetailsLeft = (CONTAINER_WIDTH - details.measuredWidth) / 2

        assertEquals(testMessageLeft, message.left)
        assertEquals(testDetailsLeft, details.left)
    }

    @Test
    fun `Given big drawable icon and small texts, then check views positions`() {
        val icon = drawableIcon(mockDrawable(400, 400))
        val message = sbisTextView("Hello")
        val details = textView("My friend")
        val composer = getDrawableViewComposer(
            icon = icon,
            message = message,
            details = details,
        )

        composer.measureAndLayout()

        testPositionsWithBigIconAndTexts(icon, message, details)
    }

    @Test
    fun `Given big view icon and small texts, then check views positions`() {
        val icon = viewIcon(drawableIcon(mockDrawable(400, 400)))
        val message = sbisTextView("Hello")
        val details = textView("My friend")
        val composer = getViewComposer(
            icon = icon,
            message = message,
            details = details,
        )

        composer.measureAndLayout()

        testPositionsWithBigIconAndTexts(icon, message, details)
    }

    private fun testPositionsWithBigIconAndTexts(
        icon: View,
        message: View,
        details: View,
        topPadding: Int = 0,
    ) {
        val contentWidth = icon.measuredWidth + ICON_RIGHT_PADDING + TEXT_AREA_WIDTH
        val contentLeft = (CONTAINER_WIDTH - contentWidth) / 2
        val textsHeight = message.measuredHeight + MESSAGE_BOTTOM_PADDING + details.measuredHeight
        val textTop = (icon.measuredHeight - textsHeight) / 2 + TEN_PERCENTS_OF_HEIGHT + topPadding
        val messageLeft = icon.right + ICON_RIGHT_PADDING + (TEXT_AREA_WIDTH - message.measuredWidth) / 2
        val detailsLeft = icon.right + ICON_RIGHT_PADDING + (TEXT_AREA_WIDTH - details.measuredWidth) / 2

        assertEquals(TEN_PERCENTS_OF_HEIGHT + topPadding, icon.top)
        assertEquals(contentLeft, icon.left)

        assertEquals(textTop, message.top)
        assertEquals(messageLeft, message.left)

        assertEquals(message.bottom + MESSAGE_BOTTOM_PADDING, details.top)
        assertEquals(detailsLeft, details.left)
    }

    @Test
    fun `Given small drawable icon and big texts, then check views positions`() {
        val icon = drawableIcon(mockDrawable(20, 20))
        val message = sbisTextView("Hello\n".repeat(3).trim())
        val details = textView("My friend\n".repeat(5).trim())
        val composer = getDrawableViewComposer(
            icon = icon,
            message = message,
            details = details,
        )

        composer.measureAndLayout()

        testPositionsWithIconAndBigTexts(icon, message, details, icon.measuredWidth)
    }

    @Test
    fun `Given small view icon and big texts, then check views positions`() {
        val icon = viewIcon(drawableIcon(mockDrawable(20, 20)))
        val message = sbisTextView("Hello\n".repeat(3).trim())
        val details = textView("My friend\n".repeat(5).trim())
        val composer = getViewComposer(
            icon = icon,
            message = message,
            details = details,
        )

        composer.measureAndLayout()

        testPositionsWithIconAndBigTexts(icon, message, details, ICON_SIZE_MIN)
    }

    private fun testPositionsWithIconAndBigTexts(
        icon: View,
        message: View,
        details: View,
        iconAreaWidth: Int,
        topPadding: Int = 0,
    ) {
        val contentWidth = iconAreaWidth + ICON_RIGHT_PADDING + TEXT_AREA_WIDTH
        val contentLeft = (CONTAINER_WIDTH - contentWidth) / 2
        val iconLeft = contentLeft + (iconAreaWidth - icon.measuredWidth) / 2
        val iconRight = contentLeft + iconAreaWidth
        val textsHeight = message.measuredHeight + MESSAGE_BOTTOM_PADDING + details.measuredHeight
        val iconTop =
            TEN_PERCENTS_OF_HEIGHT + (textsHeight - ICON_SIZE_MIN) / 2 + (ICON_SIZE_MIN - icon.measuredHeight) / 2 + topPadding
        val messageLeft = iconRight + ICON_RIGHT_PADDING + (TEXT_AREA_WIDTH - message.measuredWidth) / 2
        val detailsLeft = iconRight + ICON_RIGHT_PADDING + (TEXT_AREA_WIDTH - details.measuredWidth) / 2

        assertEquals(iconTop, icon.top)
        assertEquals(iconLeft, icon.left)

        assertEquals(TEN_PERCENTS_OF_HEIGHT + topPadding, message.top)
        assertEquals(messageLeft, message.left)

        assertEquals(message.bottom + MESSAGE_BOTTOM_PADDING, details.top)
        assertEquals(detailsLeft, details.left)
    }

    @Test
    fun `Given small drawable icon and small texts, then check views positions`() {
        val icon = drawableIcon(mockDrawable(20, 20))
        val message = sbisTextView("Hello")
        val details = textView("My friend")
        val composer = getDrawableViewComposer(
            icon = icon,
            message = message,
            details = details,
        )

        composer.measureAndLayout()

        testPositionsWithSmallIconAndSmallTexts(icon, message, details, icon.measuredWidth)
    }

    @Test
    fun `Given small view icon and small texts, then check views positions`() {
        val icon = viewIcon(drawableIcon(mockDrawable(20, 20)))
        val message = sbisTextView("Hello")
        val details = textView("My friend")
        val composer = getViewComposer(
            icon = icon,
            message = message,
            details = details,
        )

        composer.measureAndLayout()

        testPositionsWithSmallIconAndSmallTexts(icon, message, details, ICON_SIZE_MIN)
    }

    private fun testPositionsWithSmallIconAndSmallTexts(
        icon: View,
        message: View,
        details: View,
        iconAreaWidth: Int,
    ) {
        val contentWidth = iconAreaWidth + ICON_RIGHT_PADDING + TEXT_AREA_WIDTH
        val contentLeft = (CONTAINER_WIDTH - contentWidth) / 2
        val iconLeft = contentLeft + (iconAreaWidth - icon.measuredWidth) / 2
        val iconTop =
            TEN_PERCENTS_OF_HEIGHT + (STUB_VIEW_MIN_HEIGHT_LANDSCAPE - ICON_SIZE_MIN) / 2 + (ICON_SIZE_MIN - icon.measuredHeight) / 2
        val iconRight = contentLeft + iconAreaWidth
        val textsHeight = message.measuredHeight + MESSAGE_BOTTOM_PADDING + details.measuredHeight
        val textTop = (ICON_SIZE_MIN - textsHeight) / 2 + TEN_PERCENTS_OF_HEIGHT
        val messageLeft = iconRight + ICON_RIGHT_PADDING + (TEXT_AREA_WIDTH - message.measuredWidth) / 2
        val detailsLeft = iconRight + ICON_RIGHT_PADDING + (TEXT_AREA_WIDTH - details.measuredWidth) / 2

        assertEquals(iconTop, icon.top)
        assertEquals(iconLeft, icon.left)

        assertEquals(textTop, message.top)
        assertEquals(messageLeft, message.left)

        assertEquals(message.bottom + MESSAGE_BOTTOM_PADDING, details.top)
        assertEquals(detailsLeft, details.left)
    }

    @Test
    fun `Given icon height smaller than container size, then icon is visible`() {
        val icon = drawableIcon(mockDrawable(100, 100))
        val composer = getDrawableViewComposer(icon)

        composer.measureAndLayout(300, 300)

        assertEquals(View.VISIBLE, icon.visibility)
    }

    @Test
    fun `Given icon height larger than container size, then hide icon`() {
        val icon = drawableIcon(mockDrawable(100, 100))
        val composer = getDrawableViewComposer(icon)

        composer.measureAndLayout(80, 80)

        assertEquals(View.GONE, icon.visibility)
    }

    @Test
    fun `When maxHeight() called, then return zero`() {
        val composer = getDrawableViewComposer()

        assertEquals(0, composer.maxHeight())
    }

    @Test // https://online.sbis.ru/doc/fe2db50c-1cfb-45b4-b6f1-afe38c004247
    fun `Given small icon and big texts,when top padding not zero, then apply padding`() {
        val topPadding = 12
        val icon = viewIcon(drawableIcon(mockDrawable(20, 20)))
        val message = sbisTextView("Hello\n".repeat(3).trim())
        val details = textView("My friend\n".repeat(5).trim())
        val composer = getViewComposer(
            icon = icon,
            message = message,
            details = details,
        )

        composer.measureAndLayout(top = topPadding)

        testPositionsWithIconAndBigTexts(icon, message, details, ICON_SIZE_MIN, topPadding = topPadding)
    }

    @Test // https://online.sbis.ru/doc/fe2db50c-1cfb-45b4-b6f1-afe38c004247
    fun `Given big view icon and small texts, when top padding not zero, then apply padding`() {
        val topPadding = 14
        val icon = viewIcon(drawableIcon(mockDrawable(400, 400)))
        val message = sbisTextView("Hello")
        val details = textView("My friend")
        val composer = getViewComposer(
            icon = icon,
            message = message,
            details = details,
        )

        composer.measureAndLayout(top = topPadding)

        testPositionsWithBigIconAndTexts(icon, message, details, topPadding = topPadding)
    }

    private fun getDrawableViewComposer(
        icon: View? = drawableIcon(),
        message: SbisTextView = sbisTextView("Message"),
        details: TextView = textView("Details"),
    ): StubViewComposer =
        LandscapeStubViewComposer(
            icon = icon,
            message = message,
            details = details,
            iconMeasuringStrategy = DrawableIconLandscapeMeasuringStrategy(),
            context = mockContext
        )

    private fun getViewComposer(
        icon: View? = drawableIcon(),
        message: SbisTextView = sbisTextView("Message"),
        details: TextView = textView("Details"),
    ): StubViewComposer =
        LandscapeStubViewComposer(
            icon = icon,
            message = message,
            details = details,
            iconMeasuringStrategy = ViewIconMeasuringStrategy(),
            context = mockContext
        )

    private fun StubViewComposer.measureAndLayout(
        width: Int = CONTAINER_WIDTH,
        height: Int = CONTAINER_HEIGHT,
        top: Int = 0
    ) {
        this.measure(width, height)
        this.layout(0, top, width, height)
    }
}

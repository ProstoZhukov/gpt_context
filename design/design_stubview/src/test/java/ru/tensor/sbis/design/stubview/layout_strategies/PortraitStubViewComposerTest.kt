package ru.tensor.sbis.design.stubview.layout_strategies

import android.view.View
import android.widget.TextView
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import ru.tensor.sbis.design.sbis_text_view.SbisTextView
import ru.tensor.sbis.design.stubview.layout_strategies.icon_measuring_strategies.DrawableIconPortraitMeasuringStrategy
import ru.tensor.sbis.design.stubview.layout_strategies.icon_measuring_strategies.ViewIconMeasuringStrategy

/**
 * @author ma.kolpakov
 */
class PortraitStubViewComposerTest : BaseComposerTest() {

    private companion object {
        const val CONTAINER_WIDTH = 400
        const val CONTAINER_HEIGHT = 800
        const val TEN_PERCENTS_OF_CONTAINER_HEIGHT = 80
    }

    @Test
    fun `Given drawableIcon and texts with enough height, then don't hide icon`() {
        val icon = drawableIcon(mockDrawable())
        val composer = getDrawableViewComposer(
            icon = icon
        )

        composer.measureAndLayout()

        assertEquals(View.VISIBLE, icon.visibility)
    }

    @Test
    fun `Given drawableIcon and texts and not enough height, then hide icon`() {
        val icon = drawableIcon(mockDrawable())
        val composer = getDrawableViewComposer(
            icon = icon
        )

        composer.measureAndLayout(height = 200)

        assertEquals(View.GONE, icon.visibility)
    }

    @Test
    fun `Given drawableIcon, then top padding is 10 percent`() {
        val icon = drawableIcon(mockDrawable())
        val composer = getDrawableViewComposer(
            icon = icon
        )

        composer.measureAndLayout()

        assertEquals(TEN_PERCENTS_OF_CONTAINER_HEIGHT, icon.top)
    }

    @Test
    fun `Given drawableIcon and container width 400, then image width is 200 - half`() {
        val icon = drawableIcon(mockDrawable())
        val composer = getDrawableViewComposer(
            icon = icon
        )

        composer.measureAndLayout(width = 400)

        assertEquals(200, icon.width)
    }

    @Test
    fun `Given drawableIcon and container width 100, then image width is 100 - min`() {
        val icon = drawableIcon(mockDrawable())
        val composer = getDrawableViewComposer(
            icon = icon
        )

        composer.measureAndLayout(width = 100)

        assertEquals(ICON_SIZE_MIN, icon.width)
    }

    @Test
    fun `Given drawableIcon and container width 1000, then image width is 250 - max`() {
        val icon = drawableIcon(mockDrawable(150, 150))
        val composer = getDrawableViewComposer(
            icon = icon
        )

        composer.measureAndLayout(width = 1000)

        assertEquals(ICON_SIZE_MAX, icon.width)
    }

    @Test
    fun `Given drawableIcon height 50, then iconHeight is 50`() {
        val icon = drawableIcon(mockDrawable(height = 50))
        val composer = getDrawableViewComposer(
            icon = icon
        )

        composer.measureAndLayout()

        assertEquals(50, icon.height)
    }

    @Test
    fun `Given drawableIcon height 1000, then iconHeight is 250 - max`() {
        val icon = drawableIcon(mockDrawable(height = 1000))
        val composer = getDrawableViewComposer(
            icon = icon
        )

        composer.measureAndLayout(width = 1000)

        assertEquals(ICON_SIZE_MAX, icon.height)
    }

    @Test
    fun `Given drawableIcon, then icon is centered horizontal`() {
        val icon = drawableIcon(mockDrawable())
        val composer = getDrawableViewComposer(
            icon = icon
        )

        composer.measureAndLayout(width = CONTAINER_WIDTH)

        val testLeft = (CONTAINER_WIDTH - icon.width) / 2

        assertEquals(testLeft, icon.left)
    }

    @Test
    fun `Given viewIcon and texts with and enough height, then don't hide icon`() {
        val icon = viewIcon(textView("omg"))
        val composer = getViewComposer(
            icon = icon
        )

        composer.measureAndLayout()

        assertEquals(View.VISIBLE, icon.visibility)
    }

    @Test
    fun `Given viewIcon with size 100, then icon width and height is 100`() {
        val icon = viewIcon(drawableIcon(mockDrawable(100, 100)))
        val composer = getViewComposer(
            icon = icon
        )

        composer.measureAndLayout()

        assertEquals(100, icon.width)
        assertEquals(100, icon.height)
    }

    @Test
    fun `Given viewIcon with size 1000, then icon width and height is 250 - min`() {
        val icon = viewIcon(drawableIcon(mockDrawable(1000, 1000)))
        val composer = getViewComposer(
            icon = icon
        )

        composer.measureAndLayout(1000, 1000)

        assertEquals(ICON_SIZE_MAX, icon.width)
        assertEquals(ICON_SIZE_MAX, icon.height)
    }

    @Test
    fun `Given viewIcon height smaller than 100 - min, icon top is in min square`() {
        val icon = viewIcon(drawableIcon(mockDrawable(50, 50)))
        val composer = getViewComposer(
            icon = icon
        )

        composer.measureAndLayout()

        val testTop = TEN_PERCENTS_OF_CONTAINER_HEIGHT + ICON_SIZE_MIN - icon.height

        assertEquals(testTop, icon.top)
    }

    @Test
    fun `Given viewIcon height larger than 100 - min, icon top is 10 percents of container height`() {
        val icon = viewIcon(drawableIcon(mockDrawable(120, 120)))
        val composer = getViewComposer(
            icon = icon
        )

        composer.measureAndLayout()

        assertEquals(TEN_PERCENTS_OF_CONTAINER_HEIGHT, icon.top)
    }

    @Test
    fun `Given viewIcon, then icon is centered horizontal`() {
        val icon = viewIcon(textView("omg"))
        val composer = getViewComposer(
            icon = icon
        )

        composer.measureAndLayout(width = CONTAINER_WIDTH)

        val testLeft = (CONTAINER_WIDTH - icon.width) / 2

        assertEquals(testLeft, icon.left)
    }

    @Test
    fun `Given texts, then texts width is not bigger than container width`() {
        val message = sbisTextView("t ".repeat(100))
        val details = textView("t ".repeat(100))
        val composer = getViewComposer(
            icon = null,
            message = message,
            details = details,
        )

        composer.measureAndLayout(width = CONTAINER_WIDTH)

        assertTrue(message.width < CONTAINER_WIDTH)
        assertTrue(details.width < CONTAINER_WIDTH)
    }

    @Test
    fun `Given texts, then texts are centered horizontal`() {
        val message = sbisTextView("some message")
        val details = textView("some details")
        val composer = getViewComposer(
            icon = null,
            message = message,
            details = details,
        )

        composer.measureAndLayout(width = CONTAINER_WIDTH)

        assertEquals((CONTAINER_WIDTH - message.width) / 2, message.left)
        assertEquals((CONTAINER_WIDTH - details.width) / 2, details.left)
    }

    @Test
    fun `Given no icon and texts, then texts are centered in min stub height`() {
        val message = sbisTextView("some message")
        val details = textView("some details")
        val composer = getViewComposer(
            icon = null,
            message = message,
            details = details,
        )

        composer.measureAndLayout()

        val textsHeight = message.height + details.height + MESSAGE_BOTTOM_PADDING
        val messageTop = (STUB_VIEW_MIN_HEIGHT_PORTRAIT - textsHeight) / 2 + TEN_PERCENTS_OF_CONTAINER_HEIGHT

        assertEquals(messageTop, message.top)
        assertEquals(message.bottom + MESSAGE_BOTTOM_PADDING, details.top)
    }

    @Test
    fun `Given no icon and details, then details is centered in min stub height`() {
        val details = textView("some details")
        val composer = getViewComposer(
            icon = null,
            message = sbisTextView(""),
            details = details,
        )

        composer.measureAndLayout()

        val detailsTop = (STUB_VIEW_MIN_HEIGHT_PORTRAIT - details.height) / 2 + TEN_PERCENTS_OF_CONTAINER_HEIGHT

        assertEquals(detailsTop, details.top)
    }

    @Test
    fun `Given drawable icon and texts, then texts are under icon`() {
        val icon = drawableIcon(mockDrawable())
        val message = sbisTextView("some message")
        val details = textView("some details")
        val composer = getDrawableViewComposer(
            icon = icon,
            message = message,
            details = details,
        )

        composer.measureAndLayout()

        assertEquals(icon.bottom + ICON_BOTTOM_PADDING, message.top)
        assertEquals(message.bottom + MESSAGE_BOTTOM_PADDING, details.top)
    }

    @Test
    fun `Given drawable icon and details, then details is under icon`() {
        val icon = drawableIcon(mockDrawable())
        val details = textView("some details")
        val composer = getDrawableViewComposer(
            icon = icon,
            message = sbisTextView(""),
            details = details,
        )

        composer.measureAndLayout()

        assertEquals(icon.bottom + ICON_BOTTOM_PADDING, details.top)
    }

    @Test
    fun `Given view icon and texts, then texts are under icon`() {
        val icon = viewIcon(drawableIcon(mockDrawable()))
        val message = sbisTextView("some message")
        val details = textView("some details")
        val composer = getViewComposer(
            icon = icon,
            message = message,
            details = details,
        )

        composer.measureAndLayout()

        assertEquals(icon.bottom + ICON_BOTTOM_PADDING, message.top)
        assertEquals(message.bottom + MESSAGE_BOTTOM_PADDING, details.top)
    }

    @Test
    fun `Given view icon and details, then details is under icon`() {
        val icon = viewIcon(drawableIcon(mockDrawable()))
        val details = textView("some details")
        val composer = getViewComposer(
            icon = icon,
            message = sbisTextView(""),
            details = details,
        )

        composer.measureAndLayout()

        assertEquals(icon.bottom + ICON_BOTTOM_PADDING, details.top)
    }

    @Test
    fun `Given small drawable icon and small message, then icon and message are centered in min stub height`() {
        val icon = drawableIcon(mockDrawable(30, 30))
        val message = sbisTextView("Hello")
        val composer = getViewComposer(
            icon = icon,
            message = message,
            details = textView("")
        )

        composer.measureAndLayout()

        val iconAndTextHeight = ICON_SIZE_MIN + ICON_BOTTOM_PADDING + message.height
        val topPadding = ICON_SIZE_MIN - icon.height
        val iconTop = (STUB_VIEW_MIN_HEIGHT_PORTRAIT - iconAndTextHeight) / 2 + TEN_PERCENTS_OF_CONTAINER_HEIGHT + topPadding


        assertEquals(iconTop, icon.top)
        assertEquals(icon.bottom + ICON_BOTTOM_PADDING, message.top)
    }

    @Test
    fun `Given small drawable icon and small details, then icon and details are centered in min stub height`() {
        val icon = drawableIcon(mockDrawable(30, 30))
        val details = textView("Hello!!1")
        val composer = getDrawableViewComposer(
            icon = icon,
            message = sbisTextView(""),
            details = details
        )

        composer.measureAndLayout()

        val iconAndTextHeight = ICON_SIZE_MIN + ICON_BOTTOM_PADDING + details.height
        val topPadding = ICON_SIZE_MIN - icon.height
        val iconTop = (STUB_VIEW_MIN_HEIGHT_PORTRAIT - iconAndTextHeight) / 2 + TEN_PERCENTS_OF_CONTAINER_HEIGHT + topPadding

        assertEquals(iconTop, icon.top)
        assertEquals(icon.bottom + ICON_BOTTOM_PADDING, details.top)
    }

    @Test
    fun `Given small view icon and small message, then icon and message are centered in min stub height`() {
        val icon = viewIcon(textView("custom"))
        val message = sbisTextView("Hello")
        val composer = getViewComposer(
            icon = icon,
            message = message,
            details = textView("")
        )

        composer.measureAndLayout()

        val iconAndTextHeight = ICON_SIZE_MIN + ICON_BOTTOM_PADDING + message.height
        val topPadding = ICON_SIZE_MIN - icon.height
        val iconTop = (STUB_VIEW_MIN_HEIGHT_PORTRAIT - iconAndTextHeight) / 2 + TEN_PERCENTS_OF_CONTAINER_HEIGHT + topPadding

        assertEquals(iconTop, icon.top)
        assertEquals(icon.bottom + ICON_BOTTOM_PADDING, message.top)
    }

    @Test
    fun `Given small view icon and small details, then icon and details are centered in min stub height`() {
        val icon = viewIcon(textView("custom"))
        val details = textView("Hello!!1")
        val composer = getViewComposer(
            icon = icon,
            message = sbisTextView(""),
            details = details
        )

        composer.measureAndLayout()

        val iconAndTextHeight = ICON_SIZE_MIN + ICON_BOTTOM_PADDING + details.height
        val topPadding = ICON_SIZE_MIN - icon.height
        val iconTop = (STUB_VIEW_MIN_HEIGHT_PORTRAIT - iconAndTextHeight) / 2 + TEN_PERCENTS_OF_CONTAINER_HEIGHT + topPadding

        assertEquals(iconTop, icon.top)
        assertEquals(icon.bottom + ICON_BOTTOM_PADDING, details.top)
    }

    @Test
    fun `Given all content, When maxHeight() called, then return content height plus paddings`() {
        val iconHeight = 130
        val messageHeight = 50
        val detailsHeight = 80
        val paddings = ICON_BOTTOM_PADDING + MESSAGE_BOTTOM_PADDING + STUB_VIEW_PADDING * 2
        val fullHeight = iconHeight + messageHeight + detailsHeight + paddings

        val icon: View = mockedHeightView(iconHeight)
        val message: SbisTextView = mockedHeightSbisTextView(messageHeight)
        val details: TextView = mockedHeightTextView(detailsHeight)
        val composer = getViewComposer(icon, message, details)

        composer.measure(800, 1200)

        assertEquals(fullHeight, composer.maxHeight())
    }

    @Test
    fun `Given icon only, When maxHeight() called, then return icon height plus paddings`() {
        val iconHeight = 200
        val fullHeight = iconHeight + STUB_VIEW_PADDING * 2

        val icon: View = mockedHeightView(iconHeight)
        val composer = getViewComposer(icon, sbisTextView(""), textView(""))

        composer.measure(800, 1200)

        assertEquals(fullHeight, composer.maxHeight())
    }

    @Test
    fun `Given texts only, When maxHeight() called, then return texsts height plus paddings`() {
        val messageHeight = 50
        val detailsHeight = 180
        val paddings = MESSAGE_BOTTOM_PADDING + STUB_VIEW_PADDING * 2
        val fullHeight = messageHeight + detailsHeight + paddings

        val message: SbisTextView = mockedHeightSbisTextView(messageHeight)
        val details: TextView = mockedHeightTextView(detailsHeight)
        val composer = getViewComposer(null, message, details)

        composer.measure(800, 1200)

        assertEquals(fullHeight, composer.maxHeight())
    }


    @Test // https://online.sbis.ru/doc/fe2db50c-1cfb-45b4-b6f1-afe38c004247
    fun `Given top padding not zero, then apply padding`() {
        val topPadding = 22
        val icon = viewIcon(drawableIcon(mockDrawable(120, 120)))
        val composer = getViewComposer(
            icon = icon
        )

        composer.measureAndLayout(top = topPadding)

        assertEquals(TEN_PERCENTS_OF_CONTAINER_HEIGHT + topPadding, icon.top)
    }

    private fun mockedHeightView(height: Int): View = mock {
        on { measuredHeight } doReturn height
    }

    private fun mockedHeightTextView(height: Int): TextView = mock {
        on { measuredHeight } doReturn height
        on { text } doReturn "notEmptyText"
    }

    private fun mockedHeightSbisTextView(height: Int): SbisTextView = mock {
        on { measuredHeight } doReturn height
        on { text } doReturn "notEmptyText"
    }

    private fun getDrawableViewComposer(
        icon: View? = drawableIcon(),
        message: SbisTextView = sbisTextView("Message"),
        details: TextView = textView("Details"),
    ): StubViewComposer =
        PortraitStubViewComposer(
            icon = icon,
            message = message,
            details = details,
            iconMeasuringStrategy = DrawableIconPortraitMeasuringStrategy(),
            context = mockContext
        )

    private fun getViewComposer(
        icon: View? = drawableIcon(),
        message: SbisTextView = sbisTextView("Message"),
        details: TextView = textView("Details"),
    ): StubViewComposer =
        PortraitStubViewComposer(
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

package ru.tensor.sbis.design.design_menu.utils.shadow

import androidx.recyclerview.widget.RecyclerView
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import ru.tensor.sbis.design.design_menu.view.shadow.ScrollBoundsListener
import ru.tensor.sbis.design.design_menu.view.shadow.ShadowPosition

/**
 * Тесты view тени для меню быстрых действий.
 *
 * @author ra.geraskin
 */
private const val TOUCH_INDICATOR_VALUE = 1
private const val DETACH_INDICATOR_VALUE = -1

@RunWith(MockitoJUnitRunner.StrictStubs::class)
class ScrollBoundsListenerTest {

    private val recyclerViewMock: RecyclerView = mockk()

    private var indicator = 0
    private val detach: () -> Unit = { indicator = DETACH_INDICATOR_VALUE }
    private val touch: () -> Unit = { indicator = TOUCH_INDICATOR_VALUE }

    @Test
    fun `When the recyclerView has touched the top border, then the top bounds listener calls the onTouch method`() {
        val position = ShadowPosition.TOP
        val canScrollToDirection = false
        every { recyclerViewMock.canScrollVertically(position.scrollDirection) } returns (canScrollToDirection)
        val scrollBoundsListener = ScrollBoundsListener(position.scrollDirection, touch, detach)

        // act
        scrollBoundsListener.onScrolled(recyclerViewMock, 0, 0)

        // verify
        assertEquals(indicator, TOUCH_INDICATOR_VALUE)
    }

    @Test
    fun `When the recyclerView comes off the top bound, then the top bounds listener calls the onDetach method`() {
        val position = ShadowPosition.TOP
        val canScrollToDirection = true
        every { recyclerViewMock.canScrollVertically(position.scrollDirection) } returns (canScrollToDirection)
        val scrollBoundsListener = ScrollBoundsListener(position.scrollDirection, touch, detach)

        // act
        scrollBoundsListener.onScrolled(recyclerViewMock, 0, 0)

        // verify
        assertEquals(indicator, DETACH_INDICATOR_VALUE)
    }

    @Test
    fun `When the recyclerView has touched the bottom border, then the bottom bounds listener calls the onTouch method`() {
        val position = ShadowPosition.BOTTOM
        val canScrollToDirection = false
        every { recyclerViewMock.canScrollVertically(position.scrollDirection) } returns (canScrollToDirection)
        val scrollBoundsListener = ScrollBoundsListener(position.scrollDirection, touch, detach)

        // act
        scrollBoundsListener.onScrolled(recyclerViewMock, 0, 0)

        // verify
        assertEquals(indicator, TOUCH_INDICATOR_VALUE)
    }

    @Test
    fun `When the recyclerView comes off the bottom bound, then the bottom bounds listener calls the onDetach method`() {
        val position = ShadowPosition.BOTTOM
        val canScrollToDirection = true
        every { recyclerViewMock.canScrollVertically(position.scrollDirection) } returns (canScrollToDirection)
        val scrollBoundsListener = ScrollBoundsListener(position.scrollDirection, touch, detach)

        // act
        scrollBoundsListener.onScrolled(recyclerViewMock, 0, 0)

        // verify
        assertEquals(indicator, DETACH_INDICATOR_VALUE)
    }

}
package ru.tensor.sbis.design.toolbar.appbar.offset

import com.google.android.material.appbar.AppBarLayout
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.only
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import kotlin.math.absoluteValue

/**
 * @author ma.kolpakov
 * Создан 9/25/2019
 */
class NormalOffsetChangeListenerTest {

    private val collapsedOffset = -100
    private val expandedOffset = 0

    private val view: AppBarLayout = mock()
    private val observer: NormalOffsetObserver = mock()
    private val observersList = listOf(observer)

    private val listener = NormalOffsetChangeListener(observersList) { collapsedOffset.absoluteValue }

    @Test
    fun `When offset is in collapsed state, then normal offset should be zero`() {
        listener.onOffsetChanged(view, collapsedOffset)

        verify(observer, only()).onOffsetChanged(0F)
        verifyNoMoreInteractions(view)
    }

    @Test
    fun `When offset is in expended state, then normal offset should equal to 1`() {
        listener.onOffsetChanged(view, expandedOffset)

        verify(observer, only()).onOffsetChanged(1F)
        verifyNoMoreInteractions(view)
    }

    @Test
    fun `When offset is in middle state, then normal offset should be 0,5`() {
        val middleOffset = (expandedOffset - collapsedOffset) / 2
        listener.onOffsetChanged(view, middleOffset)

        verify(observer, only()).onOffsetChanged(0.5F)
        verifyNoMoreInteractions(view)
    }
}
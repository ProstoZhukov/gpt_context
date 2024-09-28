package ru.tensor.sbis.list.view.utils

import android.view.View
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever
import org.junit.Before
import org.junit.Test

import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

/**
 * @author du.bykov
 */
@RunWith(MockitoJUnitRunner.StrictStubs::class)
class ItemClickListenerFactoryTest {

    private val startTime = System.currentTimeMillis()

    @Mock
    private lateinit var view: View

    @Mock
    private lateinit var actionA: () -> Unit

    @Mock
    private lateinit var actionB: () -> Unit

    @Mock
    private lateinit var timeFunction: () -> Long

    private lateinit var clickDelegate: ItemClickListenerFactory

    @Before
    fun setUp() {
        clickDelegate = ItemClickListenerFactory(ITEM_CLICK_INTERVAL, timeFunction)

        whenever(timeFunction.invoke()).thenReturn(startTime)
    }

    @Test
    fun `When view clicked, then action should be invoked`() {
        clickDelegate.createClickListener(actionA).onClick(view)

        verify(actionA).invoke()
    }

    @Test
    fun `When view clicked twice, then action should be invoked only once`() {
        clickDelegate.createClickListener(actionA).apply {
            onClick(view)
            onClick(view)
        }

        verify(actionA).invoke()
    }

    @Test
    fun `When view clicked and then clicked another one during interval, then only first action should be invoked`() {
        val listenerA = clickDelegate.createClickListener(actionA)
        val listenerB = clickDelegate.createClickListener(actionB)
        whenever(timeFunction.invoke()).thenReturn(startTime, startTime + ITEM_CLICK_INTERVAL - 1)

        listenerB.onClick(view)
        listenerA.onClick(view)

        verify(actionB).invoke()
        verifyNoMoreInteractions(actionA)
    }

    @Test
    fun `When view clicked and then clicked another one, then both actions should be invoked`() {
        val listenerA = clickDelegate.createClickListener(actionA)
        val listenerB = clickDelegate.createClickListener(actionB)
        whenever(timeFunction.invoke()).thenReturn(startTime, startTime + ITEM_CLICK_INTERVAL)

        listenerB.onClick(view)
        listenerA.onClick(view)

        verify(actionB).invoke()
        verify(actionA).invoke()
    }
}
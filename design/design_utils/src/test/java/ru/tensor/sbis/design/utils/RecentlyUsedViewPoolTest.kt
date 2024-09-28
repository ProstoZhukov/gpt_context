package ru.tensor.sbis.design.utils

import android.view.View
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

private const val POOL_CAPACITY = 3

/**
 * @author us.bessonov
 */
@RunWith(MockitoJUnitRunner.StrictStubs::class)
class RecentlyUsedViewPoolTest {

    private val mockFactory = mock<() -> View> {
        on { invoke() } doAnswer { mock { } }
    }
    private val pool = RecentlyUsedViewPool<View, Int>(POOL_CAPACITY, mockFactory)

    @Test
    fun `When there is a view that was previously used for specified id, then returns this view`() {
        val view1 = pool.get(1)
        val view2 = pool.get(2)
        val view3 = pool.get(3)
        pool.recycle(view1)
        pool.recycle(view2)
        pool.recycle(view3)

        assertEquals(view2, pool.get(2))
        assertEquals(view3, pool.get(3))
        assertEquals(view1, pool.get(1))
    }

    @Test
    fun `When there is no view that was previously used for specified id, then returns other available view`() {
        val oldView = pool.get(1)
        pool.recycle(oldView)

        assertEquals(oldView, pool.get(2))
    }

    @Test
    fun `When there is no view that was previously used for specified id, then first of all returns a view with null id`() {
        val oldView1 = pool.get(1)
        val oldView2 = pool.get(null)
        pool.recycle(oldView1)
        pool.recycle(oldView2)

        assertEquals(oldView2, pool.get(2))
    }

    @Test
    fun `When recycled multiple views used for specified id, then all of them can be obtained by this id`() {
        val view1 = pool.get(1)
        val view2 = pool.get(1)
        val view3 = pool.get(1)
        pool.recycle(view1)
        pool.recycle(view2)
        pool.recycle(view3)

        assertEquals(view1, pool.get(1))
        assertEquals(view2, pool.get(1))
        assertEquals(view3, pool.get(1))
    }


    @Test
    fun `When view is recycled several times, it can only be obtained once`() {
        val view = pool.get(1)
        pool.recycle(view)
        pool.recycle(view)
        pool.recycle(view)

        assertEquals(view, pool.get(1))
        assertNotEquals(view, pool.get(1))
    }

    @Test
    fun `When max capacity is reached, then no more views added to cache`() {
        val view1 = pool.get(1)
        val view2 = pool.get(2)
        val view3 = pool.get(3)
        val view4 = pool.get(4)
        pool.recycle(view1)
        pool.recycle(view2)
        pool.recycle(view3)
        pool.recycle(view4)

        assertEquals(view3, pool.get(3))
        assertNotEquals(view4, pool.get(4))
    }

    @Test
    fun `When inflation requested, then it doesn't inflate more views than needed`() {
        val view = pool.get(1)
        pool.recycle(view)
        pool.inflate(5)

        verify(mockFactory, times(3)).invoke()
    }

    @Test
    fun `When additional inflation requested, then required view count is inflated`() {
        val view = pool.get(1)
        pool.recycle(view)
        pool.inflateBy(1)

        verify(mockFactory, times(2)).invoke()
    }

    @Test
    fun `When inflation requested, then all inflated views can be obtained by any key`() {
        pool.inflate(3)
        pool.get(null)
        pool.get(null)
        pool.get(1)

        verify(mockFactory, times(3)).invoke()
    }

    @Test
    fun `When pool is flushed, then there are no cached views available`() {
        val view1 = pool.get(1)
        pool.recycle(view1)
        pool.flush()

        assertNotEquals(view1, pool.get(1))
    }
}
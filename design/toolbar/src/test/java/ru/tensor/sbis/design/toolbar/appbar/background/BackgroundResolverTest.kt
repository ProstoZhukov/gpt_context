package ru.tensor.sbis.design.toolbar.appbar.background

import com.facebook.drawee.generic.GenericDraweeHierarchy
import com.facebook.drawee.view.DraweeView
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

/**
 * @author ma.kolpakov
 * Создан 9/25/2019
 */
@RunWith(MockitoJUnitRunner.StrictStubs::class)
class BackgroundResolverTest {

    @Mock
    private lateinit var ratioListener: AspectRatioChangeListener

    @Test
    fun `Default strategy for View`() {
        val strategy = resolveBackgroundStrategy(mock(), ratioListener)

        assertThat(strategy, instanceOf(DefaultBackgroundStrategy::class.java))
    }

    @Test
    fun `DraweeViewStrategy for DraweeView`() {
        val view: DraweeView<GenericDraweeHierarchy> = mock()
        whenever(view.hierarchy).thenReturn(mock())

        val strategy = resolveBackgroundStrategy(view, ratioListener)

        assertThat(strategy, instanceOf(DraweeViewBackgroundStrategy::class.java))
    }

    @Test(expected = IllegalArgumentException::class)
    fun `Check restrictions DraweeView hierarchy`() {
        // поддерживаются не все иерархии
        val view: DraweeView<*> = mock()
        whenever(view.hierarchy).thenReturn(mock())

        val strategy = resolveBackgroundStrategy(view, ratioListener)

        assertThat(strategy, instanceOf(DraweeViewBackgroundStrategy::class.java))
    }
}
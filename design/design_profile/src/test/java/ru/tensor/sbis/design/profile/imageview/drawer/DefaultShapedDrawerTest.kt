package ru.tensor.sbis.design.profile.imageview.drawer

import android.graphics.Canvas
import android.graphics.drawable.Drawable
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.mockk.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import ru.tensor.sbis.design.profile.personcollagelist.contentviews.CounterDrawer
import ru.tensor.sbis.fresco_view.shapeddrawer.ShapedImageView
import ru.tensor.sbis.fresco_view.shapeddrawer.SwapBufferShapedImageDrawer

@RunWith(AndroidJUnit4::class)
class DefaultShapedDrawerTest {

    private lateinit var shapedImageView: ShapedImageView
    private lateinit var counterDrawer: CounterDrawer
    private lateinit var defaultShapedDrawer: DefaultShapedDrawer

    @Before
    fun setUp() {
        shapedImageView = mockk()
        counterDrawer = mockk()

        mockkConstructor(SwapBufferShapedImageDrawer::class)
        every { anyConstructed<SwapBufferShapedImageDrawer>().setOnDrawForeground(any()) } just Runs
        every { anyConstructed<SwapBufferShapedImageDrawer>().setShape(any()) } just Runs
        every { anyConstructed<SwapBufferShapedImageDrawer>().onDraw(any()) } just Runs
        every { anyConstructed<SwapBufferShapedImageDrawer>().onSizeChanged(any(), any(), any(), any()) } just Runs
        every { anyConstructed<SwapBufferShapedImageDrawer>().setBackgroundColor(any()) } just Runs
        every { anyConstructed<SwapBufferShapedImageDrawer>().invalidate() } just Runs

        defaultShapedDrawer = DefaultShapedDrawer(shapedImageView, counterDrawer)
    }

    @Test
    fun `SetShape Should Call Drawer SetShape`() {
        val shape: Drawable = mockk()

        defaultShapedDrawer.setShape(shape)

        verify { anyConstructed<SwapBufferShapedImageDrawer>().setShape(shape) }
    }

    @Test
    fun `OnDraw Should Call Drawer OnDraw`() {
        val canvas: Canvas = mockk()

        defaultShapedDrawer.onDraw(null, canvas)

        verify { anyConstructed<SwapBufferShapedImageDrawer>().onDraw(canvas) }
    }

    @Test
    fun `OnSizeChanged Should Call Drawer OnSizeChanged`() {
        val w = 100
        val h = 200
        val oldW = 50
        val oldH = 100

        defaultShapedDrawer.onSizeChanged(w, h, oldW, oldH)

        verify { anyConstructed<SwapBufferShapedImageDrawer>().onSizeChanged(w, h, oldW, oldH) }
    }

    @Test
    fun `SetBackgroundColor Should Call Drawer SetBackgroundColor`() {
        val color = 0xFFFFFF

        defaultShapedDrawer.setBackgroundColor(color)

        verify { anyConstructed<SwapBufferShapedImageDrawer>().setBackgroundColor(color) }
    }

    @Test
    fun `Invalidate Should Call Drawer Invalidate`() {
        defaultShapedDrawer.invalidate()

        verify { anyConstructed<SwapBufferShapedImageDrawer>().invalidate() }
    }
}
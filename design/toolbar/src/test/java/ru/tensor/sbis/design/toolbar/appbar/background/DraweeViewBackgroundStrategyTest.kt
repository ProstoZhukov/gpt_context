package ru.tensor.sbis.design.toolbar.appbar.background

import com.facebook.drawee.controller.AbstractDraweeController
import com.facebook.drawee.controller.AbstractDraweeControllerBuilder
import com.facebook.drawee.controller.BaseControllerListener
import com.facebook.drawee.drawable.ScalingUtils
import com.facebook.drawee.generic.GenericDraweeHierarchy
import com.facebook.drawee.view.DraweeView
import com.facebook.imagepipeline.image.ImageInfo
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.only
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import ru.tensor.sbis.design.toolbar.appbar.model.ColorBackground
import ru.tensor.sbis.design.toolbar.appbar.model.ImageBackground
import ru.tensor.sbis.design.toolbar.appbar.model.UndefinedBackground
import kotlin.random.Random

/**
 * @author ma.kolpakov
 * Создан 9/25/2019
 */
class DraweeViewBackgroundStrategyTest {

    private val url = "https://www.example.com"

    private val view: DraweeView<GenericDraweeHierarchy> = mock()
    private val hierarchy: GenericDraweeHierarchy = mock()

    private val controller: AbstractDraweeController<*, *> = mock()
    private val builder: AbstractDraweeControllerBuilder<*, *, *, ImageInfo> = mock()
    private val aspectRatioChangedCallback: AspectRatioChangeListener = mock()

    private val strategy: BackgroundStrategy =
        DraweeViewBackgroundStrategy(view, { builder }, aspectRatioChangedCallback)

    @Before
    fun setUp() {
        whenever(view.hierarchy).thenReturn(hierarchy)

        whenever(builder.build()).thenReturn(controller)
    }

    @Test
    fun `Clear background test`() {
        strategy.setModel(UndefinedBackground)

        verify(view, only()).controller = null
    }

    @Test
    fun `Set color background test`() {
        val model = ColorBackground(Random.nextInt())

        strategy.setModel(model)

        verify(view, only()).setBackgroundColor(model.color)
    }

    @Test
    fun `Set image background test`() {
        val placeholderRes = Random.nextInt()
        val model = ImageBackground(url, placeholderRes)

        strategy.setModel(model)

        verify(hierarchy).setPlaceholderImage(placeholderRes, ScalingUtils.ScaleType.CENTER_CROP)
        verify(view).controller = controller
    }

    @Test
    fun `Set image background without placeholder test `() {
        val model = ImageBackground(url)

        strategy.setModel(model)

        verify(view).controller
        verify(view).controller = controller
    }

    @Test
    fun `When image loaded, invokes callback with aspect ratio`() {
        val placeholderRes = Random.nextInt()
        val model = ImageBackground(url, placeholderRes)
        val listenerCaptor = argumentCaptor<BaseControllerListener<ImageInfo>>()
        val mockImageInfo = mock<ImageInfo> {
            on { width } doReturn 3
            on { height } doReturn 4
        }

        strategy.setModel(model)

        verify(builder).setControllerListener(listenerCaptor.capture())
        listenerCaptor.firstValue.onFinalImageSet("Id", mockImageInfo, null)
        verify(aspectRatioChangedCallback).onAspectRatioChanged(0.75f)
    }
}
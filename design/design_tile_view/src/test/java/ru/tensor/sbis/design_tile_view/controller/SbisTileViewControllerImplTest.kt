package ru.tensor.sbis.design_tile_view.controller

import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.PaintDrawable
import android.view.View
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import junitparams.JUnitParamsRunner
import junitparams.Parameters
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.mockito.quality.Strictness
import ru.tensor.sbis.design.R
import ru.tensor.sbis.design.theme.VerticalAlignment
import ru.tensor.sbis.design.utils.GradientShaderFactory
import ru.tensor.sbis.design.utils.image_loading.ImageUrl
import ru.tensor.sbis.design.utils.image_loading.ViewImageLoader
import ru.tensor.sbis.design_tile_view.Circle
import ru.tensor.sbis.design_tile_view.ImagePlaceholder
import ru.tensor.sbis.design_tile_view.Rectangle
import ru.tensor.sbis.design_tile_view.SbisTileViewImageAlignment
import ru.tensor.sbis.design_tile_view.SbisTileViewImageModel
import ru.tensor.sbis.design_tile_view.SbisTileViewImageRatio
import ru.tensor.sbis.design_tile_view.TextIconPlaceholder
import ru.tensor.sbis.design_tile_view.TileViewImageUrl
import ru.tensor.sbis.design_tile_view.util.ImageBorderDrawer
import ru.tensor.sbis.design_tile_view.view.SbisTileView
import ru.tensor.sbis.design_tile_view.view.TileImageView

private const val IMAGE_SIZE = 10
private const val IMAGE_PADDING = 3

/**
 * @author us.bessonov
 */
@RunWith(JUnitParamsRunner::class)
class SbisTileViewControllerImplTest {

    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS)

    private lateinit var controller: SbisTileViewControllerImpl

    @Mock
    private lateinit var mockImageLoader: ViewImageLoader

    @Mock
    private lateinit var mockView: SbisTileView

    @Mock
    private lateinit var mockImageView: TileImageView

    @Mock
    private lateinit var mockResources: Resources

    @Mock
    private lateinit var mockImageGradient: PaintDrawable

    @Mock
    private lateinit var mockBackground: GradientDrawable

    @Mock
    private lateinit var mockBorderDrawer: ImageBorderDrawer

    @Before
    fun setUp() {
        val mockContext = mock<Context>()
        whenever(mockContext.resources).thenReturn(mockResources)
        whenever(mockView.context).thenReturn(mockContext)
        val mockBackgroundDrawableFactory = mock<(Int) -> GradientDrawable>()
        whenever(mockBackgroundDrawableFactory.invoke(anyInt())).thenReturn(mockBackground)
        val mockImageGradientDrawableFactory = mock<() -> PaintDrawable>()
        whenever(mockImageGradientDrawableFactory.invoke()).thenReturn(mockImageGradient)
        val createMeasureSpec: (Int, Int) -> Int = { size, _ -> size }
        controller = SbisTileViewControllerImpl(
            mockImageLoader,
            mockImageGradientDrawableFactory,
            mockBackgroundDrawableFactory,
            createMeasureSpec
        )
        controller.init(
            mockView,
            mockImageView,
            IMAGE_SIZE,
            IMAGE_PADDING,
            isBorderEnabled = true,
            isBorderUnderContent = false,
            mockBorderDrawer
        )
    }

    @Test
    fun `When controller is initialized, then image loader is also initialized as expected`() {
        verify(mockImageLoader).init(eq(mockView), eq(mockImageView), any(), any(), any())
    }

    @Test
    fun `When image model is set, then it is applied correctly, and image loading is started`() {
        val images = listOf(TileViewImageUrl("Url1"), TileViewImageUrl("Url2"))
        val placeholderIcon = "Icon"
        val isDarkerPlaceholder = true
        val model = SbisTileViewImageModel(
            images,
            SbisTileViewImageAlignment.LEFT,
            Circle,
            TextIconPlaceholder(placeholderIcon),
            darkerPlaceholder = isDarkerPlaceholder
        )

        controller.setImageModel(model)

        verify(mockImageView).configureTint(
            isImageTintEnabled = false,
            isPlaceholderTintEnabled = isDarkerPlaceholder,
            isTintUnderContent = true
        )
        verify(mockImageLoader).setImages(images.map { ImageUrl(it.imageUrl) })
        verify(mockImageView).setShape(model.shape)
        verify(mockImageView).setPlaceholder(placeholderIcon)
        verify(mockView).requestLayout()
    }

    @Test
    fun `When image model is set, and it is the same as before, and result is already received, then there is no action`() {
        val model = SbisTileViewImageModel(null, SbisTileViewImageAlignment.TOP, Rectangle())
        whenever(mockImageLoader.hasResult()).thenReturn(true)

        controller.setImageModel(model)
        controller.setImageModel(model)

        verify(mockImageLoader).setImages(any(), any())
    }

    @Test
    fun `When drawable placeholder is set, then it is applied to image view`() {
        val drawable = mock<Drawable>()
        whenever(mockResources.getDrawable(anyInt())).thenReturn(drawable)

        controller.setPlaceholder(ImagePlaceholder(111))

        verify(mockImageView).setPlaceholder(drawable)
    }

    @Test
    fun `When new content alignment is set, then image tint is updated, and view layout is requested`() {
        controller.setContentAlignment(VerticalAlignment.CENTER)

        verify(mockImageView).configureTint(
            isImageTintEnabled = false,
            isPlaceholderTintEnabled = false,
            isTintUnderContent = false
        )
        verify(mockView).invalidate()
        verify(mockView).requestLayout()
    }

    @Test
    fun `When new corner radius is set, then it is updated and view is invalidated`() {
        val radius = 123f
        val shape = Rectangle()

        controller.setImageModel(SbisTileViewImageModel(null, SbisTileViewImageAlignment.TOP, shape))
        controller.setCornerRadius(radius)

        verify(mockBackground).cornerRadius = radius
        Assert.assertArrayEquals(arrayOf(radius, radius, radius, radius, 0f, 0f, 0f, 0f), shape.radii.toTypedArray())
        verify(mockImageView, times(2)).setShape(shape)
        verify(mockView, times(2)).invalidate()
    }

    @Test
    fun `When shadow is enabled, then view elevation is set and view is invalidated`() {
        val elevation = 123f
        whenever(mockView.resources).thenReturn(mockResources)
        whenever(mockResources.getDimension(R.dimen.elevation_high)).thenReturn(elevation)

        controller.setNeedSetupShadow(true)

        verify(mockView).elevation = elevation
        verify(mockView).invalidate()
    }

    @Test
    fun `When background colors are set, then they are applied properly and view is invalidated`() {
        val argumentCaptor = argumentCaptor<GradientShaderFactory>()
        val startColor = Color.GREEN
        val endColor = Color.BLUE

        controller.setStartBackgroundColor(startColor)
        controller.setEndBackgroundColor(endColor)

        verify(mockBackground).colors = intArrayOf(Color.GREEN, Color.BLUE)
        verify(mockImageGradient, times(3)).shaderFactory = argumentCaptor.capture()
        assertEquals(argumentCaptor.lastValue.color, endColor)
        verify(mockView, times(2)).invalidate()
    }

    @Test
    fun `When content view is set, then image tint is updated and view layout is requested`() {
        val content = mock<View>()

        controller.setImageModel(SbisTileViewImageModel(null, SbisTileViewImageAlignment.FILL, Rectangle()))
        controller.setContentView(content)

        verify(mockImageView).configureTint(
            isImageTintEnabled = false,
            isPlaceholderTintEnabled = false,
            isTintUnderContent = true
        )
        verify(mockImageView).configureTint(
            isImageTintEnabled = true,
            isPlaceholderTintEnabled = false,
            isTintUnderContent = true
        )
        verify(mockView).invalidate()
        verify(mockView, times(2)).requestLayout()
    }

    @Test
    fun `When top view is set, then layout is requested`() {
        controller.setTopView(mock())

        verify(mockView).requestLayout()
    }

    @Test
    fun `When bottom view is set, then layout is requested`() {
        controller.setBottomView(mock())

        verify(mockView).requestLayout()
    }

    @Test
    fun `When view is drawn, then all contents are drawn`() {
        val canvas = mock<Canvas>()
        val mockTopView = mock<View>()
        val mockBottomView = mock<View>()
        val mockContentView = mock<View>()

        with(controller) {
            setImageModel(
                SbisTileViewImageModel(
                    TileViewImageUrl("Url"),
                    SbisTileViewImageAlignment.TOP,
                    Rectangle(needGradient = true)
                )
            )
            setTopView(mockTopView)
            setBottomView(mockBottomView)
            setContentView(mockContentView)
            performDraw(canvas)
        }

        verify(mockImageView).draw(canvas)
        verify(mockImageGradient).draw(canvas)
        verify(mockTopView).draw(canvas)
        verify(mockBottomView).draw(canvas)
        verify(mockContentView).draw(canvas)
        verify(mockBorderDrawer).draw(canvas)
    }

    @Test
    @Parameters(value = ["TOP", "BOTTOM"])
    fun `When view is measured, and image alignment is TOP or BOTTOM, then contents are measured accordingly`(
        alignment: SbisTileViewImageAlignment
    ) {
        val availableHeight = 30
        val expectedImageWidth = IMAGE_SIZE
        val expectedImageHeight = 13
        val mockTopView = mock<View>()
        val mockBottomView = mock<View>()
        val mockContentView = mock<View>()

        with(controller) {
            setImageModel(
                SbisTileViewImageModel(
                    TileViewImageUrl("Url"),
                    alignment,
                    Rectangle(SbisTileViewImageRatio.THREE_TO_FOUR)
                )
            )
            setTopView(mockTopView)
            setBottomView(mockBottomView)
            setContentView(mockContentView)
            performMeasure(20, availableHeight)
        }

        assertEquals(IMAGE_SIZE, controller.width)
        assertEquals(availableHeight, controller.height)
        verify(mockImageView).setSize(expectedImageWidth, expectedImageHeight)
        verify(mockTopView).measure(expectedImageWidth, expectedImageHeight / 2)
        verify(mockBottomView).measure(expectedImageWidth, expectedImageHeight / 2)
        verify(mockContentView).measure(IMAGE_SIZE, 17)
        verify(mockImageLoader).onViewMeasured()
    }

    @Test
    @Parameters(value = ["LEFT", "RIGHT"])
    fun `When view is measured, and image alignment is LEFT or RIGHT, then contents are measured accordingly`(
        alignment: SbisTileViewImageAlignment
    ) {
        val availableWidth = 30
        val availableHeight = 20
        val expectedImageWidth = 8
        val expectedImageHeight = IMAGE_SIZE
        val expectedContentWidth = 22
        val mockTopView = mock<View>()
        val mockBottomView = mock<View>()
        val mockContentView = mock<View>()

        with(controller) {
            setImageModel(
                SbisTileViewImageModel(
                    TileViewImageUrl("Url"),
                    alignment,
                    Rectangle(SbisTileViewImageRatio.THREE_TO_FOUR)
                )
            )
            setTopView(mockTopView)
            setBottomView(mockBottomView)
            setContentView(mockContentView)
            performMeasure(availableWidth, availableHeight)
        }

        assertEquals(availableWidth, controller.width)
        assertEquals(IMAGE_SIZE, controller.height)
        verify(mockImageView).setSize(expectedImageWidth, expectedImageHeight)
        verify(mockTopView).measure(expectedImageWidth, expectedImageHeight / 2)
        verify(mockBottomView).measure(expectedImageWidth, expectedImageHeight / 2)
        verify(mockContentView).measure(expectedContentWidth, IMAGE_SIZE)
        verify(mockImageLoader).onViewMeasured()
    }

    @Test
    fun `When view is measured, and image alignment is FILL, then contents are measured accordingly`() {
        val expectedImageWidth = IMAGE_SIZE
        val expectedImageHeight = IMAGE_SIZE
        val mockTopView = mock<View>()
        val mockBottomView = mock<View>()
        val mockContentView = mock<View>()

        with(controller) {
            setImageModel(
                SbisTileViewImageModel(
                    TileViewImageUrl("Url"),
                    SbisTileViewImageAlignment.FILL,
                    Rectangle(SbisTileViewImageRatio.THREE_TO_FOUR)
                )
            )
            setTopView(mockTopView)
            setBottomView(mockBottomView)
            setContentView(mockContentView)
            performMeasure(30, 20)
        }

        assertEquals(IMAGE_SIZE, controller.width)
        assertEquals(IMAGE_SIZE, controller.height)
        verify(mockImageView).setSize(IMAGE_SIZE, IMAGE_SIZE)
        verify(mockTopView).measure(expectedImageWidth, expectedImageHeight / 2)
        verify(mockBottomView).measure(expectedImageWidth, expectedImageHeight / 2)
        verify(mockContentView).measure(IMAGE_SIZE, IMAGE_SIZE)
        verify(mockImageLoader).onViewMeasured()
    }

}
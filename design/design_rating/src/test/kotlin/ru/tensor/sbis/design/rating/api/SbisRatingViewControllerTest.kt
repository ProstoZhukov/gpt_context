package ru.tensor.sbis.design.rating.api

import android.content.Context
import android.graphics.Color
import androidx.core.view.ViewCompat
import io.mockk.every
import io.mockk.invoke
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.unmockkAll
import io.mockk.verify
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import ru.tensor.sbis.design.TypefaceManager
import ru.tensor.sbis.design.custom_view_tools.TextLayout
import ru.tensor.sbis.design.custom_view_tools.TextLayoutConfig
import ru.tensor.sbis.design.custom_view_tools.utils.safeRequestLayout
import ru.tensor.sbis.design.rating.SbisRatingView
import ru.tensor.sbis.design.rating.model.SbisRatingColorsMode
import ru.tensor.sbis.design.rating.model.SbisRatingFilledMode
import ru.tensor.sbis.design.rating.model.SbisRatingIconType
import ru.tensor.sbis.design.rating.model.SbisRatingPrecision
import ru.tensor.sbis.design.rating.type.RatingIconProvider
import ru.tensor.sbis.design.rating.type.icons.DefaultRatingIconProvider
import ru.tensor.sbis.design.rating.type.icons.DefaultRatingIconTypeController
import ru.tensor.sbis.design.rating.type.smiles.SmilesRatingIconProvider
import ru.tensor.sbis.design.rating.type.smiles.SmilesRatingIconTypeController
import ru.tensor.sbis.design.rating.type.thumbs.ThumbsRatingIconProvider
import ru.tensor.sbis.design.rating.type.thumbs.ThumbsRatingIconTypeController
import ru.tensor.sbis.design.rating.utils.RatingAccessibilityDelegate
import ru.tensor.sbis.design.rating.utils.RatingStyleHolder
import ru.tensor.sbis.design.rating.utils.RatingTextLayoutFactory
import ru.tensor.sbis.design.theme.global_variables.IconSize
import ru.tensor.sbis.design.theme.global_variables.Offset
import kotlin.math.roundToInt

/**
 * Тестовый класс для [SbisRatingViewController].
 *
 * @author ps.smirnyh
 */
@RunWith(MockitoJUnitRunner.StrictStubs::class)
class SbisRatingViewControllerTest {

    private val fakeIconColors = listOf(
        0xC2140A,
        0xE86A15,
        0xFEC63F,
        0x8DC742,
        0x298E3C
    )

    private val fakeOtherIconColors = listOf(
        Color.RED,
        Color.YELLOW,
        Color.GREEN
    )

    private val goodSmileIcon
        get() = SbisRatingIconType.SMILES.emptyIcons[2]

    private val neutralSmileIcon
        get() = SbisRatingIconType.SMILES.emptyIcons[1]

    private val badSmileIcon
        get() = SbisRatingIconType.SMILES.emptyIcons[0]

    private val goodThumbIcon
        get() = SbisRatingIconType.THUMBS.emptyIcons[1]

    private val badThumbIcon
        get() = SbisRatingIconType.THUMBS.emptyIcons[0]

    private val mockStyleHolder: RatingStyleHolder = mockk(relaxed = true) {
        every { iconColors } returns fakeIconColors
        every { otherIconColors } returns fakeOtherIconColors
    }
    private val mockContext: Context = mockk()
    private val mockIconFactory: RatingTextLayoutFactory = mockk {
        every { create(any()) } answers { getTextLayoutMock() }
    }
    private lateinit var controller: SbisRatingViewController
    private lateinit var iconProvider: RatingIconProvider
    private lateinit var mockView: SbisRatingView

    @Before
    fun setUp() {
        val mockAccessibilityDelegate: RatingAccessibilityDelegate = mockk {
            justRun { layoutSet = any() }
        }
        mockkObject(IconSize.X7L)
        mockkObject(Offset.XS)
        mockkObject(Offset.ST)
        mockkObject(Offset.M)
        every { IconSize.X7L.getDimenPx(any()) } returns 42
        every { Offset.XS.getDimenPx(any()) } returns 6
        every { Offset.ST.getDimenPx(any()) } returns 10
        every { Offset.M.getDimenPx(any()) } returns 12
        mockkObject(SbisRatingIconType.STARS)
        mockkObject(SbisRatingIconType.HEARTS)
        mockkStatic(TypefaceManager::getSbisMobileIconTypeface)
        every { TypefaceManager.getSbisMobileIconTypeface(any()) } returns mockk()
        mockkStatic(ViewCompat::setAccessibilityDelegate)
        justRun { ViewCompat.setAccessibilityDelegate(any(), any()) }
        mockView = mockk(relaxed = true) {
            val enabledSlot = slot<Boolean>()
            justRun { isEnabled = capture(enabledSlot) }
            every { context } returns mockContext
            every { isEnabled } answers { enabledSlot.captured }
        }
        mockView.isEnabled = true
        controller = SbisRatingViewController(mockStyleHolder)
        iconProvider = DefaultRatingIconProvider(controller, mockIconFactory)
        every { SbisRatingIconType.STARS.getIconTypeController(controller) } answers {
            DefaultRatingIconTypeController(controller, iconProvider)
        }
        every { SbisRatingIconType.HEARTS.getIconTypeController(controller) } answers {
            DefaultRatingIconTypeController(controller, iconProvider)
        }
        controller.attach(mockView, null, 0, 0, mockAccessibilityDelegate)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `When set new rating value then value is equal to passed value and view is updated`() {
        controller.value = 2.0
        verify(exactly = 1) { mockView.safeRequestLayout() }
        assertEquals(2.0, controller.value, 0.1)
        controller.icons.forEachIndexed { index, textLayout ->
            if (index < controller.value) {
                textLayout.assertFilledIcon(controller.iconType)
            } else {
                textLayout.assertEmptyIcon(controller.iconType)
            }
        }
    }

    @Test(expected = IllegalArgumentException::class)
    fun `When set value less allowed range then throw exception`() {
        controller.value = -1.0
    }

    @Test(expected = IllegalArgumentException::class)
    fun `When set value more allowed range then throw exception`() {
        controller.value = controller.maxValue + 1.0
    }

    @Test
    fun `When set new value equal to old value rating then do nothing`() {
        controller.value = SBIS_RATING_MIN_RATING
        verify(exactly = 0) { mockView.safeRequestLayout() }
    }

    @Test
    fun `When set new value maxValue then number icons is equal to passed value and view is updated`() {
        controller.maxValue = 4
        verify(exactly = 1) { mockView.safeRequestLayout() }
        assertEquals(4, controller.icons.size)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `When set new value maxValue less allowed range then throw exception`() {
        controller.maxValue = SBIS_RATING_MIN_ICON_COUNT.toInt() - 1
    }

    @Test(expected = IllegalArgumentException::class)
    fun `When set new value maxValue more allowed range then throw exception`() {
        controller.maxValue = SBIS_RATING_MAX_ICON_COUNT.toInt() + 1
    }

    @Test
    fun `When set new value iconSize then size all icons must be equal to new value`() {
        mockkObject(IconSize.X4L)
        mockkObject(Offset.S)
        every { IconSize.X4L.getDimenPx(any()) } returns 36
        every { Offset.S.getDimenPx(any()) } returns 8
        controller.iconSize = IconSize.X4L
        controller.icons.forEach {
            assertEquals(IconSize.X4L.getDimenPx(mockContext).toFloat(), it.textPaint.textSize)
        }
        verify(exactly = 1) { mockView.safeRequestLayout() }
    }

    @Test
    fun `When set new value iconSize X7L then icons should have padding`() {
        controller.iconSize = IconSize.X7L
        controller.icons.forEach {
            it.assertPaddings(Offset.XS.getDimenPx(mockContext))
        }
    }

    @Test
    fun `When set new value not equal iconSize X7L then icons should not have padding`() {
        mockkObject(IconSize.X4L)
        mockkObject(Offset.S)
        every { IconSize.X4L.getDimenPx(any()) } returns 36
        every { Offset.S.getDimenPx(any()) } returns 8
        controller.iconSize = IconSize.X4L
        controller.icons.forEach {
            it.assertPaddings(0)
        }
    }

    @Test
    fun `When set new value iconType then icons is equal to passed value and view is updated`() {
        controller.iconType = SbisRatingIconType.HEARTS
        controller.icons.forEach {
            it.assertEmptyIcon(SbisRatingIconType.HEARTS)
        }
        verify(exactly = 1) { mockView.safeRequestLayout() }
    }

    @Test
    fun `When set colorsMode STATIC then all icons must be have same color`() {
        controller.colorsMode = SbisRatingColorsMode.STATIC
        controller.value = 5.0
        controller.icons.forEach {
            it.assertEmptyIconColor()
        }
    }

    @Test
    fun `When set colorsMode DYNAMIC then all icons must be have color depending on rating`() {
        controller.colorsMode = SbisRatingColorsMode.DYNAMIC
        controller.value = 5.0
        controller.icons.forEach {
            it.assertFilledIconColor()
        }
        controller.value = 2.0
        controller.icons.forEachIndexed { index, textLayout ->
            if (index < controller.value) {
                textLayout.assertFilledIconColor()
            } else {
                textLayout.assertEmptyIconColor()
            }
        }
    }

    @Test
    fun `When set filled mode is BORDERED then empty icons must be have unpainted icons`() {
        controller.emptyIconFilledMode = SbisRatingFilledMode.BORDERED
        controller.value = 0.0
        controller.icons.forEach {
            it.assertEmptyIcon(controller.iconType)
        }
    }

    @Test
    fun `When set filled mode is FILLED then empty icons must be have colored icons`() {
        controller.emptyIconFilledMode = SbisRatingFilledMode.FILLED
        controller.value = 0.0
        controller.icons.forEach {
            it.assertFilledIcon(controller.iconType)
        }
    }

    @Test
    fun `When set precision is HALF then empty icons must be have unpainted icons`() {
        controller.precision = SbisRatingPrecision.HALF
        controller.value = 0.0
        controller.icons.forEach {
            it.assertEmptyIcon(controller.iconType)
        }
    }

    @Test
    fun `When set precision is HALF then filled icons must be have colored icons`() {
        controller.precision = SbisRatingPrecision.HALF
        controller.value = 5.0
        controller.icons.forEach {
            it.assertFilledIcon(controller.iconType)
        }
    }

    @Test
    fun `When set precision is HALF then half icon must be have half colored icon`() {
        controller.precision = SbisRatingPrecision.HALF
        controller.value = 1.5
        controller.icons.forEachIndexed { index, textLayout ->
            when {
                index < controller.value.roundToInt() - 1 -> {
                    textLayout.assertFilledIcon(controller.iconType)
                    textLayout.assertFilledIconColor()
                }

                index > controller.value.roundToInt() - 1 -> {
                    textLayout.assertEmptyIcon(controller.iconType)
                    textLayout.assertEmptyIconColor()
                }

                else -> {
                    textLayout.assertHalfIcon(controller.iconType)
                    textLayout.assertFilledIconColor()
                }
            }
        }
    }

    @Test
    fun `When set readOnly is false then clickListener must be work on clicked`() {
        controller.readOnly = true
        controller.readOnly = false
        val onRatingSelectedMock = mockk<(Double) -> Unit>()
        justRun { onRatingSelectedMock(any()) }
        controller.onRatingSelected = onRatingSelectedMock
        val oldRating = controller.value

        val touchResult = controller.icons[2].onTouch(mockView, mockk())

        assertEquals(true, touchResult)
        assertNotEquals(oldRating, controller.value, 0.1)
        assertEquals(3.0, controller.value, 0.1)
        verify(exactly = 1) { onRatingSelectedMock.invoke(any()) }
        controller.icons.forEachIndexed { index, textLayout ->
            if (index < controller.value) {
                textLayout.assertFilledIcon(controller.iconType)
                textLayout.assertFilledIconColor()
            } else {
                textLayout.assertEmptyIcon(controller.iconType)
                textLayout.assertEmptyIconColor()
            }
        }
    }

    @Test
    fun `When set allowUserToResetRating is true then click on icon again reset rating value`() {
        controller.allowUserToResetRating = true
        val onRatingSelectedMock = mockk<(Double) -> Unit>()
        justRun { onRatingSelectedMock(any()) }
        controller.onRatingSelected = onRatingSelectedMock

        controller.icons[3].onTouch(mockView, mockk())
        assertEquals(4.0, controller.value, 0.1)
        controller.icons[3].onTouch(mockView, mockk())
        assertEquals(SBIS_RATING_MIN_RATING, controller.value, 0.1)
        verify(exactly = 2) { onRatingSelectedMock.invoke(any()) }
    }

    @Test
    fun `When set allowUserToResetRating is false then click on icon again do nothing`() {
        controller.allowUserToResetRating = false
        val onRatingSelectedMock = mockk<(Double) -> Unit>()
        justRun { onRatingSelectedMock(any()) }
        controller.onRatingSelected = onRatingSelectedMock

        controller.icons[3].onTouch(mockView, mockk())
        assertEquals(4.0, controller.value, 0.1)
        controller.icons[3].onTouch(mockView, mockk())
        assertEquals(4.0, controller.value, 0.1)
        verify(exactly = 1) { onRatingSelectedMock.invoke(any()) }
    }

    @Test
    fun `When set readOnly is true then clickListener does not work on clicked`() {
        controller.readOnly = true
        val onRatingSelectedMock = mockk<(Double) -> Unit>()
        justRun { onRatingSelectedMock(any()) }
        controller.onRatingSelected = onRatingSelectedMock
        val oldRating = controller.value

        val touchResult = controller.icons[3].onTouch(mockView, mockk())

        assertEquals(false, touchResult)
        assertEquals(oldRating, controller.value, 0.1)
        verify(exactly = 0) { onRatingSelectedMock.invoke(any()) }

    }

    @Test
    fun `When set iconType SMILES and value is four then icon and color are good`() {
        mockkObject(SbisRatingIconType.SMILES)
        iconProvider = SmilesRatingIconProvider(controller, mockIconFactory)
        every { SbisRatingIconType.SMILES.getIconTypeController(controller) } answers {
            SmilesRatingIconTypeController(controller, iconProvider)
        }

        controller.iconType = SbisRatingIconType.SMILES
        controller.value = 4.0

        assertEquals(
            goodSmileIcon,
            controller.icons.singleOrNull { it.textPaint.color == fakeOtherIconColors[2] }?.text
        )
    }

    @Test
    fun `When set iconType SMILES and value is three then icon and color are neutral`() {
        mockkObject(SbisRatingIconType.SMILES)
        iconProvider = SmilesRatingIconProvider(controller, mockIconFactory)
        every { SbisRatingIconType.SMILES.getIconTypeController(controller) } answers {
            SmilesRatingIconTypeController(controller, iconProvider)
        }

        controller.iconType = SbisRatingIconType.SMILES
        controller.value = 3.0

        assertEquals(
            neutralSmileIcon,
            controller.icons.singleOrNull { it.textPaint.color == fakeOtherIconColors[1] }?.text
        )
    }

    @Test
    fun `When set iconType SMILES and value is two then icon and color are bad`() {
        mockkObject(SbisRatingIconType.SMILES)
        iconProvider = SmilesRatingIconProvider(controller, mockIconFactory)
        every { SbisRatingIconType.SMILES.getIconTypeController(controller) } answers {
            SmilesRatingIconTypeController(controller, iconProvider)
        }

        controller.iconType = SbisRatingIconType.SMILES
        controller.value = 2.0

        assertEquals(
            badSmileIcon,
            controller.icons.singleOrNull { it.textPaint.color == fakeOtherIconColors[0] }?.text
        )
    }

    @Test
    fun `When set iconType THUMBS and value is two then icon and color are bad`() {
        mockkObject(SbisRatingIconType.THUMBS)
        iconProvider = ThumbsRatingIconProvider(controller, mockIconFactory)
        every { SbisRatingIconType.THUMBS.getIconTypeController(controller) } answers {
            ThumbsRatingIconTypeController(controller, iconProvider)
        }

        controller.iconType = SbisRatingIconType.THUMBS
        controller.value = 2.0

        assertEquals(
            badThumbIcon,
            controller.icons.singleOrNull { it.textPaint.color == fakeOtherIconColors[0] }?.text
        )
    }

    @Test
    fun `When set iconType THUMBS and value is four then icon and color are good`() {
        mockkObject(SbisRatingIconType.THUMBS)
        iconProvider = ThumbsRatingIconProvider(controller, mockIconFactory)
        every { SbisRatingIconType.THUMBS.getIconTypeController(controller) } answers {
            ThumbsRatingIconTypeController(controller, iconProvider)
        }

        controller.iconType = SbisRatingIconType.THUMBS
        controller.value = 4.0

        assertEquals(
            goodThumbIcon,
            controller.icons.singleOrNull { it.textPaint.color == fakeOtherIconColors[2] }?.text
        )
    }

    private fun getTextLayoutMock() =
        mockk<TextLayout>(relaxed = true) {
            val clickListenerSlot = slot<TextLayout.OnClickListener>()
            val textSizeSlot = slot<Float>()
            val textColorSlot = slot<Int>()
            val params = TextLayout.TextLayoutParams(
                paint = mockk {
                    justRun { color = capture(textColorSlot) }
                    justRun { textSize = capture(textSizeSlot) }
                }
            )
            val configSlot = slot<TextLayoutConfig>()
            justRun { setOnClickListener(capture(clickListenerSlot)) }
            every { configure(capture(configSlot)) } answers {
                configSlot.invoke(params)
                false
            }
            every { text } answers {
                params.text
            }
            every { textPaint.textSize } answers {
                textSizeSlot.captured
            }
            every { textPaint.color } answers {
                textColorSlot.captured
            }
            every { paddingStart } answers { params.padding.start }
            every { paddingTop } answers { params.padding.top }
            every { paddingEnd } answers { params.padding.end }
            every { paddingBottom } answers { params.padding.bottom }
            every { onTouch(any(), any()) } answers {
                if (mockView.isEnabled) {
                    clickListenerSlot.captured.onClick(mockContext, this@mockk)
                    true
                } else {
                    false
                }
            }
        }

    private fun TextLayout.assertPaddings(expected: Int) {
        assertEquals(expected, paddingStart)
        assertEquals(expected, paddingTop)
        assertEquals(expected, paddingEnd)
        assertEquals(expected, paddingBottom)
    }


    private fun TextLayout.assertEmptyIcon(iconType: SbisRatingIconType) =
        assertEquals(
            iconType.emptyIcons.first(),
            text
        )

    private fun TextLayout.assertFilledIcon(iconType: SbisRatingIconType) =
        assertEquals(
            iconType.filledIcons.first(),
            text
        )

    private fun TextLayout.assertHalfIcon(iconType: SbisRatingIconType) =
        assertEquals(
            iconType.halfFilledIcons.first(),
            text
        )

    private fun TextLayout.assertEmptyIconColor() =
        assertEquals(
            (iconProvider as? DefaultRatingIconProvider)?.emptyIconColor,
            textPaint.color
        )

    private fun TextLayout.assertFilledIconColor() =
        assertEquals(
            (iconProvider as? DefaultRatingIconProvider)?.filledIconColor,
            textPaint.color
        )
}
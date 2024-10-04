package ru.tensor.sbis.design

import android.app.Activity
import android.graphics.Color
import android.graphics.RectF
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.DisplayName
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.drawables.BaseBackgroundDrawable
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.drawables.models.BoundaryDrawableType
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.drawables.style_holder.BackgroundDrawableStyleHolder

/**
 * Тестирование [BaseBackgroundDrawable].
 *
 * @author mb.kruglova
 */
@RunWith(AndroidJUnit4::class)
class BaseBackgroundDrawableTest {

    private val activity = Robolectric.buildActivity(Activity::class.java).setup().get()
    private val borderThickness = 4F
    private val halfBorderThickness = borderThickness / 2.0F
    private val defaultValue = 0F
    private lateinit var backgroundDrawable: BaseBackgroundDrawable
    private lateinit var styleHolder: BackgroundDrawableStyleHolder

    @Before
    fun setUp() {
        activity.theme.applyStyle(R.style.BaseAppTheme, false)
        styleHolder = BackgroundDrawableStyleHolder(
            cornerColor = Color.BLACK,
            borderColor = Color.GRAY,
            backgroundColor = Color.WHITE,
            borderThickness = borderThickness.toInt(),
            cornerRadius = 32F,
            compactCornerRadius = 32F,
            borderRadius = 62F
        )
        backgroundDrawable = object : BaseBackgroundDrawable(
            activity,
            Color.MAGENTA,
            styleHolder
        ) {

            override var cornerRadius: Float = styleHolder.compactCornerRadius
            override var radius: Float = (bounds.bottom - bounds.top) / 2F
        }
    }

    @Test
    @DisplayName(
        "When drawable type is VERTICAL_ROUNDING and position is left " +
            "then border rect has rounded bottom right and left corners"
    )
    fun getBorderRectForLeftVerticalRounding() {
        performTest(
            BoundaryDrawableType.VERTICAL_ROUNDING,
            true,
            expectedLeft = halfBorderThickness,
            expectedTop = -borderThickness,
            expectedRight = -halfBorderThickness,
            expectedBottom = -halfBorderThickness
        )
    }

    @Test
    @DisplayName(
        "When drawable type is VERTICAL_ROUNDING and position is right " +
            "then border rect has rounded top right and left corners"
    )
    fun getBorderRectForRightVerticalRounding() {
        performTest(
            BoundaryDrawableType.VERTICAL_ROUNDING,
            false,
            expectedLeft = halfBorderThickness,
            expectedTop = halfBorderThickness,
            expectedRight = -halfBorderThickness,
            expectedBottom = borderThickness
        )
    }

    @Test
    @DisplayName(
        "When drawable type is HORIZONTAL_ROUNDING and position is left " +
            "then border rect has rounded left top and bottom corners"
    )
    fun getBorderRectForLeftHorizontalRounding() {
        performTest(
            BoundaryDrawableType.HORIZONTAL_ROUNDING,
            true,
            expectedLeft = halfBorderThickness,
            expectedTop = halfBorderThickness,
            expectedRight = borderThickness,
            expectedBottom = -halfBorderThickness
        )
    }

    @Test
    @DisplayName(
        "When drawable type is HORIZONTAL_ROUNDING and position is right " +
            "then border rect has rounded right top and bottom corners"
    )
    fun getBorderRectForRightHorizontalRounding() {
        performTest(
            BoundaryDrawableType.HORIZONTAL_ROUNDING,
            false,
            expectedLeft = -borderThickness,
            expectedTop = halfBorderThickness,
            expectedRight = -halfBorderThickness,
            expectedBottom = -halfBorderThickness
        )
    }

    @Test
    @DisplayName(
        "When drawable type is TOP_ROUNDING and position is left " +
            "then border rect has rounded top left corner"
    )
    fun getBorderRectForLeftTopRounding() {
        performTest(
            BoundaryDrawableType.TOP_ROUNDING,
            true,
            expectedLeft = halfBorderThickness,
            expectedTop = halfBorderThickness,
            expectedRight = borderThickness,
            expectedBottom = borderThickness
        )
    }

    @Test
    @DisplayName(
        "When drawable type is TOP_ROUNDING and position is right " +
            "then border rect has rounded top right corner"
    )
    fun getBorderRectForRightTopRounding() {
        performTest(
            BoundaryDrawableType.TOP_ROUNDING,
            false,
            expectedLeft = -borderThickness,
            expectedTop = halfBorderThickness,
            expectedRight = -halfBorderThickness,
            expectedBottom = borderThickness
        )
    }

    @Test
    @DisplayName(
        "When drawable type is BOTTOM_ROUNDING and position is left " +
            "then border rect has rounded bottom left corner"
    )
    fun getBorderRectForLeftBottomRounding() {
        performTest(
            BoundaryDrawableType.BOTTOM_ROUNDING,
            true,
            expectedLeft = halfBorderThickness,
            expectedTop = -borderThickness,
            expectedRight = borderThickness,
            expectedBottom = -halfBorderThickness
        )
    }

    @Test
    @DisplayName(
        "When drawable type is BOTTOM_ROUNDING and position is right " +
            "then border rect has rounded bottom right corner"
    )
    fun getBorderRectForRightBottomRounding() {
        performTest(
            BoundaryDrawableType.BOTTOM_ROUNDING,
            false,
            expectedLeft = -borderThickness,
            expectedTop = -borderThickness,
            expectedRight = -halfBorderThickness,
            expectedBottom = -halfBorderThickness
        )
    }

    @Test
    fun `When drawable type is BORDER then border rect doesn't have rounded corners`() {
        performTestForBorder(true)

        performTestForBorder(false)
    }

    private fun performTestForBorder(isLeft: Boolean) {
        performTest(
            BoundaryDrawableType.BORDER,
            isLeft,
            expectedLeft = defaultValue,
            expectedTop = defaultValue,
            expectedRight = defaultValue,
            expectedBottom = defaultValue
        )
    }

    private fun performTest(
        type: BoundaryDrawableType,
        isLeft: Boolean,
        expectedLeft: Float,
        expectedTop: Float,
        expectedRight: Float,
        expectedBottom: Float
    ) {
        performAssert(
            rect = if (isLeft) getLeftRect(type) else getRightRect(type),
            left = expectedLeft,
            top = expectedTop,
            right = expectedRight,
            bottom = expectedBottom
        )
    }

    private fun performAssert(rect: RectF, left: Float, top: Float, right: Float, bottom: Float) {
        assertEquals(rect.left, left)
        assertEquals(rect.top, top)
        assertEquals(rect.right, right)
        assertEquals(rect.bottom, bottom)
    }

    private fun getLeftRect(type: BoundaryDrawableType) = backgroundDrawable.getBorderRect(type, true)
    private fun getRightRect(type: BoundaryDrawableType) = backgroundDrawable.getBorderRect(type, false)
}
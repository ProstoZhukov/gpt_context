package ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.drawables

import android.content.Context
import android.graphics.Rect
import androidx.annotation.ColorInt

/**
 * Drawable для отрисовки фона дня в календаре.
 *
 * @author mb.kruglova
 */
internal class DayBackgroundDrawable @JvmOverloads constructor(
    val context: Context,
    @ColorInt private val customColor: Int? = null
) : BaseBackgroundDrawable(context, customColor) {

    override var cornerRadius: Float = 0F
    override var radius: Float = 0f

    override fun onBoundsChange(bounds: Rect) {
        cornerRadius = styleHolder.compactCornerRadius
        radius = (bounds.bottom - bounds.top) / 2F

        super.onBoundsChange(bounds)
    }
}
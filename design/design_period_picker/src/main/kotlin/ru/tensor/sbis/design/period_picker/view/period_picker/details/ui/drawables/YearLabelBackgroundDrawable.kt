package ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.drawables

import android.content.Context
import android.graphics.Rect

/**
 * Drawable для отрисовки фона года в шапке календаря.
 *
 * @author mb.kruglova
 */
internal
class YearLabelBackgroundDrawable @JvmOverloads constructor(
    val context: Context
) : BaseBackgroundDrawable(context) {

    override var cornerRadius: Float = 0F
    override var radius: Float = 0f

    override fun onBoundsChange(bounds: Rect) {
        cornerRadius = styleHolder.cornerRadius
        radius = (bounds.bottom - bounds.top) / 2F

        super.onBoundsChange(bounds)
    }
}
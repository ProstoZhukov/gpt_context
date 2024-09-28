package ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.drawables

import android.content.Context
import android.graphics.Rect

/**
 * Drawable для отрисовки фона квантов в календаре.
 *
 * @author mb.kruglova
 */
internal class QuantumBackgroundDrawable @JvmOverloads constructor(
    val context: Context
) : BaseBackgroundDrawable(context) {

    override var cornerRadius: Float = 0F
    override var radius: Float = 0f

    override fun onBoundsChange(bounds: Rect) {
        cornerRadius = styleHolder.cornerRadius
        radius = styleHolder.borderRadius

        super.onBoundsChange(bounds)
    }
}
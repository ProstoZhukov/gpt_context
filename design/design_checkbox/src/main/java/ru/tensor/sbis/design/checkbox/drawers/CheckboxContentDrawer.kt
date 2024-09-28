package ru.tensor.sbis.design.checkbox.drawers

import android.graphics.Canvas
import androidx.annotation.ColorInt
import androidx.annotation.Dimension

/**
 * Интерфейс объекта для рисования контента для чекбокса
 *
 * @author mb.kruglova
 */
internal interface CheckboxContentDrawer {
    @get:Dimension
    val width: Float

    @get:Dimension
    val height: Float

    fun setTint(@ColorInt color: Int)

    fun draw(canvas: Canvas)
}
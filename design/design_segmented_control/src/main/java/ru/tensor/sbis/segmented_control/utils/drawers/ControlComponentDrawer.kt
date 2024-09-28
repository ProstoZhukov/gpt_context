package ru.tensor.sbis.segmented_control.utils.drawers

import android.graphics.Canvas
import androidx.annotation.ColorInt
import androidx.annotation.Dimension

/**
 * Интерфейс объекта для рисования элементов сегмент-контрола. Реализации должны быть легковесными и
 * поддерживать простое сравнивание, чтобы избегать лишних обновлений
 *
 * @see ControlTextComponentDrawer
 *
 * @author ps.smirnyh
 */
internal interface ControlComponentDrawer {

    /**
     * Отметка о том, нужно ли рисовать компонент в методе [draw]. При этом компонент продолжает
     * участвовать в вычислениях размера, занимать пространство
     */
    var isVisible: Boolean

    @get:Dimension
    val width: Float

    @get:Dimension
    val height: Float

    fun setTint(@ColorInt color: Int): Boolean

    fun draw(canvas: Canvas)
}
package ru.tensor.sbis.design.buttons.base.utils.drawers

import android.graphics.Canvas
import androidx.annotation.ColorInt
import androidx.annotation.Dimension
import ru.tensor.sbis.design.buttons.base.api.AbstractSbisButtonController
import ru.tensor.sbis.design.buttons.base.models.state.SbisButtonState

/**
 * Интерфейс объекта для рисования элементов кнопки. Реализации должны быть легковесными и
 * поддерживать простое сравнивание, чтобы избегать лишних обновлений.
 *
 * @see AbstractSbisButtonController.updateIconDrawer
 * @see ButtonTextComponentDrawer
 *
 * @author ma.kolpakov
 */
internal interface ButtonComponentDrawer {

    /**
     * Отметка о том, нужно ли рисовать компонент в методе [draw]. При этом компонент продолжает
     * участвовать в вычислениях размера, занимать пространство.
     */
    var isVisible: Boolean

    @get:Dimension
    val width: Float

    @get:Dimension
    val height: Float

    fun setTint(@ColorInt color: Int): Boolean

    fun draw(canvas: Canvas)

    fun changeState(state: SbisButtonState) = Unit
}

internal fun ButtonComponentDrawer.updateVisibilityByState(state: SbisButtonState) {
    isVisible = state != SbisButtonState.IN_PROGRESS
}

internal fun ButtonComponentDrawer.updateVisivilityProgressByState(state: SbisButtonState) {
    isVisible = state == SbisButtonState.IN_PROGRESS
}
package ru.tensor.sbis.design.buttons.button.utils

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import androidx.annotation.ColorInt
import ru.tensor.sbis.design.buttons.base.models.state.SbisButtonState

/**
 * Реализация фона, которая позволяет перекючаться между градиентным и залитым состояниями.
 *
 * @author ma.kolpakov
 */
internal class ButtonBackgroundDrawable : GradientDrawable() {

    private var solidColors: ColorStateList? = null

    /**
     * "Стартовый" цвет градиента. Значение сбрасывается при установке залитых цветов [setColor].
     */
    @ColorInt
    var gradientColor = UNDEFINED_COLOR

    /**
     * Состояние фона кнопки.
     */
    var buttonState = SbisButtonState.ENABLED
        set(value) {
            if (field == value) return
            if (field == SbisButtonState.IN_PROGRESS && value == SbisButtonState.DISABLED) {
                val stateSet = intArrayOf(-android.R.attr.state_enabled)
                field = value
                onStateChange(stateSet)
            } else if (field == SbisButtonState.DISABLED && value == SbisButtonState.IN_PROGRESS) {
                val stateSet = intArrayOf()
                field = value
                onStateChange(stateSet)
            } else {
                field = value
            }
        }

    override fun setColor(colorStateList: ColorStateList?) {
        super.setColor(colorStateList)
        solidColors = colorStateList
        gradientColor = UNDEFINED_COLOR
    }

    override fun isStateful(): Boolean = true

    override fun onStateChange(stateSet: IntArray): Boolean {
        /*
        На уровне фона применяется корректировака состояний для того, чтобы состояние прогресса
        не меняло внешний вид на выключенное состояение. Особенность прогесса в том, что состоянеие
        ведёт себя как выключенное, но выглядит как включенное
         */
        val filteredState = if (buttonState == SbisButtonState.IN_PROGRESS) {
            stateSet.plus(android.R.attr.state_enabled)
        } else {
            stateSet
        }
        // Сборка и установка градиента по состояниям
        solidColors?.let { solid ->
            if (gradientColor != UNDEFINED_COLOR) {
                val selectedColor = solid.getColorForState(filteredState, solid.defaultColor)
                // градиент применяется только к цвету по умолчанию
                colors = intArrayOf(
                    if (selectedColor == solid.defaultColor) gradientColor else selectedColor,
                    selectedColor
                )
            }
        }
        return super.onStateChange(filteredState)
    }

    private companion object {
        const val UNDEFINED_COLOR = Color.MAGENTA
    }
}
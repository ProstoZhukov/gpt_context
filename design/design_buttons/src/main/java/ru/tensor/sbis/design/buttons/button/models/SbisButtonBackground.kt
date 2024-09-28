package ru.tensor.sbis.design.buttons.button.models

import androidx.annotation.FloatRange
import ru.tensor.sbis.design.theme.Direction

/**
 * @author ma.kolpakov
 */
sealed class SbisButtonBackground(
    @FloatRange(from = 0.0, to = 1.0) internal val buttonAlpha: Float = 1.0F,
    @FloatRange(from = 0.0, to = 1.0) internal val buttonPressedAlpha: Float = buttonAlpha
) {

    /**
     * Непрозрачный фон, обводка.
     */
    object Default : SbisButtonBackground()

    /**
     * Непрозрачный фон без обводки.
     */
    object Contrast : SbisButtonBackground()

    /**
     * Прозрачный фон, обводка.
     */
    object BorderOnly : SbisButtonBackground()

    /**
     * Прозрачный фон без обводки.
     */
    object Transparent : SbisButtonBackground()

    /**
     * Градиент (первый цвет берется применяется из обычного состояния, другой из нажатого).
     */
    data class Gradient(
        val direction: Direction
    ) : SbisButtonBackground()

    /**
     * Прозрачный фон без обводки для отображения в группе кнопок.
     */
    object InGroup : SbisButtonBackground(buttonPressedAlpha = 0.6F)
}
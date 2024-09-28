package ru.tensor.sbis.design.buttons.round.model

import ru.tensor.sbis.design.theme.Direction

/**
 * Тип круглой кнопки.
 *
 * @author mb.kruglova
 */
sealed class SbisRoundButtonType {
    /**
     * С заливкой.
     */
    object Filled : SbisRoundButtonType()

    /**
     * Без заливки.
     */
    object Transparent : SbisRoundButtonType()

    /**
     * Градиентная.
     */
    data class Gradient(
        val direction: Direction
    ) : SbisRoundButtonType()
}
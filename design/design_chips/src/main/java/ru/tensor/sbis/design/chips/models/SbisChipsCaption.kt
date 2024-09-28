package ru.tensor.sbis.design.chips.models

import ru.tensor.sbis.design.theme.HorizontalPosition
import ru.tensor.sbis.design.theme.global_variables.FontSize
import ru.tensor.sbis.design.theme.res.SbisColor

/**
 * Модель заголовка.
 *
 * @author ps.smirnyh
 */
data class SbisChipsCaption(

    /** Текст заголовка. */
    var caption: CharSequence,

    /** Позиция заголовка относительно иконки. */
    var position: HorizontalPosition = HorizontalPosition.LEFT,

    /**
     * Кастомный размер заголовка.
     *
     * При значении null будет использоваться стандартный размер заголовка для размера элемента.
     */
    var customSize: FontSize? = null,

    /**
     * Кастомный цвет заголовка.
     *
     * При значении null будет использоваться стандартный цвет заголовка для элемента.
     */
    var customColor: SbisColor? = null
)

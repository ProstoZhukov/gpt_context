package ru.tensor.sbis.design.chips.models

import ru.tensor.sbis.design.theme.global_variables.IconSize
import ru.tensor.sbis.design.theme.res.SbisColor
import ru.tensor.sbis.design.theme.res.PlatformSbisString

/**
 * Модель иконки.
 *
 * @author ps.smirnyh
 */
data class SbisChipsIcon(

    /** Иконка. */
    val icon: PlatformSbisString,

    /**
     * Кастомный размер иконки.
     *
     * При значении null будет использоваться стандартный размер иконки для размера элемента.
     */
    val customSize: IconSize? = null,

    /**
     * Кастомный цвет иконки.
     *
     * При значении null будет использоваться стандартный цвет иконки для элемента.
     */
    val customColor: SbisColor? = null
)
package ru.tensor.sbis.design.rating.utils

import ru.tensor.sbis.design.theme.global_variables.IconSize
import ru.tensor.sbis.design.theme.global_variables.Offset

/**
 * Получить отступ между иконками.
 *
 * @author ps.smirnyh
 */
internal fun IconSize.getOffsetIcon(): Offset? = when (this) {
    IconSize.X2S -> Offset.X3S
    IconSize.XS, IconSize.S, IconSize.ST, IconSize.M -> Offset.X2S
    IconSize.L, IconSize.XL, IconSize.X2L -> Offset.XS
    IconSize.X3L, IconSize.X4L -> Offset.S
    IconSize.X5L, IconSize.X6L -> Offset.M
    IconSize.X7L -> null
}
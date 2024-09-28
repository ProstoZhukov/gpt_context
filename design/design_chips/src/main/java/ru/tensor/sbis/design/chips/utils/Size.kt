/**
 * Утилиты для получения стандартных размеров.
 *
 * @author ps.smirnyh
 */
package ru.tensor.sbis.design.chips.utils

import ru.tensor.sbis.design.theme.global_variables.FontSize
import ru.tensor.sbis.design.theme.global_variables.IconSize
import ru.tensor.sbis.design.theme.global_variables.InlineHeight

/** Получить стандартный размер заголовка. */
internal fun InlineHeight.getDefaultTitleSize(): FontSize = when (this) {
    InlineHeight.X8S, InlineHeight.X7S, InlineHeight.X6S, InlineHeight.X5S -> FontSize.X3S
    InlineHeight.X4S, InlineHeight.X3S, InlineHeight.X2S, InlineHeight.XS -> FontSize.XS
    InlineHeight.S, InlineHeight.M, InlineHeight.L -> FontSize.XL
    InlineHeight.XL, InlineHeight.X2L, InlineHeight.X3L -> FontSize.X2L
}

/** Получить стандартный размер иконки. */
internal fun InlineHeight.getDefaultIconSize(): IconSize = when (this) {
    InlineHeight.X8S, InlineHeight.X7S, InlineHeight.X6S, InlineHeight.X5S -> IconSize.X2S
    InlineHeight.X4S, InlineHeight.X3S -> IconSize.S
    InlineHeight.X2S, InlineHeight.XS -> IconSize.M
    InlineHeight.S, InlineHeight.M, InlineHeight.L -> IconSize.X2L
    InlineHeight.XL, InlineHeight.X2L, InlineHeight.X3L -> IconSize.X7L
}
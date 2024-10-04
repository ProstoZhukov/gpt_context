package ru.tensor.sbis.design.buttons.base.models.icon

import ru.tensor.sbis.design.theme.global_variables.IconSize
import ru.tensor.sbis.design.theme.models.IconSizeModel

/**
 * Размер иконки в кнопке.
 *
 * @author ma.kolpakov
 */
enum class SbisButtonIconSize(
    override val globalVar: IconSize
) : IconSizeModel {

    XS(IconSize.XS),
    S(IconSize.S),
    XL(IconSize.XL),
    X2L(IconSize.X2L),
    X3L(IconSize.X3L),
    X4L(IconSize.X4L),
    X5L(IconSize.X5L),
    X7L(IconSize.X7L)
}

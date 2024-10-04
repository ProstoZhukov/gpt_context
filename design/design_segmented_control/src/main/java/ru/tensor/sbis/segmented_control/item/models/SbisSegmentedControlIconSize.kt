package ru.tensor.sbis.segmented_control.item.models

import ru.tensor.sbis.design.theme.global_variables.IconSize
import ru.tensor.sbis.design.theme.models.IconSizeModel

/**
 * Перечисление возможных размеров иконов в сегмент-контроле.
 *
 * @author ps.smirnyh
 */
enum class SbisSegmentedControlIconSize(
    override val globalVar: IconSize
) : IconSizeModel {

    /** @SelfDocumented */
    M(IconSize.M),

    /** @SelfDocumented */
    XL(IconSize.XL),

    /** @SelfDocumented */
    X2L(IconSize.X2L),

    /** @SelfDocumented */
    X4L(IconSize.X4L),

    /** @SelfDocumented */
    X5L(IconSize.X5L),

    /** @SelfDocumented */
    X7L(IconSize.X7L)
}
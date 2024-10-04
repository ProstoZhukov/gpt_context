package ru.tensor.sbis.segmented_control.item.models

import ru.tensor.sbis.design.theme.global_variables.StyleColor

/**
 * Типы счетчиков сегмент-контрола.
 *
 * @author ps.smirnyh
 */
enum class SbisSegmentedControlCounterStyle(
    internal val styleColor: StyleColor
) {

    /** @SelfDocumented */
    PRIMARY(StyleColor.PRIMARY),

    /** @SelfDocumented */
    SECONDARY(StyleColor.UNACCENTED)
}
package ru.tensor.sbis.segmented_control.item.models

import ru.tensor.sbis.design.theme.global_variables.FontSize

/**
 * Перечисление размеров заголовка в сегмент-контроле.
 *
 * @author ps.smirnyh
 */
enum class SbisSegmentedControlTitleSize(
    internal val titleSize: FontSize
) {

    /** @SelfDocumented */
    S(FontSize.M)
}

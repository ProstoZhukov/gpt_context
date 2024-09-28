package ru.tensor.sbis.segmented_control.control.models

import ru.tensor.sbis.design.theme.global_variables.FontSize
import ru.tensor.sbis.design.theme.global_variables.InlineHeight
import ru.tensor.sbis.design.theme.global_variables.Offset
import ru.tensor.sbis.design.theme.models.InlineHeightModel
import ru.tensor.sbis.segmented_control.item.models.SbisSegmentedControlIconSize
import ru.tensor.sbis.segmented_control.item.models.SbisSegmentedControlTitleSize

/**
 * Размеры сегмент-контрола.
 *
 * @author ps.smirnyh
 */
enum class SbisSegmentedControlSize(
    override val globalVar: InlineHeight,
    internal val iconSize: SbisSegmentedControlIconSize,
    internal val titleSize: FontSize,
    internal val innerSpacing: Offset,
    internal val sideSpacing: Offset
) : InlineHeightModel {

    /** @SelfDocumented */
    XS(
        globalVar = InlineHeight.X4S,
        iconSize = SbisSegmentedControlIconSize.M,
        titleSize = SbisSegmentedControlTitleSize.S.titleSize,
        innerSpacing = Offset.X2S,
        sideSpacing = Offset.S
    ),

    /** @SelfDocumented */
    S(
        globalVar = InlineHeight.X2S,
        iconSize = SbisSegmentedControlIconSize.M,
        titleSize = SbisSegmentedControlTitleSize.S.titleSize,
        innerSpacing = Offset.X2S,
        sideSpacing = Offset.M
    )
}
package ru.tensor.sbis.segmented_control.control.models

import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import ru.tensor.sbis.segmented_control.R

/**
 * Варианты стилей сегмент-контрола.
 *
 * @author ps.smirnyh
 */
enum class SbisSegmentedControlStyle(
    @AttrRes internal val segmentedControlStyle: Int,
    @StyleRes internal val baseSegmentedControlStyle: Int
) {

    /** @SelfDocumented */
    DEFAULT(
        R.attr.sbisSegmentedControlDefaultTheme,
        R.style.SbisSegmentedControlDefaultTheme
    ),

    /** @SelfDocumented */
    PRIMARY(
        R.attr.sbisSegmentedControlPrimaryTheme,
        R.style.SbisSegmentedControlPrimaryTheme
    ),

    /** @SelfDocumented */
    SECONDARY(
        R.attr.sbisSegmentedControlSecondaryTheme,
        R.style.SbisSegmentedControlSecondaryTheme
    ),

    /** @SelfDocumented */
    SUCCESS(
        R.attr.sbisSegmentedControlSuccessTheme,
        R.style.SbisSegmentedControlSuccessTheme
    ),

    /** @SelfDocumented */
    WARNING(
        R.attr.sbisSegmentedControlWarningTheme,
        R.style.SbisSegmentedControlWarningTheme
    ),

    /** @SelfDocumented */
    DANGER(
        R.attr.sbisSegmentedControlDangerTheme,
        R.style.SbisSegmentedControlDangerTheme
    ),

    /** @SelfDocumented */
    INFO(
        R.attr.sbisSegmentedControlInfoTheme,
        R.style.SbisSegmentedControlInfoTheme
    )
}
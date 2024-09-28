package ru.tensor.sbis.segmented_control.item.models

/**
 * Модель заголовка в сегмент-контролле.
 *
 * @author ps.smirnyh
 */
data class SbisSegmentedControlTitle(
    val text: CharSequence,
    val size: SbisSegmentedControlTitleSize? = null
)

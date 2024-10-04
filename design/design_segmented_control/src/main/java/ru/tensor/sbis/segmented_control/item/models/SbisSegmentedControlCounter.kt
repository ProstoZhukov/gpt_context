package ru.tensor.sbis.segmented_control.item.models

/**
 * Модель счетчика в сегмент-контроле.
 *
 * @author ps.smirnyh
 */
data class SbisSegmentedControlCounter(

    /** Значение счетчика. */
    var counter: Int,

    /** Стиль счетчика. */
    var style: SbisSegmentedControlCounterStyle = SbisSegmentedControlCounterStyle.SECONDARY
)
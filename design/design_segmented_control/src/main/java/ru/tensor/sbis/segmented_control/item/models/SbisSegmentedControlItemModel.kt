package ru.tensor.sbis.segmented_control.item.models

/**
 * Модель сегмента в сегмент-контроле.
 *
 * @author ps.smirnyh
 */
data class SbisSegmentedControlItemModel internal constructor(
    val icon: SbisSegmentedControlIcon? = null,
    val title: SbisSegmentedControlTitle? = null,
    val counter: SbisSegmentedControlCounter? = null
) {

    constructor(
        title: SbisSegmentedControlTitle,
        icon: SbisSegmentedControlIcon,
        counter: SbisSegmentedControlCounter? = null
    ) : this(icon, title, counter)

    constructor(
        title: SbisSegmentedControlTitle,
        counter: SbisSegmentedControlCounter? = null
    ) : this(null, title, counter)

    constructor(
        icon: SbisSegmentedControlIcon,
        counter: SbisSegmentedControlCounter? = null
    ) : this(icon, null, counter)
}
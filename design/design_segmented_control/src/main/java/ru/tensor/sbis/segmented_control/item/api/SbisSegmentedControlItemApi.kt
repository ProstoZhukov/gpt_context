package ru.tensor.sbis.segmented_control.item.api

import ru.tensor.sbis.segmented_control.item.models.SbisSegmentedControlItemModel

/**
 * Api сегмента в сегмент-контроле.
 *
 * @author ps.smirnyh
 */
internal interface SbisSegmentedControlItemApi {

    /** Модель контента для сегмента. */
    var model: SbisSegmentedControlItemModel
}
package ru.tensor.sbis.segmented_control.utils

import ru.tensor.sbis.segmented_control.item.models.SbisSegmentedControlItemModel

/**
 * Слушатель изменения позиции сегмента.
 *
 * @param indexSegment индекс выбранного сегмента.
 * @param segmentModel модель выбранного сегмента.
 *
 * @author ps.smirnyh
 */
typealias SegmentChangedListener = (indexSegment: Int, segmentModel: SbisSegmentedControlItemModel) -> Unit
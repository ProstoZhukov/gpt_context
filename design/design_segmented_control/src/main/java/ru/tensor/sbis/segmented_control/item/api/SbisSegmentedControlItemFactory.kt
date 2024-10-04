package ru.tensor.sbis.segmented_control.item.api

import android.content.Context
import android.view.View
import ru.tensor.sbis.segmented_control.item.models.SbisSegmentedControlItemModel
import ru.tensor.sbis.segmented_control.utils.SegmentedControlStyleHolder

/**
 * Интерфейс фабрики для создания сегмента для сегмент-контрола.
 *
 * @author ps.smirnyh
 */
internal interface SbisSegmentedControlItemFactory {

    /** @SelfDocumented */
    fun createItem(
        context: Context,
        newModel: SbisSegmentedControlItemModel,
        isSelected: Boolean,
        isContrast: Boolean,
        styleHolder: SegmentedControlStyleHolder
    ): View
}
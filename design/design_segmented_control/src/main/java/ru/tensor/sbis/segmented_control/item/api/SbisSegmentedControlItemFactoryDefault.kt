package ru.tensor.sbis.segmented_control.item.api

import android.content.Context
import ru.tensor.sbis.segmented_control.R
import ru.tensor.sbis.segmented_control.item.SbisSegmentedControlItem
import ru.tensor.sbis.segmented_control.item.models.SbisSegmentedControlItemModel
import ru.tensor.sbis.segmented_control.utils.SegmentedControlStyleHolder

/**
 * Стандартная реализация [SbisSegmentedControlItemFactory].
 *
 * @author ps.smirnyh
 */
internal object SbisSegmentedControlItemFactoryDefault : SbisSegmentedControlItemFactory {
    override fun createItem(
        context: Context,
        newModel: SbisSegmentedControlItemModel,
        isSelected: Boolean,
        isContrast: Boolean,
        styleHolder: SegmentedControlStyleHolder
    ) = SbisSegmentedControlItem(context).apply {
        id = R.id.design_segmented_control_segment
        model = newModel
        onSelectedChanged(
            isSelected,
            styleHolder.getItemTextColorBySelected(isContrast),
            styleHolder.getItemIconColorBySelected(isContrast)
        )
    }
}
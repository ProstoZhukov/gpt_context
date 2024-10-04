package ru.tensor.sbis.design.universal_selection.domain.factory.customization.selectable.view_holders.simple

import android.view.ViewGroup
import androidx.core.view.isVisible
import ru.tensor.sbis.design.universal_selection.domain.factory.UniversalSelectionItem
import ru.tensor.sbis.design.universal_selection.domain.factory.customization.selectable.view_holders.BaseUniversalSelectionViewHolder
import ru.tensor.sbis.design_selection.contract.customization.selection.SelectionClickDelegate

/**
 * Ячейка обычного элемента, доступного для выбора, в компоненте универсального справочника.
 *
 * @author vv.chekurda
 */
internal class UniversalSelectionViewHolder(parentView: ViewGroup) :
    BaseUniversalSelectionViewHolder<UniversalSelectionItem>(parentView) {

    override fun bind(
        data: UniversalSelectionItem,
        clickDelegate: SelectionClickDelegate,
        isMultiSelection: Boolean
    ) {
        super.bind(data, clickDelegate, isMultiSelection)
        binding.universalSelectionItemSelectionIconClickArea.isVisible = isMultiSelection
        binding.universalSelectionItemSelectionIcon.isVisible = isMultiSelection
    }
}
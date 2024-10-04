package ru.tensor.sbis.design.universal_selection.domain.factory.customization

import ru.tensor.sbis.design.universal_selection.domain.factory.UniversalItem
import ru.tensor.sbis.design.universal_selection.domain.factory.customization.selectable.UniversalSelectableItemsCustomization
import ru.tensor.sbis.design.universal_selection.domain.factory.customization.selected.UniversalSelectedItemsCustomization
import ru.tensor.sbis.design_selection.contract.customization.SelectionCustomization
import ru.tensor.sbis.design_selection.contract.customization.selected.SelectedItemsCustomization
import ru.tensor.sbis.design_selection.contract.customization.selection.SelectableItemsCustomization

/**
 * Кастомизация компонента выбора для универсального справочника.
 *
 * @author vv.chekurda
 */
internal class UniversalSelectionCustomization : SelectionCustomization<UniversalItem> {

    override fun getListItemsCustomization(): SelectableItemsCustomization<UniversalItem> =
        UniversalSelectableItemsCustomization()

    override fun getSelectedItemsCustomization(): SelectedItemsCustomization<UniversalItem> =
        UniversalSelectedItemsCustomization()
}
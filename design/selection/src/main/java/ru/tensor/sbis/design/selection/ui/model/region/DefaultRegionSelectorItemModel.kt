package ru.tensor.sbis.design.selection.ui.model.region

import ru.tensor.sbis.design.selection.bl.contract.model.SelectorItemId
import ru.tensor.sbis.design.selection.bl.contract.model.SelectorItemMeta
import ru.tensor.sbis.design.selection.ui.model.HierarchySelectorItemModel

/**
 * Реализация по умолчанию для выбора регионов
 *
 * @author ma.kolpakov
 */
data class DefaultRegionSelectorItemModel(
    override val id: SelectorItemId,
    override val title: String,
    override val subtitle: String? = null,
    override val counter: Int = 0,
    override val hasNestedItems: Boolean = false
) : RegionSelectorItemModel, HierarchySelectorItemModel {

    override lateinit var meta: SelectorItemMeta
}
package ru.tensor.sbis.design.selection.ui.model

/**
 * Модель [SelectorItemModel] для иерархических структур
 *
 * @author ma.kolpakov
 */
interface HierarchySelectorItemModel : SelectorItemModel {

    val hasNestedItems: Boolean
}
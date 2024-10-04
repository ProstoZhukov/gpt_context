package ru.tensor.sbis.design_selection.contract.customization.selected.panel

import ru.tensor.sbis.design_selection.contract.data.SelectionItem

/**
 * Данные о выбранных элементах в компоненте выбора.
 *
 * @property items список выбранных элементов.
 * @property isUserSelection true, если текущий список выбранных элементов
 * был загружен после пользовательских изменений.
 * @property hasSelectedItems признак наличия выбранных элементов вне зависимости от текущих параметрво фильтрации.
 * @property isMultiSelection признак мультивыбора.
 *
 * @author vv.chekurda
 */
data class SelectedData<ITEM : SelectionItem>(
    val items: List<ITEM> = emptyList(),
    val isUserSelection: Boolean = false,
    val hasSelectedItems: Boolean = items.isNotEmpty(),
    val isMultiSelection: Boolean = true
)
package ru.tensor.sbis.design.selection.ui.contract

import ru.tensor.sbis.design.selection.bl.contract.model.SelectorItem

/**
 * Функция загрузки выбранных элементов при инициализации
 *
 * @author ma.kolpakov
 */
interface MultiSelectionLoader<out DATA : SelectorItem> {

    fun loadSelectedItems(): List<DATA>
}
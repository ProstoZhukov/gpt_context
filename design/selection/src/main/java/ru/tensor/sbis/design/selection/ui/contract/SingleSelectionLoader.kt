package ru.tensor.sbis.design.selection.ui.contract

import ru.tensor.sbis.design.selection.bl.contract.model.SelectorItem

/**
 * Функция загрузки выбранного элемента при инициализации
 *
 * @author us.bessonov
 */
interface SingleSelectionLoader<out DATA : SelectorItem> {

    /**
     * @return текущий выбранный элемент при его наличии
     */
    fun loadSelectedItem(): DATA?
}
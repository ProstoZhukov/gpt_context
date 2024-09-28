package ru.tensor.sbis.design.selection.bl.vm.selection.single

import ru.tensor.sbis.design.selection.bl.contract.model.SelectorItem
import ru.tensor.sbis.design.selection.bl.vm.selection.SelectionViewModel

/**
 * Вьюмодель одиночного выбора
 *
 * @author us.bessonov
 */
internal interface SingleSelectionViewModel<ITEM : SelectorItem> : SelectionViewModel<ITEM> {

    /**
     * Делает элемент выбранным и завершает выбор
     */
    fun complete(data: ITEM)
}
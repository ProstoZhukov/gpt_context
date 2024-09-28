package ru.tensor.sbis.design_selection.contract.customization.selection

import ru.tensor.sbis.design_selection.contract.data.SelectionItem

/**
 * Обработчик нажатий для элементов доступных для выбора компонента выбора.
 *
 * @author vv.chekurda
 */
interface SelectionClickDelegate {

    /**
     * Нажатие на кнопку добавления в список выбранных, расположенную справа.
     */
    fun onAddButtonClicked(item: SelectionItem)

    /**
     * Нажатие на элемент.
     */
    fun onItemClicked(item: SelectionItem)

    /**
     * Длительное нажатие на элемент.
     */
    fun onItemLongClicked(item: SelectionItem)

    /**
     * Обработать клик, по которому начинается навигация.
     */
    fun onNavigateClicked(item: SelectionItem)
}

package ru.tensor.sbis.design.selection.ui.list.listener

import ru.tensor.sbis.design.selection.ui.model.SelectorItemModel

/**
 * Обработчик нажатий для элементов компонента выбора
 *
 * @author ma.kolpakov
 */
interface SelectionClickDelegate<DATA : SelectorItemModel> {

    /**
     * Нажатие на кнопку добавления в список выбранных, расположенную справа: (+) или (-)
     */
    fun onAddButtonClicked(data: DATA)

    /**
     * Нажатие на сам элемент
     */
    fun onItemClicked(data: DATA)

    /**
     * Длительное нажатие на сам элемент
     */
    fun onItemLongClicked(data: DATA)
}

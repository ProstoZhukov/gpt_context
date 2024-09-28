package ru.tensor.sbis.master_detail

/**
 * Позволяет управлять возможностями выделения элементов в списке.
 *
 * @author du.bykov
 */
interface SelectionHelper {

    /**
     * Очистка выделения элементов списка.
     */
    fun cleanSelection()

    /**
     * Элемент списка во фрагменте должен оставлять элементы выделенными после нажатии на них.
     */
    fun shouldHighlightSelectedItems()
}
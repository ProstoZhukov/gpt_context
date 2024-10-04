package ru.tensor.sbis.design_selection.contract.controller

import ru.tensor.sbis.communication_decl.selection.SelectionItemId
import ru.tensor.sbis.design_selection.contract.data.SelectionItem

/**
 * Делегат операций выбора компонента выбора.
 *
 * @author vv.chekurda
 */
interface SelectionDelegate<out ITEM : SelectionItem> {

    /**
     * Получить список выбранных элементов [ITEM] по текущему фильтру.
     */
    fun getSelectedItems(): List<ITEM>

    /**
     * Получить список всех выбранных элементов [ITEM] вне зависимости от фильтра.
     */
    fun getAllSelectedItems(): List<ITEM>

    /**
     * Выбрать элемент.
     *
     * @param id идентификатор элемента.
     * @param withNotify признак необходимости обновить список по нотификации контроллера.
     */
    fun select(id: SelectionItemId, withNotify: Boolean = true)

    /**
     * Выбрать один элемент и завершить.
     *
     * @param id идентификатор элемента.
     */
    fun singleComplete(id: SelectionItemId): ITEM

    /**
     * Заменить все выбранные элементы новым выбранным.
     *
     * @param id идентификатор элемента, на который необходимо заменить весь список выбранных.
     * @param withNotify признак необходимости обновить список по нотификации контроллера.
     */
    fun replaceSelected(id: SelectionItemId, withNotify: Boolean = true)

    /**
     * Отменить выбор элемента.
     *
     * @param id идентификатор элемента, который нужно сделать невыбранным.
     */
    fun unselect(id: SelectionItemId)

    /**
     * Установить колбэк об измении параметров фильтрации списка.
     */
    fun setOnFilterChangedCallback(callback: (() -> Unit)?)

    /**
     * Получить признак наличия выбранных элементов вне зависимости от текущего фильтра.
     */
    fun hasSelectedItems(): Boolean
}
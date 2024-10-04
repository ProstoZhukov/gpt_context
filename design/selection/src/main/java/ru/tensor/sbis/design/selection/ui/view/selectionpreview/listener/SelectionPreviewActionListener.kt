package ru.tensor.sbis.design.selection.ui.view.selectionpreview.listener

import ru.tensor.sbis.design.selection.ui.view.selectionpreview.model.SelectionPreviewListData
import ru.tensor.sbis.design.selection.ui.view.selectionpreview.model.SelectionSuggestionListData

/**
 * Слушатель событий нажатия на элементы превью выбора, либо блока предложений для выбора.
 * Достаточно реализовать только методы, необходимые для конкретного случая
 *
 * @see SelectionPreviewListData
 * @see SelectionSuggestionListData
 *
 * @author us.bessonov
 */
interface SelectionPreviewActionListener<in ITEM> {

    /**
     * Обработчик нажатия на элемент
     */
    fun onItemClick(item: ITEM)

    /**
     * Обработчик нажатия на кнопку удаления элемента (для превью выбора)
     */
    fun onRemoveClick(item: ITEM)

    /**
     * Обработчик нажатия на заголовок или кнопку "Ещё" для показа всех предлагаемых для выбора элементов
     */
    fun onShowAllClick()
}
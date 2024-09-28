package ru.tensor.sbis.design.selection.ui.view.selectionpreview.listener

/**
 * Адаптер [SelectionPreviewActionListener] для избавления от необходимости реализации всех методов
 *
 * @author us.bessonov
 */
abstract class SelectionPreviewActionListenerAdapter<in ITEM> : SelectionPreviewActionListener<ITEM> {

    override fun onItemClick(item: ITEM) {
    }

    override fun onRemoveClick(item: ITEM) {
    }

    override fun onShowAllClick() {
    }
}
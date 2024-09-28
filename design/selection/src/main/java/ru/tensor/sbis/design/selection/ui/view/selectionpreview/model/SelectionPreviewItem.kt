package ru.tensor.sbis.design.selection.ui.view.selectionpreview.model

/**
 * Интерфейс превью выбранного элемента списка
 *
 * @author us.bessonov
 */
interface SelectionPreviewItem {
    val title: String
    val isCancellable: Boolean
    val isAcceptable: Boolean
}

/**
 * Стандартная модель превью выбранного элемента списка
 *
 * @author us.bessonov
 */
data class DefaultSelectionPreviewItem(
    override val title: String,
    override val isCancellable: Boolean,
    override val isAcceptable: Boolean
) : SelectionPreviewItem
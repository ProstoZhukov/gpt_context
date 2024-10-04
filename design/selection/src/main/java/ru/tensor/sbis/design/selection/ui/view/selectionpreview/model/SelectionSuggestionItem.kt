package ru.tensor.sbis.design.selection.ui.view.selectionpreview.model

/**
 * Интерфейс пункта списка предлагаемых элементов
 *
 * @author us.bessonov
 */
interface SelectionSuggestionItem {
    val title: String
    val count: Int
}

/**
 * Стандартная модель пункта списка предлагаемых элементов
 *
 * @author us.bessonov
 */
data class DefaultSelectionSuggestionItem(override val title: String, override val count: Int) : SelectionSuggestionItem
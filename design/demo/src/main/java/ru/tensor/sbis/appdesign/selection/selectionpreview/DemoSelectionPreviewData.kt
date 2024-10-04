/**
 * Данные для превью в компоненте выбора.
 *
 * @author us.bessonov
 */
package ru.tensor.sbis.appdesign.selection.selectionpreview

import ru.tensor.sbis.design.selection.ui.view.selectionpreview.model.DefaultSelectionPreviewItem
import ru.tensor.sbis.design.selection.ui.view.selectionpreview.model.DefaultSelectionSuggestionItem

internal val demoPreviewItems = mutableListOf(
    DefaultSelectionPreviewItem("Москва", isCancellable = true, isAcceptable = true),
    DefaultSelectionPreviewItem("Санкт-Петербург", isCancellable = true, isAcceptable = true),
    DefaultSelectionPreviewItem("Адыгея", isCancellable = true, isAcceptable = true),
    DefaultSelectionPreviewItem("Алтай", isCancellable = true, isAcceptable = true),
    DefaultSelectionPreviewItem("Алтайский край", isCancellable = true, isAcceptable = true),
    DefaultSelectionPreviewItem("Амурская обл", isCancellable = true, isAcceptable = true),
    DefaultSelectionPreviewItem("Архангельская обл", isCancellable = true, isAcceptable = true)
)

internal val demoSelectAllItem = DefaultSelectionPreviewItem("Все регионы", isCancellable = false, isAcceptable = false)

internal val demoSuggestionItems = listOf(
    DefaultSelectionSuggestionItem("Безопасность, охрана", 21385),
    DefaultSelectionSuggestionItem("Бизнес, финансы, страхование, маркетинг", 299444),
    DefaultSelectionSuggestionItem("Бумага, тара, упаковка", 5106),
    DefaultSelectionSuggestionItem("Деревообработка, лес", 111007),
    DefaultSelectionSuggestionItem("Уомпьютеры, офисная техника, ПО", 121154)
)
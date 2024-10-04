package ru.tensor.sbis.design.selection.ui.view.selectionpreview.model

import androidx.annotation.StringRes
import ru.tensor.sbis.design.selection.R
import ru.tensor.sbis.design.selection.ui.view.selectionpreview.listener.SelectionPreviewActionListener

/**
 * Значения максимального числа видимых элементов по умолчанию
 */
const val MAX_DISPLAYED_PREVIEW_ENTRIES = 3
const val MAX_DISPLAYED_SUGGESTION_ENTRIES = 3

/**
 * Набор типов данных для отображения компонентом превью выбора
 *
 * @author us.bessonov
 */
sealed class SelectionPreviewData

/**
 * Данные для отображения блока со списком превью выбранных элементов
 *
 * @author us.bessonov
 */
data class SelectionPreviewListData<PREVIEW_ITEM : SelectionPreviewItem> @JvmOverloads constructor(
    val items: List<PREVIEW_ITEM>,
    val actionListener: SelectionPreviewActionListener<PREVIEW_ITEM>,
    val maxDisplayedEntries: Int = MAX_DISPLAYED_PREVIEW_ENTRIES
) : SelectionPreviewData()

/**
 * Данные для отображения блока с заголовком и предлагаемыми для выбора элементами
 *
 * @property headerTitle текст заголовка, либо `null` если заголовок не требуется
 * @property showMoreItem должен ли отображаться элемент "Ещё". При отсутствии значения он будет показан только если
 * число элементов превышает [maxDisplayedEntries]
 *
 * @author us.bessonov
 */
data class SelectionSuggestionListData<SUGGESTION_ITEM : SelectionSuggestionItem> @JvmOverloads constructor(
    @StringRes
    val headerTitle: Int?,
    val items: List<SUGGESTION_ITEM>,
    val actionListener: SelectionPreviewActionListener<SUGGESTION_ITEM>,
    val maxDisplayedEntries: Int = MAX_DISPLAYED_SUGGESTION_ENTRIES,
    val showMoreItem: Boolean = items.size > maxDisplayedEntries,
    val moreItemTitle: Int = R.string.selection_suggestion_more
) : SelectionPreviewData()

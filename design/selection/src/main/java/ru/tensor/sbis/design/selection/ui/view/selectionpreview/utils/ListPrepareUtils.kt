/**
 * Инструменты для подготовки данных перед отображением в SelectionPreviewView
 *
 * @author ma.kolpakov
 */
package ru.tensor.sbis.design.selection.ui.view.selectionpreview.utils

import androidx.annotation.StringRes
import ru.tensor.sbis.design.selection.R
import ru.tensor.sbis.design.selection.ui.utils.CounterFormat
import ru.tensor.sbis.design.selection.ui.view.selectionpreview.model.SelectionPreviewItem
import ru.tensor.sbis.design.selection.ui.view.selectionpreview.model.SelectionPreviewListData
import ru.tensor.sbis.design.selection.ui.view.selectionpreview.model.SelectionSuggestionItem
import ru.tensor.sbis.design.selection.ui.view.selectionpreview.model.SelectionSuggestionListData
import ru.tensor.sbis.design.selection.ui.view.selectionpreview.vm.*
import kotlin.math.min

/**
 * Формирует список вьюмоделей превью выбранных элементов
 */
internal fun <PREVIEW_ITEM : SelectionPreviewItem> prepareSelectionPreviewList(
    data: SelectionPreviewListData<PREVIEW_ITEM>,
    stringProvider: StringProvider
): List<SelectionPreviewListItemVm> = mutableListOf<SelectionPreviewListItemVm>().apply {
    val list = data.items
    val displayedCount = min(list.size, data.maxDisplayedEntries)

    list.take(displayedCount)
        .forEachIndexed { i, it ->
            add(it.toItemVm(data.actionListener::onItemClick, data.actionListener::onRemoveClick))
            if (i < displayedCount - 1) {
                add(SelectionPreviewDividerItemVm)
            }
        }

    if (list.size > data.maxDisplayedEntries) {
        add(createPreviewTotalCountItemVm(list.size, stringProvider))
    }
}

/**
 * Формирует список вьюмоделей предлагаемых элементов
 */
internal fun <SUGGESTION_ITEM : SelectionSuggestionItem> prepareSuggestionList(
    data: SelectionSuggestionListData<SUGGESTION_ITEM>,
    stringProvider: StringProvider
): List<SelectionPreviewListItemVm> = mutableListOf<SelectionPreviewListItemVm>().apply {
    val list = data.items
    val displayedCount = min(list.size, data.maxDisplayedEntries)

    data.headerTitle?.let {
        add(createSuggestionHeaderItemVm(it, data.actionListener::onShowAllClick, stringProvider))
    }

    list.take(displayedCount)
        .forEachIndexed { i, it ->
            add(it.toItemVm(data.actionListener::onItemClick))
            if (i < displayedCount - 1) {
                add(SelectionPreviewDividerItemVm)
            }
        }

    if (data.showMoreItem) {
        add(createSuggestionFooterItemVM(data.moreItemTitle, data.actionListener::onShowAllClick, stringProvider))
    }
}

/**@SelfDocumented**/
internal fun <PREVIEW_ITEM : SelectionPreviewItem> PREVIEW_ITEM.toItemVm(
    itemClickAction: (PREVIEW_ITEM) -> Unit,
    removeClickAction: (PREVIEW_ITEM) -> Unit
) = SelectionPreviewItemVm(
    title,
    isCancellable,
    isAcceptable,
    { itemClickAction(this) },
    { removeClickAction(this) }
)

private fun createPreviewTotalCountItemVm(
    count: Int,
    stringProvider: StringProvider
) = SelectionPreviewTotalCountItemVm(
    stringProvider.getFormattedString(R.string.selection_preview_total_count_format, count)
)

private fun createSuggestionHeaderItemVm(
    @StringRes
    title: Int,
    clickAction: () -> Unit,
    stringProvider: StringProvider
) = SelectionSuggestionHeaderItemVm(
    stringProvider.getString(title), clickAction
)

private fun createSuggestionFooterItemVM(
    @StringRes
    title: Int,
    clickAction: () -> Unit,
    stringProvider: StringProvider
) = SelectionSuggestionMoreItemVm(
    stringProvider.getText(title), clickAction
)

/**@SelfDocumented**/
internal fun <SUGGESTION_ITEM : SelectionSuggestionItem> SUGGESTION_ITEM.toItemVm(
    itemClickAction: (SUGGESTION_ITEM) -> Unit
) = SelectionSuggestionItemVm(
    title,
    CounterFormat.THOUSANDS_DECIMAL_FORMAT.format(count).orEmpty()
) { itemClickAction(this) }

package ru.tensor.sbis.design.selection.ui.view.selectionpreview.vm

import ru.tensor.sbis.design.selection.ui.view.selectionpreview.model.SelectionPreviewItem
import ru.tensor.sbis.design.selection.ui.view.selectionpreview.model.SelectionSuggestionItem
import ru.tensor.sbis.design.selection.ui.view.selectionpreview.view.SelectionPreviewView

/**
 * Представляет вьюмодели ячеек, отображаемых в [SelectionPreviewView]
 *
 * @author us.bessonov
 */
sealed class SelectionPreviewListItemVm

/**
 * Вьюмодель разделителя списка
 *
 * @author us.bessonov
 */
internal object SelectionPreviewDividerItemVm : SelectionPreviewListItemVm()

/**
 * Вьюмодель превью выбранного элемента
 *
 * @see SelectionPreviewItem
 * @author us.bessonov
 */
internal class SelectionPreviewItemVm(
    val title: String,
    val isRemoveIconVisible: Boolean,
    val isMarkIconVisible: Boolean,
    var onClick: (() -> Unit)? = null,
    var onRemoveClick: (() -> Unit)? = null
) : SelectionPreviewListItemVm()

/**
 * Вьюмодель ячейки с общим числом выбранных элементов
 *
 * @author us.bessonov
 */
internal class SelectionPreviewTotalCountItemVm(val titleWithCount: String, var onClick: (() -> Unit)? = null) :
    SelectionPreviewListItemVm()

/**
 * Вьюмодель заголовка блока предлагаемых элементов
 *
 * @author us.bessonov
 */
internal class SelectionSuggestionHeaderItemVm(val title: String, var onClick: (() -> Unit)? = null) :
    SelectionPreviewListItemVm()

/**
 * Вьюмодель предлагаемого элемента
 *
 * @see SelectionSuggestionItem
 * @author us.bessonov
 */
internal class SelectionSuggestionItemVm(val title: String, val count: String, var onClick: (() -> Unit)? = null) :
    SelectionPreviewListItemVm()

/**
 * Вьюмодель ячейки "Ешё"
 *
 * @author us.bessonov
 */
internal class SelectionSuggestionMoreItemVm(val title: CharSequence, var onClick: (() -> Unit)? = null) :
    SelectionPreviewListItemVm()

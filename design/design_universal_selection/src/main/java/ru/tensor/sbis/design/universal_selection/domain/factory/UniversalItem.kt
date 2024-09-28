/**
 * Внутренние модели реализации элементов компонента выбора получателей.
 *
 * @author vv.chekurda
 */
package ru.tensor.sbis.design.universal_selection.domain.factory

import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.common_views.SearchSpan
import ru.tensor.sbis.communication_decl.selection.universal.data.UniversalItemId
import ru.tensor.sbis.design_selection.contract.data.SelectionFolderItem
import ru.tensor.sbis.design_selection.contract.data.SelectionItem

/**
 * Базовый элемент для списка универсального справочника.
 */
internal sealed interface UniversalItem : SelectionItem

/**
 * Обычный элемент для списка универсального справочника.
 * Включает в себя заголовок, подзаголовок.
 */
@Parcelize
internal data class UniversalSelectionItem(
    override val id: UniversalItemId,
    override val title: String,
    override val subtitle: String? = null,
    override val titleHighlights: List<SearchSpan> = emptyList()
) : UniversalItem

/**
 * Элемент папки для списка универсального справочника.
 * Включает в себя заголовок, подзаголовок и возможность проваливаться, как в папку.
 */
@Parcelize
internal data class UniversalSelectionFolderItem(
    override val id: UniversalItemId,
    override val title: String,
    override val subtitle: String? = null,
    override val openable: Boolean = true,
    override val selectable: Boolean = true,
    override val titleHighlights: List<SearchSpan> = emptyList()
) : UniversalItem, SelectionFolderItem
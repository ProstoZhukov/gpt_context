package ru.tensor.sbis.design_selection.contract.filter

import ru.tensor.sbis.communication_decl.selection.SelectionItemId

/**
 * Мета-дата для создания фильтра для загрузки списка компонента выбора.
 *
 * @property query поисковый запрос.
 * @property folderId идентификатор папки при проваливания.
 *
 * @author vv.chekurda
 */
data class SelectionFilterMeta<ITEM_ID : SelectionItemId>(
    val query: String,
    val filterItemId: ITEM_ID? = null
)
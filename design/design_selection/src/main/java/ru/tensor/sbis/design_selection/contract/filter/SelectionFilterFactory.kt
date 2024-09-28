package ru.tensor.sbis.design_selection.contract.filter

import ru.tensor.sbis.communication_decl.selection.SelectionItemId

/**
 * Фабрика фильтров для загрузки списка компонента выбора.
 *
 * @author vv.chekurda
 */
interface SelectionFilterFactory<SERVICE_FILTER, ITEM_ID : SelectionItemId> {

    /**
     * Создать фильтр по мета-данным [meta].
     *
     * @return фильтр для запроса списка.
     */
    fun createFilter(meta: SelectionFilterMeta<ITEM_ID>): SERVICE_FILTER
}
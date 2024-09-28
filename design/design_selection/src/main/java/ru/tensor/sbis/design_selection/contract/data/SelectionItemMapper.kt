package ru.tensor.sbis.design_selection.contract.data

import ru.tensor.sbis.communication_decl.selection.SelectionItemId

/**
 * Маппер элементов списка контроллера в элементы списка ui компонента выбора.
 *
 * @author vv.chekurda
 */
interface SelectionItemMapper<in SERVICE_ITEM, out SERVICE_ITEM_ID,
    out ITEM : SelectionItem, in ITEM_ID : SelectionItemId> {

    /** @SelfDocumented */
    fun map(item: SERVICE_ITEM): ITEM

    /**
     * Получить идентификатор контроллера по идентификатору UI модели элемента.
     */
    fun getId(id: ITEM_ID): SERVICE_ITEM_ID
}
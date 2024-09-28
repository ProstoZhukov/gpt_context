package ru.tensor.sbis.design_selection.contract.controller

import ru.tensor.sbis.crud3.domain.Wrapper
import ru.tensor.sbis.design_selection.contract.data.SelectionItem

/**
 * Обертка коллекции списка компонента выбора.
 *
 * @see Wrapper
 *
 * @author vv.chekurda
 */
interface SelectionCollectionWrapper<COLLECTION, COLLECTION_OBSERVER, FILTER,
    PAGINATION_ANCHOR, SERVICE_ITEM_WITH_INDEX, SERVICE_ITEM, ITEM : SelectionItem> :
    Wrapper<COLLECTION, COLLECTION_OBSERVER, FILTER, PAGINATION_ANCHOR, SERVICE_ITEM_WITH_INDEX, ITEM> {

    /**
     * Поставщик обертки коллекции списка компонента выбора [SelectionCollectionWrapper].
     */
    interface Provider<COLLECTION, COLLECTION_OBSERVER, FILTER,
        PAGINATION_ANCHOR, SERVICE_ITEM_WITH_INDEX, SERVICE_ITEM, ITEM : SelectionItem> {

        /**
         * Получить обертку коллекции списка компонента выбора [SelectionCollectionWrapper].
         */
        fun getSelectionCollectionWrapper(): SelectionCollectionWrapper<COLLECTION,
            COLLECTION_OBSERVER, FILTER, PAGINATION_ANCHOR, SERVICE_ITEM_WITH_INDEX, SERVICE_ITEM, ITEM>
    }
}
package ru.tensor.sbis.design_selection.contract.controller

import ru.tensor.sbis.design_selection.contract.data.SelectionFolderItem
import ru.tensor.sbis.design_selection.contract.data.SelectionItem
import java.io.Serializable

/**
 * Обертка контроллера компонента выбора.
 *
 * @see SelectionDelegate
 * @see SelectionCollectionWrapper.Provider
 *
 * @author vv.chekurda
 */
interface SelectionControllerWrapper<COLLECTION, COLLECTION_OBSERVER, FILTER,
    PAGINATION_ANCHOR, SERVICE_ITEM_WITH_INDEX, SERVICE_ITEM, ITEM : SelectionItem> :
    SelectionCollectionWrapper.Provider<COLLECTION, COLLECTION_OBSERVER, FILTER,
        PAGINATION_ANCHOR, SERVICE_ITEM_WITH_INDEX, SERVICE_ITEM, ITEM>,
    SelectionDelegate<ITEM> {

    /**
     * Поставщик обертки контроллера компонента выбора [SelectionControllerWrapper].
     */
    interface Provider<COLLECTION, COLLECTION_OBSERVER, FILTER, PAGINATION_ANCHOR,
        SERVICE_ITEM_WITH_INDEX, SERVICE_ITEM, ITEM : SelectionItem> :
        Serializable {

        /**
         * Создать обертку контроллера компонента выбора [SelectionControllerWrapper] для папки [SelectionFolderItem].
         *
         * @param folderItem элемент папки, с которой будет работать контроллер.
         * null - рутовая папка.
         */
        fun createSelectionControllerWrapper(folderItem: SelectionFolderItem? = null): SelectionControllerWrapper<
            COLLECTION, COLLECTION_OBSERVER, FILTER, PAGINATION_ANCHOR,
            SERVICE_ITEM_WITH_INDEX, SERVICE_ITEM, ITEM>
    }
}
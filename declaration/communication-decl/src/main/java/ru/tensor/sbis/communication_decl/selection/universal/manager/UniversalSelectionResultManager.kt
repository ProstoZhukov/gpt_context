package ru.tensor.sbis.communication_decl.selection.universal.manager

import ru.tensor.sbis.communication_decl.selection.result_manager.SelectionResultManager
import ru.tensor.sbis.communication_decl.selection.universal.data.UniversalPreselectedData
import ru.tensor.sbis.communication_decl.selection.universal.data.UniversalSelectionData

/**
 * Менеджер для работы с результатами компонента универсального выбора.
 *
 * @author vv.chekurda
 */
interface UniversalSelectionResultManager
    : SelectionResultManager<UniversalPreselectedData, UniversalSelectionData, UniversalSelectionResult> {

    /**
     * Поставщик менеджера для работы с результатами компонента универсального выбора.
     */
    interface Provider {

        /**
         * Получить менеджер для работы с результатами компонента универсального выбора.
         */
        fun getUniversalSelectionResultManager(): UniversalSelectionResultManager
    }
}
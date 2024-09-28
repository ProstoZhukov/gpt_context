package ru.tensor.sbis.communication_decl.selection.recipient.manager

import ru.tensor.sbis.communication_decl.selection.recipient.data.RecipientSelectionData
import ru.tensor.sbis.communication_decl.selection.result_manager.SelectionResultDelegate
import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Делегат для передачи результата компонента выбора получателей.
 *
 * @author vv.chekurda
 */
interface RecipientSelectionResultDelegate : SelectionResultDelegate<RecipientSelectionData> {

    /**
     * Поставщик делегата передачи результата [RecipientSelectionResultDelegate].
     */
    interface Provider : Feature {

        /**
         * Получить делегат передачи результата выбора получателей.
         */
        fun getRecipientSelectionResultDelegate(): RecipientSelectionResultDelegate
    }
}
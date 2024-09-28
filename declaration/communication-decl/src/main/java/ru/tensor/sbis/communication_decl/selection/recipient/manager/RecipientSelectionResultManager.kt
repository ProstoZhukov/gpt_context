package ru.tensor.sbis.communication_decl.selection.recipient.manager

import ru.tensor.sbis.communication_decl.selection.recipient.data.RecipientId
import ru.tensor.sbis.communication_decl.selection.recipient.data.RecipientPreselectedData
import ru.tensor.sbis.communication_decl.selection.recipient.data.RecipientSelectionData
import ru.tensor.sbis.communication_decl.selection.result_manager.SelectionResultManager
import java.util.UUID

/**
 * Менеджер для работы с результатами компонента выбора получателей.
 *
 * @author vv.chekurda
 */
interface RecipientSelectionResultManager
    : SelectionResultManager<RecipientPreselectedData, RecipientSelectionData, RecipientSelectionResult> {

    /**
     * Предвыбрать персон, которые будут выбраны при следующем открытии выбора получателей.
     *
     * @param personsUuids список идентификаторов персон.
     */
    fun preselect(personsUuids: List<UUID>?)

    /**
     * Предвыбрать получателей по составным идентификаторам [RecipientId],
     * которые будут выбраны при следующем открытии выбора получателей.
     */
    fun preselectIds(ids: List<RecipientId>?)

    /**
     * Поставщик менеджера для работы с результатами компонента выбора получателей.
     */
    interface Provider {

        /**
         * Получить менеджер для работы с результатами компонента выбора получателей.
         */
        fun getRecipientSelectionResultManager(): RecipientSelectionResultManager
    }
}
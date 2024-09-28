package ru.tensor.sbis.communication_decl.recipient_selection

import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Провайдер менеджера результата выбора сотрудников
 */
interface RecipientSelectionResultManagerProviderContract : Feature {
    /**
     * Получить результат выбора сотрудников
     */
    fun getRecipientSelectionResultManager(): RecipientSelectionResultManagerContract
}
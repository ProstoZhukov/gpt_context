package ru.tensor.sbis.recipient_selection.profile.contract

import ru.tensor.sbis.communication_decl.recipient_selection.RecipientSelectionResultManagerProviderContract
import ru.tensor.sbis.recipient_selection.profile.ui.resultmanager.RecipientSelectionResultManager

/**
 * Провайдер менеджера для получения данных о выбранных получателях
 */
interface RecipientSelectionResultManagerProvider : RecipientSelectionResultManagerProviderContract {

    override fun getRecipientSelectionResultManager(): RecipientSelectionResultManager
}
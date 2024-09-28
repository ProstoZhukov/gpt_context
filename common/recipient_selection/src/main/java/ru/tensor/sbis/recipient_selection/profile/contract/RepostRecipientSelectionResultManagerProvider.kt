package ru.tensor.sbis.recipient_selection.profile.contract

import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.recipient_selection.profile.ui.resultmanager.RecipientSelectionResultManager

/**
 * Поставщик менеджера репоста для получения данных о выбранных получателях
 */
interface RepostRecipientSelectionResultManagerProvider : Feature {

    fun getRepostRecipientSelectionResultManager(): RecipientSelectionResultManager
}
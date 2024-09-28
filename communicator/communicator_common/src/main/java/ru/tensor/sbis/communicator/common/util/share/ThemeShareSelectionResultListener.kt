package ru.tensor.sbis.communicator.common.util.share

import ru.tensor.sbis.communicator.common.data.theme.ConversationModel

/**
 * Слушатель результата выбора диалога/канала для шаринга.
 *
 * @author vv.chekurda
 */
interface ThemeShareSelectionResultListener {

    /**
     * Обработать выбор переписки [model].
     */
    fun onConversationSelected(model: ConversationModel)
}
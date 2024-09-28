package ru.tensor.sbis.communicator.themes_registry.ui.conversationparticipants

import java.util.*

/**
 * Слушатель результатов участников чата/диалога
 *
 * @author da.zhukov
 */
internal interface ConversationParticipantsResultListener {

    /** @SelfDocumented */
    fun onResultOk(result: String)

    /** @SelfDocumented */
    fun onResultOk(result: UUID)

    /** @SelfDocumented */
    fun onResultCancel()
}

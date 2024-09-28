package ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations

import android.view.MotionEvent
import ru.tensor.sbis.communicator.common.data.theme.ConversationModel

/**
 * Обработчик нажатий на диалог/чат
 *
 * @author rv.krohalev
 */
internal interface ConversationItemClickHandler {

    /** @SelfDocumented */
    fun onConversationItemClicked(conversation: ConversationModel)

    /** @SelfDocumented */
    fun onConversationItemLongClicked(conversation: ConversationModel)

    /** @SelfDocumented */
    fun onConversationItemTouch(conversation: ConversationModel, event: MotionEvent)
}
package ru.tensor.sbis.communicator.common.conversation

import ru.tensor.sbis.communication_decl.communicator.ConversationToolbarEventProvider
import ru.tensor.sbis.communication_decl.communicator.event.ConversationToolbarEvent
import ru.tensor.sbis.plugin_struct.feature.Feature

/** @SelfDocumented */
interface ConversationToolbarEventManagerProvider : Feature {

    /** @SelfDocumented */
    fun getConversationToolbarEventManager(): ConversationToolbarEventManager
}

/** @SelfDocumented */
interface ConversationToolbarEventManager : ConversationToolbarEventProvider {

    /** @SelfDocumented */
    val hasObservers: Boolean

    /** @SelfDocumented */
    fun postEvent(event: ConversationToolbarEvent)
}
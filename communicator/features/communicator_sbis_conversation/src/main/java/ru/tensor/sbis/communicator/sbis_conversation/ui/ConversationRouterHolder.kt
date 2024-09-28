package ru.tensor.sbis.communicator.sbis_conversation.ui

import ru.tensor.sbis.communicator.common.conversation.ConversationRouter


/**
 * Интерфейс установки маршрутизатора для открытия других экранов из экрана переписки
 */
internal interface ConversationRouterHolder {

    /** @SelfDocumented */
    fun setRouter(router: ConversationRouter?)

}
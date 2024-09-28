package ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.navigation

import ru.tensor.sbis.communicator.common.navigation.contract.CommunicatorConversationRouter
import ru.tensor.sbis.design.utils.DebounceActionHandler
import java.util.*
import javax.inject.Inject

/**
 * Роутер view списка статусов прочитанности сообщения
 *
 * @author vv.chekurda
 */
interface ReadStatusListRouter {

    /**
     * Показать профиль сотрудника
     *
     * @param personUuid идентификатор сотрудника
     */
    fun showProfile(personUuid: UUID)
}

/**
 * Реализация роутера view списка статусов прочитанности сообщения
 *
 * @property communicatorConversationRouter роутер навигации
 */
internal class ReadStatusListRouterImpl @Inject constructor(
    private val communicatorConversationRouter: CommunicatorConversationRouter,
) : ReadStatusListRouter {

    override fun showProfile(personUuid: UUID) = DebounceActionHandler.INSTANCE.handle {
        communicatorConversationRouter.showProfile(personUuid)
    }
}

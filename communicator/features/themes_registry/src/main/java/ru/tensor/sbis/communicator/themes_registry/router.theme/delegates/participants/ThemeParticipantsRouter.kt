package ru.tensor.sbis.communicator.themes_registry.router.theme.delegates.participants

import ru.tensor.sbis.communicator.common.data.theme.ConversationModel
import ru.tensor.sbis.communicator.themes_registry.router.theme.ThemeRouterInitializer
import java.util.*

/**
 * Роутер участников переписки реестра диалогов
 * @see [ThemeRouterInitializer]
 *
 * @author vv.chekurda
 */
internal interface ThemeParticipantsRouter :
    ThemeRouterInitializer {

    /**
     * Показать профиль сотрудника
     *
     * @param uuid идентификатор сотрудника
     */
    fun showProfile(uuid: UUID)

    /**
     * Показать участинков переписки
     *
     * @param conversation модель переписки
     */
    fun showConversationMembers(conversation: ConversationModel)
}
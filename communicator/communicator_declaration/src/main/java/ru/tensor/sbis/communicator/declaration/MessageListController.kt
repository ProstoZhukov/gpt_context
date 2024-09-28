package ru.tensor.sbis.communicator.declaration

import ru.tensor.sbis.base_components.adapter.sectioned.content.ListController

/**
 * Интерфейс секции списка сообщений.
 *
 * @author vv.chekurda
 */
interface MessageListController : ListController {

    /** @SelfDocumented */
    fun updateList()

    /** @SelfDocumented */
    fun onDestroy()

    /** @SelfDocumented */
    fun showConversationMembers()

    /** Оповестить секцию о показе диалога привязки номера телефона. */
    fun showVerificationPhoneDialog() = Unit
}
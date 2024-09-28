package ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.chats.holders

import ru.tensor.sbis.communicator.common.data.theme.ConversationModel

/**
 * Слушатель действий с элементами списка чатов
 *
 * @author vv.chekurda
 */
internal interface ChatListActionsListener {

    /** @SelfDocumented */
    fun onCollageViewClick(conversation: ConversationModel)

    /** @SelfDocumented */
    fun onSwipeHideClicked(conversation: ConversationModel, isByDismiss: Boolean)

    /**
     * Обработка клика по опции прикрепить чат
     * @param conversation модель чата, который собираемся прикреплять
     */
    fun onPinChatClicked(conversation: ConversationModel)

    /**
     * Обработка клика по опции открепить чат
     * @param conversation модель чата, который собираемся откреплять
     */
    fun onUnpinChatClicked(conversation: ConversationModel)

    /**
     * Обработка клика по опции восстановить чат
     * @param conversation модель чата, который собираемся восстановить
     */
    fun onRestoreChatClicked(conversation: ConversationModel)
}
package ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.chats.settings.chat_types

/**
 * Слушатель изменений типов канала в настройках.
 *
 * @author dv.baranov
 */
internal interface ChatSettingsTypeChangingListener {

    /**
     * Обработать выбор нового типа канала.
     */
    fun onChatTypeChanged(chatType: ChatSettingsTypeOptions)

    /**
     * Обработать выбор нового типа участия в канале.
     */
    fun onChatParticipationTypeChanged(chatType: ChatSettingsParticipationTypeOptions)
}

package ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.chats.settings

import ru.tensor.sbis.communicator.generated.ChatNotificationOptions
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.chats.settings.chat_types.ChatSettingsParticipationTypeOptions
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.chats.settings.chat_types.ChatSettingsTypeOptions

/** Класс для хранения основной информации о экране настроек чата */
internal class ChatSettingsInfo(
    var chatName: String?,
    var avatarUrl: String?,
    var isChatAvatarAdded: Boolean,
    var notificationOptions: ChatNotificationOptions,
    var chatType: ChatSettingsTypeOptions,
    var participationType: ChatSettingsParticipationTypeOptions,
    var savedParticipationType: ChatSettingsParticipationTypeOptions
)

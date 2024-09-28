package ru.tensor.sbis.communicator_support_channel_list.feature

import ru.tensor.sbis.consultations.generated.SupportChatsType


/**
 * Получить SupportChatsType контроллера по SupportChannelListFragmentFactoryMode
 * SupportChannelListFragmentFactoryMode - часть публичного API, используется без зависимостей от контроллера
 */
internal fun SupportComponentConfig.controllerSupportChatsType() = when (this) {
    is SupportComponentConfig.SabySupport -> SupportChatsType.SABY_SUPPORT
    is SupportComponentConfig.ClientSupport -> SupportChatsType.CLIENT_SUPPORT
    is SupportComponentConfig.SabyGet -> SupportChatsType.SABYGET
}
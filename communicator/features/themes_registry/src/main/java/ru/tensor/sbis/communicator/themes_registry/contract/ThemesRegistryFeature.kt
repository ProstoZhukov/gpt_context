package ru.tensor.sbis.communicator.themes_registry.contract

import ru.tensor.sbis.communicator.common.themes_registry.AddChatParticipantsIntentFactory
import ru.tensor.sbis.communicator.common.themes_registry.ChatSettingsIntentFactory
import ru.tensor.sbis.communicator.common.themes_registry.ConversationParticipantsFactory
import ru.tensor.sbis.communicator.declaration.host_factory.ThemesRegistryHostFragmentFactory
import ru.tensor.sbis.communicator.declaration.theme.ThemesRegistryFragmentFactory
import ru.tensor.sbis.communicator.declaration.counter.nav_counters.CommunicatorNavCounters

/**
 * API модуля реестров диалогов и чатов, описывающий предоставляемый модулем функционал
 * @see [ThemesRegistryFragmentFactory]
 * @see [AddChatParticipantsIntentFactory]
 * @see [ConversationParticipantsFactory]
 * @see [ChatSettingsIntentFactory]
 * @see [ThemesRegistryHostFragmentFactory]
 * @see [CommunicatorNavCounters.Provider]
 *
 * @author da.zhukov
 */
interface ThemesRegistryFeature :
    ThemesRegistryFragmentFactory,
    AddChatParticipantsIntentFactory,
    ConversationParticipantsFactory,
    ChatSettingsIntentFactory,
    ThemesRegistryHostFragmentFactory,
    CommunicatorNavCounters.Provider
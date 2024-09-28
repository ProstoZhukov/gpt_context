package ru.tensor.sbis.communicator.themes_registry.ui.conversationparticipants.chatparticipants.contract

import ru.tensor.sbis.communicator.themes_registry.ui.chatrecipientselection.chatAdministrators.ChatAdministratorsCommandWrapper
import ru.tensor.sbis.communicator.themes_registry.ui.themeParticipants.ThemeParticipantsCommandWrapper

/**
 * Интерактор списка участников чата
 *
 * @author rv.krohalev
 */
internal interface ChatParticipantsInteractor {

    /** @SelfDocumented */
    val themeParticipantsCommandWrapper: ThemeParticipantsCommandWrapper

    /** @SelfDocumented */
    val chatAdministratorsCommandWrapper: ChatAdministratorsCommandWrapper
}
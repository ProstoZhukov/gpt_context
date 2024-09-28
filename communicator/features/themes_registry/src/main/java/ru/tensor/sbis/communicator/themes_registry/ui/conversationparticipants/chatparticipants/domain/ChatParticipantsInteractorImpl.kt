package ru.tensor.sbis.communicator.themes_registry.ui.conversationparticipants.chatparticipants.domain

import ru.tensor.sbis.communicator.themes_registry.ui.conversationparticipants.chatparticipants.contract.ChatParticipantsInteractor
import ru.tensor.sbis.communicator.themes_registry.ui.chatrecipientselection.chatAdministrators.ChatAdministratorsCommandWrapper
import ru.tensor.sbis.communicator.themes_registry.ui.themeParticipants.ThemeParticipantsCommandWrapper
import ru.tensor.sbis.mvp.interactor.BaseInteractor

/** @SelfDocumented */
internal class ChatParticipantsInteractorImpl(override val themeParticipantsCommandWrapper: ThemeParticipantsCommandWrapper,
                                              override val chatAdministratorsCommandWrapper: ChatAdministratorsCommandWrapper) :
        BaseInteractor(),
        ChatParticipantsInteractor
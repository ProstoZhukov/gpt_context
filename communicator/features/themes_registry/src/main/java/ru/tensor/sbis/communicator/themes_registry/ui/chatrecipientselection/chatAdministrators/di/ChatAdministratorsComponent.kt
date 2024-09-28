package ru.tensor.sbis.communicator.themes_registry.ui.chatrecipientselection.chatAdministrators.di

import ru.tensor.sbis.common.data.DependencyProvider
import ru.tensor.sbis.communicator.generated.ChatAdministratorsController
import ru.tensor.sbis.communicator.generated.ChatAdministratorsFilter
import ru.tensor.sbis.communicator.generated.DataRefreshedChatAdministratorsControllerCallback
import ru.tensor.sbis.communicator.themes_registry.ui.chatrecipientselection.chatAdministrators.ChatAdministratorsCommandWrapper
import ru.tensor.sbis.communicator.themes_registry.ui.chatrecipientselection.chatAdministrators.ChatAdministratorsListFilter
import ru.tensor.sbis.communicator.themes_registry.ui.chatrecipientselection.chatAdministrators.ChatAdministratorsRepository
import ru.tensor.sbis.communicator.themes_registry.ui.themeParticipants.model.ThemeParticipant
import ru.tensor.sbis.mvp.data.model.PagedListResult
import ru.tensor.sbis.mvp.interactor.crudinterface.command.BaseListObservableCommand
import javax.inject.Named

/** @SelfDocumented */
internal interface ChatAdministratorsComponent {

    fun getChatAdministratorsListFilter(): ChatAdministratorsListFilter

    fun getChatAdministratorsController(): DependencyProvider<ChatAdministratorsController>
    fun getChatAdministratorsRepository(): ChatAdministratorsRepository
    fun getChatAdministratorsCommandWrapper(): ChatAdministratorsCommandWrapper

    @Named("ChatAdministratorsModule")
    fun getChatAdministratorsListCommand(): BaseListObservableCommand<PagedListResult<ThemeParticipant>, ChatAdministratorsFilter, DataRefreshedChatAdministratorsControllerCallback>
}

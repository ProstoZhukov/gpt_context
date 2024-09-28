package ru.tensor.sbis.communicator.themes_registry.ui.chatrecipientselection.chatAdministrators.di

import dagger.Module
import dagger.Provides
import ru.tensor.sbis.common.data.DependencyProvider
import ru.tensor.sbis.common.modelmapper.BaseModelMapper
import ru.tensor.sbis.communicator.common.util.CommunicatorActivityStatusSubscriptionInitializer
import ru.tensor.sbis.communicator.generated.ChatAdministratorsController
import ru.tensor.sbis.communicator.generated.ChatAdministratorsFilter
import ru.tensor.sbis.communicator.generated.DataRefreshedChatAdministratorsControllerCallback
import ru.tensor.sbis.communicator.generated.ListResultOfThemeParticipantMapOfStringString
import ru.tensor.sbis.communicator.themes_registry.ui.chatrecipientselection.chatAdministrators.*
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.chats.settings.adapter.viewholder.ChatSettingsItem
import ru.tensor.sbis.communicator.themes_registry.ui.themeParticipants.model.ThemeParticipant
import ru.tensor.sbis.mvp.data.model.PagedListResult
import ru.tensor.sbis.mvp.interactor.crudinterface.command.BaseListObservableCommand
import javax.inject.Named

/** @SelfDocumented */
@Suppress("unused")
@Module
internal class ChatAdministratorsModule {

    @Provides
    internal fun provideFilter(): ChatAdministratorsListFilter = ChatAdministratorsListFilter()

    @Provides
    internal fun provideController():
            DependencyProvider<ChatAdministratorsController> =
            DependencyProvider.create { ChatAdministratorsController.instance() }

    @Provides
    internal fun provideRepository(chatAdministratorsController: DependencyProvider<ChatAdministratorsController>):
            ChatAdministratorsRepository =
            ChatAdministratorsRepositoryImpl(chatAdministratorsController)

    @Provides
    internal fun provideChatAdministratorsCommandWrapper(
            repository: ChatAdministratorsRepository,
            @Named("ChatAdministratorsModule") listCommand: BaseListObservableCommand<PagedListResult<ThemeParticipant>, ChatAdministratorsFilter, DataRefreshedChatAdministratorsControllerCallback>):
            ChatAdministratorsCommandWrapper =
            ChatAdministratorsCommandWrapperImpl(
                    repository,
                    listCommand)

    @Provides
    @Named("ChatAdministratorsModule")
    internal fun provideListCommand(
        repository: ChatAdministratorsRepository,
        mapper: BaseModelMapper<ListResultOfThemeParticipantMapOfStringString, PagedListResult<ThemeParticipant>>,
        activityStatusSubscriptionsInitializer: CommunicatorActivityStatusSubscriptionInitializer
    ): BaseListObservableCommand<PagedListResult<ThemeParticipant>, ChatAdministratorsFilter, DataRefreshedChatAdministratorsControllerCallback> =
        ChatAdministratorsListCommand(repository, mapper, activityStatusSubscriptionsInitializer)

    @Provides
    internal fun provideChatAdministratorsSettingsCommandWrapper(
        repository: ChatAdministratorsRepository,
        @Named("ChatAdministratorsModule") listCommand: BaseListObservableCommand<PagedListResult<ChatSettingsItem>, ChatAdministratorsFilter, DataRefreshedChatAdministratorsControllerCallback>):
            ChatAdministratorsSettingsCommandWrapper =
        ChatAdministratorsSettingsCommandWrapperImpl(
            repository,
            listCommand)

    @Provides
    @Named("ChatAdministratorsModule")
    internal fun provideSettingsListCommand(
        repository: ChatAdministratorsRepository,
        mapper: BaseModelMapper<ListResultOfThemeParticipantMapOfStringString, PagedListResult<ChatSettingsItem>>,
        activityStatusSubscriptionsInitializer: CommunicatorActivityStatusSubscriptionInitializer
    ): BaseListObservableCommand<PagedListResult<ChatSettingsItem>, ChatAdministratorsFilter, DataRefreshedChatAdministratorsControllerCallback> =
        ChatAdministratorsSettingsListCommand(repository, mapper, activityStatusSubscriptionsInitializer)
}

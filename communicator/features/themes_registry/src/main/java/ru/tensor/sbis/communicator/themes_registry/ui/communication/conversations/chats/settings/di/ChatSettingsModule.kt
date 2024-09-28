package ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.chats.settings.di

import android.content.Context
import dagger.Module
import dagger.Provides
import ru.tensor.sbis.common.data.DependencyProvider
import ru.tensor.sbis.common.modelmapper.BaseModelMapper
import ru.tensor.sbis.common.util.NetworkUtils
import ru.tensor.sbis.common.util.uri.UriWrapper
import ru.tensor.sbis.communicator.common.crud.ThemeRepository
import ru.tensor.sbis.communicator.common.util.CommunicatorActivityStatusSubscriptionInitializer
import ru.tensor.sbis.communicator.generated.ChatController
import ru.tensor.sbis.communicator.generated.DataRefreshedThemeParticipantsControllerCallback
import ru.tensor.sbis.communicator.generated.ListResultOfThemeParticipantListItemMapOfStringString
import ru.tensor.sbis.communicator.generated.ListResultOfThemeParticipantMapOfStringString
import ru.tensor.sbis.communicator.generated.ThemeParticipantsFilter
import ru.tensor.sbis.communicator.themes_registry.ThemesRegistryFacade.themesRegistryDependency
import ru.tensor.sbis.communicator.themes_registry.data.mapper.ChatParticipantMapper
import ru.tensor.sbis.communicator.themes_registry.ui.chatrecipientselection.chatAdministrators.ChatAdministratorsListFilter
import ru.tensor.sbis.communicator.themes_registry.ui.chatrecipientselection.chatAdministrators.ChatAdministratorsSettingsCommandWrapper
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.chats.settings.ChatSettingsAdminsPresenter
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.chats.settings.ChatSettingsCommandWrapper
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.chats.settings.ChatSettingsCommandWrapperImpl
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.chats.settings.ChatSettingsContract
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.chats.settings.ChatSettingsInteractor
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.chats.settings.ChatSettingsListCommand
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.chats.settings.ChatSettingsListListItemMapper
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.chats.settings.ChatSettingsListMapper
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.chats.settings.ChatSettingsParticipantsPresenter
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.chats.settings.adapter.viewholder.ChatSettingsItem
import ru.tensor.sbis.communicator.themes_registry.ui.themeParticipants.ThemeParticipantsListFilter
import ru.tensor.sbis.communicator.themes_registry.ui.themeParticipants.ThemeParticipantsRepository
import ru.tensor.sbis.mvp.data.model.PagedListResult
import ru.tensor.sbis.mvp.interactor.crudinterface.command.BaseListObservableCommand
import ru.tensor.sbis.mvp.interactor.crudinterface.event.DefaultEventManagerServiceSubscriber
import ru.tensor.sbis.mvp.interactor.crudinterface.event.EventManagerServiceSubscriber
import ru.tensor.sbis.mvp.interactor.crudinterface.subscribing.SubscriptionManager
import ru.tensor.sbis.profile_service.controller.employee_profile.EmployeeProfileControllerWrapper
import java.util.*
import javax.inject.Named

/**
 * DI модуль экрана настроек чата.
 *
 * @author da.zhukov
 */
@Suppress("unused")
@Module
internal class ChatSettingsModule {

    @Provides
    @ChatSettingsScope
    internal fun provideChatSettingsPresenter(
        interactor: ChatSettingsInteractor,
        uriWrapper: UriWrapper,
        @Named("newChat") newChat: Boolean,
        @Named("chatUuid") uuid: UUID?,
        @Named("draftChat") draftChat: Boolean,
        themeParticipantsListFilter: ThemeParticipantsListFilter,
        chatAdministratorsListFilter: ChatAdministratorsListFilter,
        subscriptionManager: SubscriptionManager,
        networkUtils: NetworkUtils
    ): ChatSettingsContract.Presenter {
        if (newChat) {
            return ChatSettingsParticipantsPresenter(
                interactor,
                uriWrapper,
                themesRegistryDependency.getRecipientSelectionResultManager(),
                newChat,
                uuid,
                draftChat,
                themeParticipantsListFilter,
                subscriptionManager,
                networkUtils,
            )
        } else {
            return ChatSettingsAdminsPresenter(
                interactor,
                uriWrapper,
                themesRegistryDependency.getRecipientSelectionResultManager(),
                newChat,
                uuid!!,
                draftChat,
                chatAdministratorsListFilter,
                subscriptionManager,
                networkUtils
            )
        }
    }

    @Provides
    @ChatSettingsScope
    internal fun provideChatSettingsInteractor(
        chatControllerDependencyProvider: DependencyProvider<ChatController>,
        employeeProfileControllerWrapperProvider: DependencyProvider<EmployeeProfileControllerWrapper>,
        themeRepository: ThemeRepository,
        chatSettingsCommandWrapper: ChatSettingsCommandWrapper,
        chatAdministratorsCommandWrapper: ChatAdministratorsSettingsCommandWrapper,
        chatParticipantMapper: ChatParticipantMapper,
        context: Context,
        activityStatusSubscriptionsInitializer: CommunicatorActivityStatusSubscriptionInitializer
    ): ChatSettingsInteractor {
        return ChatSettingsInteractor(
            chatControllerDependencyProvider,
            employeeProfileControllerWrapperProvider,
            themeRepository,
            chatSettingsCommandWrapper,
            chatAdministratorsCommandWrapper,
            chatParticipantMapper,
            context,
            activityStatusSubscriptionsInitializer
        )
    }

    @Provides
    @ChatSettingsScope
    internal fun provideChatParticipantModelMapper(context: Context): ChatParticipantMapper =
        ChatParticipantMapper(context)

    @ChatSettingsScope
    @Provides
    internal fun provideEventManagerSubscriber(context: Context): EventManagerServiceSubscriber =
        DefaultEventManagerServiceSubscriber(context)

    @ChatSettingsScope
    @Provides
    internal fun provideSubscriptionManager(eventManagerServiceSubscriber: EventManagerServiceSubscriber): SubscriptionManager =
        SubscriptionManager(eventManagerServiceSubscriber)

    @ChatSettingsScope
    @Provides
    internal fun provideChatSettingsCommandWrapper(
        repository: ThemeParticipantsRepository,
        listCommand: BaseListObservableCommand<PagedListResult<ChatSettingsItem>, ThemeParticipantsFilter, DataRefreshedThemeParticipantsControllerCallback>,
    ): ChatSettingsCommandWrapper = ChatSettingsCommandWrapperImpl(repository, listCommand)

    @ChatSettingsScope
    @Provides
    internal fun provideListCommand(
        repository: ThemeParticipantsRepository,
        mapper: BaseModelMapper<ListResultOfThemeParticipantListItemMapOfStringString, PagedListResult<ChatSettingsItem>>,
        activityStatusSubscriptionsInitializer: CommunicatorActivityStatusSubscriptionInitializer,
    ): BaseListObservableCommand<PagedListResult<ChatSettingsItem>, ThemeParticipantsFilter, DataRefreshedThemeParticipantsControllerCallback> =
        ChatSettingsListCommand(repository, mapper, activityStatusSubscriptionsInitializer)

    @ChatSettingsScope
    @Provides
    internal fun provideListMapper(context: Context): BaseModelMapper<ListResultOfThemeParticipantMapOfStringString, PagedListResult<ChatSettingsItem>> =
        ChatSettingsListMapper(context)

    @ChatSettingsScope
    @Provides
    internal fun provideListListItemMapper(context: Context): BaseModelMapper<ListResultOfThemeParticipantListItemMapOfStringString, PagedListResult<ChatSettingsItem>> =
        ChatSettingsListListItemMapper(context)
}

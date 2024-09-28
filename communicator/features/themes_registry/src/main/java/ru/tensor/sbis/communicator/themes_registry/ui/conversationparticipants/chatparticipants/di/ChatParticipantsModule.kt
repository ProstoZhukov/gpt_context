package ru.tensor.sbis.communicator.themes_registry.ui.conversationparticipants.chatparticipants.di

import android.content.Context
import dagger.Module
import dagger.Provides
import ru.tensor.sbis.mvp.interactor.crudinterface.event.DefaultEventManagerServiceSubscriber
import ru.tensor.sbis.mvp.interactor.crudinterface.event.EventManagerServiceSubscriber
import ru.tensor.sbis.mvp.interactor.crudinterface.subscribing.SubscriptionManager
import ru.tensor.sbis.common.util.NetworkUtils
import ru.tensor.sbis.common.util.scroll.ScrollHelper
import ru.tensor.sbis.communicator.common.data.model.NetworkAvailability
import ru.tensor.sbis.communicator.themes_registry.ThemesRegistryFacade.themesRegistryDependency
import ru.tensor.sbis.communicator.generated.Permissions
import ru.tensor.sbis.communicator.themes_registry.ui.conversationparticipants.adapter.ThemeParticipantsAdapter
import ru.tensor.sbis.communicator.themes_registry.ui.conversationparticipants.chatparticipants.contract.ChatParticipantsInteractor
import ru.tensor.sbis.communicator.themes_registry.ui.conversationparticipants.chatparticipants.contract.ChatParticipantsViewContract
import ru.tensor.sbis.communicator.themes_registry.ui.conversationparticipants.chatparticipants.domain.ChatParticipantsInteractorImpl
import ru.tensor.sbis.communicator.themes_registry.ui.conversationparticipants.chatparticipants.presentation.viewmodel.ChatParticipantsViewModel
import ru.tensor.sbis.communicator.themes_registry.ui.conversationparticipants.chatparticipants.presentation.presenter.ChatParticipantsPresenter
import ru.tensor.sbis.communicator.themes_registry.ui.chatrecipientselection.chatAdministrators.ChatAdministratorsCommandWrapper
import ru.tensor.sbis.communicator.themes_registry.ui.themeParticipants.ThemeParticipantsCommandWrapper
import ru.tensor.sbis.communicator.themes_registry.ui.themeParticipants.ThemeParticipantsListFilter
import java.util.*

/** @SelfDocumented */
@Suppress("unused")
@Module
internal class ChatParticipantsModule {

    @ChatParticipantsScope
    @Provides
    internal fun provideEventManagerSubscriber(context: Context): EventManagerServiceSubscriber =
            DefaultEventManagerServiceSubscriber(context)

    @ChatParticipantsScope
    @Provides
    internal fun provideSubscriptionManager(eventManagerServiceSubscriber: EventManagerServiceSubscriber): SubscriptionManager =
            SubscriptionManager(eventManagerServiceSubscriber)

    @ChatParticipantsScope
    @Provides
    internal fun providePresenter(conversationUuid: UUID,
                                  chatPermissions: Permissions,
                                  viewModel: ChatParticipantsViewModel,
                                  interactor: ChatParticipantsInteractor,
                                  filter: ThemeParticipantsListFilter,
                                  subscriptionManager: SubscriptionManager,
                                  networkAvailability: NetworkAvailability,
                                  networkUtils: NetworkUtils,
                                  scrollHelper: ScrollHelper):
            ChatParticipantsViewContract.Presenter =
            ChatParticipantsPresenter(
                conversationUuid,
                chatPermissions,
                viewModel,
                interactor,
                filter,
                themesRegistryDependency.getRecipientSelectionResultManager(),
                networkAvailability,
                subscriptionManager,
                networkUtils,
                scrollHelper)

    @ChatParticipantsScope
    @Provides
    internal fun provideNetworkAvailability(): NetworkAvailability =
        NetworkAvailability()

    @ChatParticipantsScope
    @Provides
    internal fun provideInteractor(
        themeParticipantsCommandWrapper: ThemeParticipantsCommandWrapper,
        chatAdministratorsCommandWrapper: ChatAdministratorsCommandWrapper
    ): ChatParticipantsInteractor =
        ChatParticipantsInteractorImpl(
            themeParticipantsCommandWrapper,
            chatAdministratorsCommandWrapper
        )

    @Provides
    @ChatParticipantsScope
    fun provideAdapter(): ThemeParticipantsAdapter = ThemeParticipantsAdapter()
}
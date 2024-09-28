package ru.tensor.sbis.communicator.themes_registry.ui.conversationparticipants.dialogparticipants.di

import android.content.Context
import dagger.Module
import dagger.Provides
import ru.tensor.sbis.common.data.DependencyProvider
import ru.tensor.sbis.common.util.NetworkUtils
import ru.tensor.sbis.common.util.scroll.ScrollHelper
import ru.tensor.sbis.communicator.common.util.CommunicatorActivityStatusSubscriptionInitializer
import ru.tensor.sbis.communicator.generated.DialogController
import ru.tensor.sbis.communicator.themes_registry.ThemesRegistryFacade.themesRegistryDependency
import ru.tensor.sbis.communicator.themes_registry.ui.conversationparticipants.adapter.ThemeParticipantsAdapter
import ru.tensor.sbis.communicator.themes_registry.ui.conversationparticipants.dialogparticipants.contract.DialogParticipantsInteractor
import ru.tensor.sbis.communicator.themes_registry.ui.conversationparticipants.dialogparticipants.contract.DialogParticipantsViewContract
import ru.tensor.sbis.communicator.themes_registry.ui.conversationparticipants.dialogparticipants.domain.DialogParticipantsInteractorImpl
import ru.tensor.sbis.communicator.themes_registry.ui.conversationparticipants.dialogparticipants.presentation.presenter.DialogParticipantsPresenter
import ru.tensor.sbis.communicator.themes_registry.ui.themeParticipants.ThemeParticipantsCommandWrapper
import ru.tensor.sbis.communicator.themes_registry.ui.themeParticipants.ThemeParticipantsListFilter
import ru.tensor.sbis.mvp.interactor.crudinterface.event.DefaultEventManagerServiceSubscriber
import ru.tensor.sbis.mvp.interactor.crudinterface.event.EventManagerServiceSubscriber
import ru.tensor.sbis.mvp.interactor.crudinterface.subscribing.SubscriptionManager
import java.util.*
import javax.inject.Named

/** @SelfDocumented */
@Suppress("unused")
@Module
internal class DialogParticipantsModule {

    @DialogParticipantsScope
    @Provides
    internal fun provideEventManagerSubscriber(context: Context): EventManagerServiceSubscriber =
        DefaultEventManagerServiceSubscriber(context)

    @DialogParticipantsScope
    @Provides
    internal fun provideSubscriptionManager(
        eventManagerServiceSubscriber: EventManagerServiceSubscriber
    ): SubscriptionManager =
        SubscriptionManager(eventManagerServiceSubscriber)

    @DialogParticipantsScope
    @Provides
    internal fun providePresenter(
        conversationUuid: UUID,
        @Named("isNewDialog") isNewDialog: Boolean,
        @Named("isFromCollage") isFromCollage: Boolean,
        participantsUuids: ArrayList<UUID>?,
        interactor: DialogParticipantsInteractor,
        filter: ThemeParticipantsListFilter,
        subscriptionManager: SubscriptionManager,
        networkUtils: NetworkUtils,
        scrollHelper: ScrollHelper,
    ): DialogParticipantsViewContract.Presenter =
        DialogParticipantsPresenter(
            conversationUuid,
            isNewDialog,
            isFromCollage,
            participantsUuids,
            interactor,
            filter,
            themesRegistryDependency.getRecipientSelectionResultManager(),
            subscriptionManager,
            networkUtils,
            scrollHelper
        )

    @DialogParticipantsScope
    @Provides
    internal fun provideInteractor(
        themeParticipantsCommandWrapper: ThemeParticipantsCommandWrapper,
        dialogController: DependencyProvider<DialogController>,
        activityStatusSubscriptionsInitializer: CommunicatorActivityStatusSubscriptionInitializer
    ): DialogParticipantsInteractor =
        DialogParticipantsInteractorImpl(
            themeParticipantsCommandWrapper,
            dialogController,
            themesRegistryDependency.employeeProfileControllerWrapper,
            activityStatusSubscriptionsInitializer
        )

    @Provides
    @DialogParticipantsScope
    fun provideAdapter(): ThemeParticipantsAdapter =
        ThemeParticipantsAdapter()
}
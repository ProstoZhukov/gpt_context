package ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.dialogs.information.di

import android.content.Context
import dagger.Module
import dagger.Provides
import ru.tensor.sbis.common.data.DependencyProvider
import ru.tensor.sbis.common.util.NetworkUtils
import ru.tensor.sbis.communicator.common.util.CommunicatorActivityStatusSubscriptionInitializer
import ru.tensor.sbis.communicator.generated.DialogController
import ru.tensor.sbis.communicator.themes_registry.ThemesRegistryFacade.themesRegistryDependency
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.dialogs.information.DialogInformationContract
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.dialogs.information.DialogInformationInteractor
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.dialogs.information.DialogInformationInteractorImpl
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.dialogs.information.DialogInformationPresenter
import ru.tensor.sbis.communicator.themes_registry.ui.themeParticipants.ThemeParticipantsCommandWrapper
import ru.tensor.sbis.communicator.themes_registry.ui.themeParticipants.ThemeParticipantsListFilter
import ru.tensor.sbis.mvp.interactor.crudinterface.event.DefaultEventManagerServiceSubscriber
import ru.tensor.sbis.mvp.interactor.crudinterface.event.EventManagerServiceSubscriber
import ru.tensor.sbis.mvp.interactor.crudinterface.subscribing.SubscriptionManager
import java.util.*
import javax.inject.Named

/**
 * Модуль экрана информации о диалоге
 *
 * @author da.zhukov
 */
@Module
internal class DialogInformationModule {

    @DialogInformationScope
    @Provides
    internal fun provideEventManagerSubscriber(context: Context): EventManagerServiceSubscriber =
        DefaultEventManagerServiceSubscriber(context)

    @DialogInformationScope
    @Provides
    internal fun provideSubscriptionManager(
        eventManagerServiceSubscriber: EventManagerServiceSubscriber
    ): SubscriptionManager =
        SubscriptionManager(eventManagerServiceSubscriber)

    @DialogInformationScope
    @Provides
    internal fun providePresenter(
        @Named(CONVERSATION_NAME) conversationName: String,
        @Named("isNewDialog") isNewDialog: Boolean,
        conversationUuid: UUID,
        participantsUuids: ArrayList<UUID>?,
        interactor: DialogInformationInteractor,
        filter: ThemeParticipantsListFilter,
        subscriptionManager: SubscriptionManager,
        networkUtils: NetworkUtils
    ): DialogInformationContract.Presenter =
        DialogInformationPresenter(
            conversationName,
            isNewDialog,
            conversationUuid,
            participantsUuids,
            interactor,
            filter,
            themesRegistryDependency.getRecipientSelectionResultManager(),
            subscriptionManager,
            networkUtils
        )

    @DialogInformationScope
    @Provides
    internal fun provideInteractor(
        themeParticipantsCommandWrapper: ThemeParticipantsCommandWrapper,
        dialogControllerProvider: DependencyProvider<DialogController>,
        activityStatusSubscriptionsInitializer: CommunicatorActivityStatusSubscriptionInitializer
    ): DialogInformationInteractor =
        DialogInformationInteractorImpl(
            themeParticipantsCommandWrapper,
            dialogControllerProvider,
            themesRegistryDependency.employeeProfileControllerWrapper,
            activityStatusSubscriptionsInitializer
        )
}
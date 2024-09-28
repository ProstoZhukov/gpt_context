package ru.tensor.sbis.communicator.crm.conversation.di

import androidx.lifecycle.ViewModelStoreOwner
import dagger.Binds
import dagger.Module
import dagger.Provides
import ru.tensor.sbis.common.data.DependencyProvider
import ru.tensor.sbis.common.lifecycle.AppLifecycleTracker
import ru.tensor.sbis.common.modelmapper.BaseModelMapper
import ru.tensor.sbis.common.util.ClipboardManager
import ru.tensor.sbis.communicator.base.conversation.presentation.presenter.MessageCollectionFilter
import ru.tensor.sbis.communicator.base.conversation.presentation.crud.ConversationCollectionStorage
import ru.tensor.sbis.communicator.base.conversation.presentation.crud.ConversationComponentVM
import ru.tensor.sbis.communicator.base.conversation.presentation.crud.ConversationListComponent
import ru.tensor.sbis.communicator.base.conversation.presentation.crud.createConversationComponentVM
import ru.tensor.sbis.communicator.common.conversation.crud.MessageControllerBinaryMapper
import ru.tensor.sbis.communicator.common.conversation.data.ListResultOfMessageMapOfStringString
import ru.tensor.sbis.communicator.common.push.MessagesPushManager
import ru.tensor.sbis.communicator.crm.conversation.CRMConversationPlugin.communicatorPushKeyboardHelperProvider
import ru.tensor.sbis.communicator.crm.conversation.contract.CRMConversationDependency
import ru.tensor.sbis.communicator.crm.conversation.presentation.ui.CRMConversationContract.CRMConversationPresenterContract
import ru.tensor.sbis.communicator.crm.conversation.presentation.ui.CRMConversationContract.CRMConversationViewContract
import ru.tensor.sbis.communicator.crm.conversation.presentation.ui.CRMConversationPresenter
import ru.tensor.sbis.communicator.crm.conversation.presentation.ui.CRMConversationStubViewContentFactory
import ru.tensor.sbis.communicator.crm.conversation.presentation.ui.CRMMessageCollectionFilter
import ru.tensor.sbis.communicator.crm.conversation.presentation.presenter.message.CRMConversationMessagesPresenter
import ru.tensor.sbis.communicator.crm.conversation.presentation.presenter.message.CRMConversationMessagesPresenterContract
import ru.tensor.sbis.communicator.crm.conversation.data.CRMCoreConversationInfo
import ru.tensor.sbis.communicator.crm.conversation.data.mapper.CRMMessageListMapper
import ru.tensor.sbis.communicator.crm.conversation.data.mapper.CRMMessageMapper
import ru.tensor.sbis.communicator.crm.conversation.data.model.CRMConversationMessage
import ru.tensor.sbis.communicator.crm.conversation.interactor.CRMConversationInteractorImpl
import ru.tensor.sbis.communicator.crm.conversation.interactor.contract.CRMConversationInteractor
import ru.tensor.sbis.communicator.crm.conversation.interactor.crud.CRMConversationRepository
import ru.tensor.sbis.communicator.crm.conversation.presentation.presenter.contracts.CRMConversationMessagePanelPresenterContract
import ru.tensor.sbis.communicator.crm.conversation.presentation.presenter.contracts.CRMConversationToolbarPresenterContract
import ru.tensor.sbis.communicator.crm.conversation.presentation.presenter.dispatcher.CRMConversationDataDispatcher
import ru.tensor.sbis.communicator.crm.conversation.presentation.presenter.message_panel.CRMConversationMessagePanelPresenter
import ru.tensor.sbis.communicator.crm.conversation.presentation.presenter.toolbar.CRMConversationToolbarPresenter
import ru.tensor.sbis.communicator.crm.conversation.presentation.ui.viewmodel.CRMConversationViewModel
import ru.tensor.sbis.communicator.crm.conversation.router.CRMConversationRouter
import ru.tensor.sbis.communicator.crm.conversation.router.CRMConversationRouterImpl
import ru.tensor.sbis.communicator.generated.Message
import ru.tensor.sbis.communicator.generated.MessageCollectionStorageProvider
import ru.tensor.sbis.communicator.generated.MessageController
import ru.tensor.sbis.consultations.generated.ConsultationService
import ru.tensor.sbis.consultations.generated.QuickReplyCollectionProvider
import ru.tensor.sbis.design.list_header.ListDateViewUpdater
import ru.tensor.sbis.design.list_header.format.ListDateFormatter
import ru.tensor.sbis.localfeaturetoggle.domain.LocalFeatureToggleService
import ru.tensor.sbis.mvp.data.model.PagedListResult
import ru.tensor.sbis.mvp.interactor.crudinterface.command.CreateCommand
import ru.tensor.sbis.mvp.interactor.crudinterface.command.CreateObservableCommand
import ru.tensor.sbis.mvp.interactor.crudinterface.command.DeleteCommand
import ru.tensor.sbis.mvp.interactor.crudinterface.command.DeleteObservableCommand
import ru.tensor.sbis.mvp.interactor.crudinterface.command.ReadCommand
import ru.tensor.sbis.mvp.interactor.crudinterface.command.ReadObservableCommand
import ru.tensor.sbis.mvp.interactor.crudinterface.command.UpdateCommand
import ru.tensor.sbis.mvp.interactor.crudinterface.command.UpdateObservableCommand
import ru.tensor.sbis.plugin_struct.utils.SbisThemedContext

/**
 * DI модуль компонента сабмодуля сообщений CRM.
 *
 * @author da.zhukov
 */
@Module(includes = [CRMConversationModule.BindsDIModule::class])
internal class CRMConversationModule {
    @Provides
    @CRMConversationScope
    fun provideConversationPresenter(
        messagesPresenter: CRMConversationMessagesPresenterContract<CRMConversationViewContract>,
        panelPresenter: CRMConversationMessagePanelPresenterContract<CRMConversationViewContract>,
        toolbarPresenter: CRMConversationToolbarPresenterContract<CRMConversationViewContract>
    ): CRMConversationPresenterContract {
        return CRMConversationPresenter(messagesPresenter, panelPresenter, toolbarPresenter)
    }

    @Provides
    @CRMConversationScope
    @Suppress("UNCHECKED_CAST")
    fun provideCRMConversationMessagesPresenter(
        collectionComponent: ConversationListComponent<CRMConversationMessage>,
        interactor: CRMConversationInteractor,
        coreConversationInfo: CRMCoreConversationInfo,
        dataDispatcher: CRMConversationDataDispatcher,
        clipboardManager: ClipboardManager,
        collectionFilter: MessageCollectionFilter,
        router: CRMConversationRouter,
        messagesPushManager: MessagesPushManager,
        appLifecycleTracker: AppLifecycleTracker,
        viewModel: CRMConversationViewModel,
        featureToggleService: LocalFeatureToggleService,
    ): CRMConversationMessagesPresenterContract<CRMConversationViewContract> =
        CRMConversationMessagesPresenter(
            collectionComponent,
            interactor,
            coreConversationInfo,
            dataDispatcher,
            clipboardManager,
            collectionFilter,
            router,
            messagesPushManager,
            appLifecycleTracker,
            viewModel,
            featureToggleService,
            communicatorPushKeyboardHelperProvider.get().getCommunicatorPushKeyboardHelper()
        ) as CRMConversationMessagesPresenterContract<CRMConversationViewContract>

    @Provides
    @CRMConversationScope
    @Suppress("UNCHECKED_CAST")
    fun provideConversationComponentVM(
        viewModelStoreOwner: ViewModelStoreOwner,
        mapper: CRMMessageMapper,
        coreConversationInfo: CRMCoreConversationInfo
    ): ConversationComponentVM<CRMConversationMessage> =
        createConversationComponentVM(
            viewModelStoreOwner,
            mapper,
            CRMConversationStubViewContentFactory(coreConversationInfo.crmConsultationCase)
        )

    @Provides
    @CRMConversationScope
    fun provideConversationCollectionStorage(
        storageProvider: DependencyProvider<MessageCollectionStorageProvider>
    ): ConversationCollectionStorage =
        ConversationCollectionStorage(storageProvider)

    @Provides
    @CRMConversationScope
    fun provideConversationComponentWrapper(
        listComponent: ConversationComponentVM<CRMConversationMessage>,
        storage: ConversationCollectionStorage
    ): ConversationListComponent<CRMConversationMessage> =
        ConversationListComponent(listComponent, storage)

    @Provides
    @CRMConversationScope
    fun provideCRMConversationMessagePanelPresenter(
        interactor: CRMConversationInteractor,
        coreConversationInfo: CRMCoreConversationInfo,
        dataDispatcher: CRMConversationDataDispatcher,
        viewModel: CRMConversationViewModel
    ): CRMConversationMessagePanelPresenterContract<CRMConversationViewContract> {
        @Suppress("UNCHECKED_CAST")
        return CRMConversationMessagePanelPresenter(
            interactor,
            coreConversationInfo,
            dataDispatcher,
            viewModel
        ) as CRMConversationMessagePanelPresenterContract<CRMConversationViewContract>
    }

    @Provides
    @CRMConversationScope
    fun provideCRMConversationToolbarPresenter(
        interactor: CRMConversationInteractor,
        coreConversationInfo: CRMCoreConversationInfo,
        dataDispatcher: CRMConversationDataDispatcher,
        viewModel: CRMConversationViewModel,
        router: CRMConversationRouter
    ): CRMConversationToolbarPresenterContract<CRMConversationViewContract> {
        @Suppress("UNCHECKED_CAST")
        return CRMConversationToolbarPresenter(
            interactor,
            coreConversationInfo,
            dataDispatcher,
            viewModel,
            router
        ) as CRMConversationToolbarPresenterContract<CRMConversationViewContract>
    }

    @Provides
    @CRMConversationScope
    internal fun provideConsultationService(): DependencyProvider<ConsultationService> =
        DependencyProvider.create { ConsultationService.instance() }

    @Provides
    @CRMConversationScope
    fun provideCRMConversationRepository(
        controller: DependencyProvider<MessageController>,
        messageControllerBinaryMapper: MessageControllerBinaryMapper
    ): CRMConversationRepository =
        CRMConversationRepository(controller, messageControllerBinaryMapper)

    @Provides
    @CRMConversationScope
    fun provideMessageControllerBinaryMapper(): MessageControllerBinaryMapper =
        MessageControllerBinaryMapper()

    @Provides
    @CRMConversationScope
    fun provideCreateCommand(repository: CRMConversationRepository): CreateObservableCommand<Message> =
        CreateCommand(repository)

    @Provides
    @CRMConversationScope
    fun provideReadCommand(
        repository: CRMConversationRepository,
        mapper: CRMMessageMapper
    ): ReadObservableCommand<CRMConversationMessage> =
        ReadCommand(repository, mapper)

    @Provides
    @CRMConversationScope
    fun provideUpdateCommand(repository: CRMConversationRepository): UpdateObservableCommand<Message> =
        UpdateCommand(repository)

    @Provides
    @CRMConversationScope
    fun provideDeleteCommand(repository: CRMConversationRepository): DeleteObservableCommand =
        DeleteCommand(repository)

    @Provides
    @CRMConversationScope
    fun provideCRMConversationDataDispatcher(): CRMConversationDataDispatcher =
        CRMConversationDataDispatcher()

    @Provides
    @CRMConversationScope
    fun provideListDateViewUpdater(
        dateTimeWithTodayFormatter: ListDateFormatter.DateTimeWithTodayStandard
    ): ListDateViewUpdater =
        ListDateViewUpdater(dateTimeWithTodayFormatter)

    @Provides
    @CRMConversationScope
    fun provideQuickReplyCollectionProvider(): DependencyProvider<QuickReplyCollectionProvider> =
        DependencyProvider.create(QuickReplyCollectionProvider::instance)

    @Provides
    @CRMConversationScope
    fun provideMessageViewPool(
        dependency: CRMConversationDependency,
        context: SbisThemedContext,
    ) = dependency.messageViewComponentsFactory.createMessageViewPool(context)

    @Provides
    @CRMConversationScope
    fun provideMessageCollectionFilter(): MessageCollectionFilter = CRMMessageCollectionFilter()

    @Module
    interface BindsDIModule {

        @Binds
        @CRMConversationScope
        fun asCRMConversationInteractor(interactor: CRMConversationInteractorImpl): CRMConversationInteractor


        @Binds
        @CRMConversationScope
        fun asBaseModelMapper(mapper: CRMMessageListMapper): BaseModelMapper<ListResultOfMessageMapOfStringString, PagedListResult<CRMConversationMessage>>

        @Binds
        @CRMConversationScope
        fun asCRMConversationRouter(router: CRMConversationRouterImpl): CRMConversationRouter
    }
}

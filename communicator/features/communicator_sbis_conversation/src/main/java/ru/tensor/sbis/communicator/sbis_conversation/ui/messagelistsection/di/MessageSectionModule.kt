package ru.tensor.sbis.communicator.sbis_conversation.ui.messagelistsection.di

import android.content.Context
import androidx.lifecycle.ViewModelStoreOwner
import dagger.Module
import dagger.Provides
import ru.tensor.sbis.common.data.DependencyProvider
import ru.tensor.sbis.common.lifecycle.AppLifecycleTracker
import ru.tensor.sbis.common.modelmapper.BaseModelMapper
import ru.tensor.sbis.common.util.ClipboardManager
import ru.tensor.sbis.common.util.ResourceProvider
import ru.tensor.sbis.common.util.UUIDUtils
import ru.tensor.sbis.communication_decl.communicator.media.MediaPlayer
import ru.tensor.sbis.communicator.base.conversation.presentation.crud.ConversationCollectionStorage
import ru.tensor.sbis.communicator.base.conversation.presentation.crud.ConversationListComponent
import ru.tensor.sbis.communicator.base.conversation.presentation.crud.ConversationStubViewContentFactory
import ru.tensor.sbis.communicator.base.conversation.presentation.crud.createConversationComponentVM
import ru.tensor.sbis.communicator.base.conversation.presentation.presenter.MessageCollectionFilter
import ru.tensor.sbis.communicator.common.conversation.ConversationEventsPublisher
import ru.tensor.sbis.communicator.common.conversation.ConversationPrefetchManager
import ru.tensor.sbis.communicator.common.conversation.ConversationToolbarEventManager
import ru.tensor.sbis.communicator.common.conversation.crud.MessageControllerBinaryMapper
import ru.tensor.sbis.communicator.common.conversation.data.ListResultOfMessageMapOfStringString
import ru.tensor.sbis.communicator.common.crud.ThemeRepository
import ru.tensor.sbis.communicator.common.push.MessagesPushManager
import ru.tensor.sbis.communicator.common.util.CommunicatorActivityStatusSubscriptionInitializer
import ru.tensor.sbis.communicator.common.util.PersonAvatarPrefetchHelper
import ru.tensor.sbis.communicator.common.util.share.quick_share.QuickShareHelper
import ru.tensor.sbis.communicator.generated.ChatController
import ru.tensor.sbis.communicator.generated.DecoratedOfMessage
import ru.tensor.sbis.communicator.generated.DialogController
import ru.tensor.sbis.communicator.generated.DialogDocumentController
import ru.tensor.sbis.communicator.generated.HierarchyCollectionOfMessage
import ru.tensor.sbis.communicator.generated.MessageCollectionStorageProvider
import ru.tensor.sbis.communicator.generated.MessageController
import ru.tensor.sbis.communicator.generated.PathModelOfMessageMapOfStringString
import ru.tensor.sbis.communicator.generated.ThemeParticipantsController
import ru.tensor.sbis.communicator.generated.TupleOfUuidOptionalOfBool
import ru.tensor.sbis.communicator.sbis_conversation.CommunicatorSbisConversationPlugin.communicatorPushKeyboardHelperProvider
import ru.tensor.sbis.communicator.sbis_conversation.CommunicatorSbisConversationPlugin.communicatorSbisConversationDependency
import ru.tensor.sbis.communicator.sbis_conversation.adapters.MessagesListAdapter
import ru.tensor.sbis.communicator.sbis_conversation.contract.CommunicatorSbisConversationDependency
import ru.tensor.sbis.communicator.sbis_conversation.data.CoreConversationInfo
import ru.tensor.sbis.communicator.sbis_conversation.data.mapper.ConversationDataMapper
import ru.tensor.sbis.communicator.sbis_conversation.data.mapper.DocumentMapper
import ru.tensor.sbis.communicator.sbis_conversation.data.mapper.MessageMapper
import ru.tensor.sbis.communicator.sbis_conversation.data.model.ConversationMessage
import ru.tensor.sbis.communicator.sbis_conversation.interactor.ConversationInteractor
import ru.tensor.sbis.communicator.sbis_conversation.interactor.ConversationInteractorImpl
import ru.tensor.sbis.communicator.sbis_conversation.interactor.data.ConversationDataInteractor
import ru.tensor.sbis.communicator.sbis_conversation.interactor.data.ConversationDataInteractorImpl
import ru.tensor.sbis.communicator.sbis_conversation.ui.ConversationDataDispatcher
import ru.tensor.sbis.communicator.sbis_conversation.ui.crud.ConversationFilter
import ru.tensor.sbis.communicator.sbis_conversation.ui.crud.ConversationRepository
import ru.tensor.sbis.communicator.sbis_conversation.ui.crud.MessageListMapper
import ru.tensor.sbis.communicator.sbis_conversation.ui.message.ConversationMessagesContract
import ru.tensor.sbis.communicator.sbis_conversation.ui.message.ConversationMessagesPresenter
import ru.tensor.sbis.communicator.sbis_conversation.ui.message.delegates.ConversationActionDelegate
import ru.tensor.sbis.communicator.sbis_conversation.ui.message.delegates.ConversationMediaActionDelegate
import ru.tensor.sbis.communicator.sbis_conversation.ui.message.delegates.ConversationPhoneNumberDelegate
import ru.tensor.sbis.communicator.sbis_conversation.ui.message.delegates.ConversationSenderActionListenerDelegate
import ru.tensor.sbis.communicator.sbis_conversation.ui.message.delegates.ConversationSingAndAcceptActionDelegate
import ru.tensor.sbis.communicator.sbis_conversation.ui.message.delegates.ConversationThreadActionDelegate
import ru.tensor.sbis.communicator.sbis_conversation.ui.messagepanel.ConversationMessagePanelContract
import ru.tensor.sbis.communicator.sbis_conversation.ui.messagepanel.ConversationMessagePanelPresenter
import ru.tensor.sbis.communicator.sbis_conversation.ui.toolbar.ConversationToolbarContract
import ru.tensor.sbis.communicator.sbis_conversation.ui.toolbar.ConversationToolbarPresenterImpl
import ru.tensor.sbis.communicator.sbis_conversation.ui.viewmodel.ConversationViewModel
import ru.tensor.sbis.communicator.sbis_conversation.utils.ConversationViewPoolController
import ru.tensor.sbis.crud4.ComponentViewModel
import ru.tensor.sbis.crud4.view.StubFactory
import ru.tensor.sbis.design.list_header.ListDateViewUpdater
import ru.tensor.sbis.design.list_header.format.ListDateFormatter
import ru.tensor.sbis.feature_ctrl.SbisFeatureService
import ru.tensor.sbis.localfeaturetoggle.domain.LocalFeatureToggleService
import ru.tensor.sbis.mvp.data.model.PagedListResult
import ru.tensor.sbis.mvp.interactor.crudinterface.event.DefaultEventManagerServiceSubscriber
import ru.tensor.sbis.mvp.interactor.crudinterface.event.EventManagerServiceSubscriber
import ru.tensor.sbis.mvp.interactor.crudinterface.subscribing.SubscriptionManager
import ru.tensor.sbis.profiles.generated.PersonController
import javax.inject.Named

/**
 * Di модуль секции сообщений.
 *
 * @author vv.chekurda
 */
@Suppress("unused")
@Module
internal class MessageSectionModule {

    @Provides
    @MessageSectionScope
    fun provideDocumentMapper(context: Context): DocumentMapper {
        return DocumentMapper(context)
    }

    @Provides
    @MessageSectionScope
    fun provideMessageMapper(context: Context): MessageMapper {
        return MessageMapper(context)
    }

    @Provides
    @MessageSectionScope
    fun provideConversationDataMapper(
        context: Context,
        documentMapper: DocumentMapper,
        messageMapper: MessageMapper,
        dateTimeFormatter: ListDateFormatter.DateTimeWithTodayShort,
        @Named("CoreConversationInfo") coreConversationInfo: CoreConversationInfo
    ): ConversationDataMapper {
        return ConversationDataMapper(context, documentMapper, messageMapper, dateTimeFormatter, coreConversationInfo)
    }

    @Provides
    @MessageSectionScope
    fun provideConversationMessagesPresenter(
        collectionComponent: ConversationListComponent<ConversationMessage>,
        interactor: ConversationInteractor,
        @Named("CoreConversationInfo") coreConversationInfo: CoreConversationInfo,
        dataDispatcher: ConversationDataDispatcher,
        builder: MessageCollectionFilter?,
        clipboardManager: ClipboardManager,
        messagesPushManager: MessagesPushManager,
        communicatorDependency: CommunicatorSbisConversationDependency,
        conversationViewModel: ConversationViewModel?,
        appLifecycleTracker: AppLifecycleTracker?,
        localFeatureService: LocalFeatureToggleService,
        featureService: SbisFeatureService?,
        conversationActionDelegate: ConversationActionDelegate,
        prefetchManager: ConversationPrefetchManager
    ): ConversationMessagesContract.Presenter<*> {
        val playerFeature = communicatorDependency.mediaPlayerFeature
        var mediaPlayer: MediaPlayer? = null
        if (playerFeature != null) {
            mediaPlayer = playerFeature.getMediaPlayer()
        }
        return ConversationMessagesPresenter(
            collectionComponent,
            interactor,
            coreConversationInfo,
            dataDispatcher,
            builder!!,
            clipboardManager,
            messagesPushManager,
            communicatorDependency.getRecipientSelectionResultManager(),
            conversationViewModel,
            appLifecycleTracker!!,
            mediaPlayer,
            communicatorDependency.loginInterface,
            localFeatureService,
            featureService,
            conversationActionDelegate,
            prefetchManager,
            communicatorPushKeyboardHelperProvider.get().getCommunicatorPushKeyboardHelper()
        )
    }

    @Provides
    @MessageSectionScope
    fun provideConversationComponentWrapper(
        listComponent: ComponentViewModel<HierarchyCollectionOfMessage, ConversationMessage, MessageCollectionFilter, DecoratedOfMessage, PathModelOfMessageMapOfStringString, TupleOfUuidOptionalOfBool>,
        storage: ConversationCollectionStorage
    ): ConversationListComponent<ConversationMessage> {
        return ConversationListComponent(
            listComponent,
            storage
        )
    }

    @Provides
    @MessageSectionScope
    fun provideComponentViewModel(
        viewModelStoreOwner: ViewModelStoreOwner,
        mapper: MessageMapper,
        stubFactory: StubFactory
    ): ComponentViewModel<HierarchyCollectionOfMessage, ConversationMessage, MessageCollectionFilter, DecoratedOfMessage, PathModelOfMessageMapOfStringString, TupleOfUuidOptionalOfBool> {
        return createConversationComponentVM(
            viewModelStoreOwner,
            mapper,
            stubFactory
        )
    }

    @Provides
    @MessageSectionScope
    fun provideConversationStubViewContentFactory(
        @Named("CoreConversationInfo") coreConversationInfo: CoreConversationInfo
    ): StubFactory {
        return ConversationStubViewContentFactory(coreConversationInfo.isChat)
    }

    @Provides
    @MessageSectionScope
    fun provideConversationCollectionStorage(
        storageProvider: DependencyProvider<MessageCollectionStorageProvider>
    ): ConversationCollectionStorage {
        return ConversationCollectionStorage(storageProvider)
    }

    @Provides
    @MessageSectionScope
    fun provideMessageCollectionFilter(): MessageCollectionFilter {
        return MessageCollectionFilter()
    }

    @Provides
    @MessageSectionScope
    fun provideConversationActionDelegate(
        senderActionListenerDelegate: ConversationSenderActionListenerDelegate?,
        phoneNumberDelegate: ConversationPhoneNumberDelegate?,
        signActionHelper: ConversationSingAndAcceptActionDelegate?,
        mediaActionDelegate: ConversationMediaActionDelegate?,
        threadActionDelegate: ConversationThreadActionDelegate?
    ): ConversationActionDelegate {
        return ConversationActionDelegate(
            senderActionListenerDelegate!!,
            phoneNumberDelegate!!,
            signActionHelper!!,
            mediaActionDelegate!!,
            threadActionDelegate!!
        )
    }

    @Provides
    @MessageSectionScope
    fun provideConversationPhoneNumberDelegate(
        clipboardManager: ClipboardManager?
    ): ConversationPhoneNumberDelegate {
        return ConversationPhoneNumberDelegate(clipboardManager!!)
    }

    @Provides
    @MessageSectionScope
    fun provideConversationSenderActionListenerDelegate(
        dataDispatcher: ConversationDataDispatcher?
    ): ConversationSenderActionListenerDelegate {
        return ConversationSenderActionListenerDelegate(dataDispatcher!!)
    }

    @Provides
    @MessageSectionScope
    fun provideConversationSignActionHelper(
        interactor: ConversationInteractor?
    ): ConversationSingAndAcceptActionDelegate {
        return ConversationSingAndAcceptActionDelegate(interactor!!)
    }

    @Provides
    @MessageSectionScope
    fun provideConversationMediaActionDelegate(): ConversationMediaActionDelegate {
        return ConversationMediaActionDelegate()
    }

    @Provides
    @MessageSectionScope
    fun provideThreadActionDelegate(
        @Named("CoreConversationInfo") coreConversationInfo: CoreConversationInfo?,
        interactor: ConversationInteractor?,
        dataDispatcher: ConversationDataDispatcher?
    ): ConversationThreadActionDelegate {
        return ConversationThreadActionDelegate(
            coreConversationInfo!!,
            interactor!!,
            dataDispatcher!!
        )
    }

    @Provides
    @MessageSectionScope
    fun provideConversationToolbarPresenter(
        interactor: ConversationInteractor,
        @Named("CoreConversationInfo") coreConversationInfo: CoreConversationInfo,
        toolbarEventManager: ConversationToolbarEventManager,
        dataDispatcher: ConversationDataDispatcher,
        resourceProvider: ResourceProvider,
        localFeatureService: LocalFeatureToggleService,
        featureService: SbisFeatureService?
    ): ConversationToolbarContract.Presenter<*> {
        return ConversationToolbarPresenterImpl(
            interactor,
            coreConversationInfo,
            dataDispatcher,
            resourceProvider,
            null,
            toolbarEventManager,
            localFeatureService = localFeatureService,
            featureService = featureService
        )
    }

    @Provides
    @MessageSectionScope
    internal fun provideConversationMessagePanelPresenter(
        interactor: ConversationInteractor,
        communicatorDependency: CommunicatorSbisConversationDependency,
        @Named("CoreConversationInfo") coreConversationInfo: CoreConversationInfo,
        dataDispatcher: ConversationDataDispatcher,
        resourceProvider: ResourceProvider,
        conversationEventsPublisher: ConversationEventsPublisher,
        directShareHelper: QuickShareHelper
    ): ConversationMessagePanelContract.Presenter<*> {
        val currentUuid = communicatorDependency.loginInterface.getCurrentAccount()?.uuid ?: UUIDUtils.NIL_UUID
        return ConversationMessagePanelPresenter(
            interactor,
            coreConversationInfo,
            dataDispatcher,
            resourceProvider,
            communicatorDependency.getRecipientSelectionResultManager(),
            null,
            conversationEventsPublisher,
            communicatorDependency.viewerSliderArgsFactory(),
            currentUuid,
            directShareHelper
        )
    }

    @Provides
    @MessageSectionScope
    fun provideConversationInteractor(
        themeRepository: ThemeRepository,
        dialogControllerProvider: DependencyProvider<DialogController>,
        dialogDocumentControllerProvider: DependencyProvider<DialogDocumentController>,
        chatControllerProvider: DependencyProvider<ChatController>,
        messageControllerProvider: DependencyProvider<MessageController>,
        themeParticipantsControllerProvider: DependencyProvider<ThemeParticipantsController>,
        conversationDataInteractor: ConversationDataInteractor,
        messageMapper: MessageMapper,
        conversationRepository: ConversationRepository,
        messageControllerBinaryMapper: MessageControllerBinaryMapper,
        activityStatusSubscriptionInitializer: CommunicatorActivityStatusSubscriptionInitializer,
        personControllerProvider: DependencyProvider<PersonController>
    ): ConversationInteractor {
        return ConversationInteractorImpl(
            themeRepository,
            dialogControllerProvider,
            dialogDocumentControllerProvider,
            chatControllerProvider,
            messageControllerProvider,
            themeParticipantsControllerProvider,
            conversationDataInteractor,
            messageMapper,
            activityStatusSubscriptionInitializer,
            prefetchManager = null,
            conversationRepository,
            messageControllerBinaryMapper,
            communicatorSbisConversationDependency?.attachmentControllerProvider,
            personControllerProvider
        )
    }

    @Provides
    @MessageSectionScope
    fun provideConversationDataInteractor(
        dialogControllerProvider: DependencyProvider<DialogController>,
        chatControllerProvider: DependencyProvider<ChatController>,
        conversationDataMapper: ConversationDataMapper,
        avatarPrefetchHelper: PersonAvatarPrefetchHelper
    ): ConversationDataInteractor =
        ConversationDataInteractorImpl(dialogControllerProvider, chatControllerProvider, conversationDataMapper, avatarPrefetchHelper)

    @Provides
    @MessageSectionScope
    fun provideConversationDataDispatcher(): ConversationDataDispatcher {
        return ConversationDataDispatcher()
    }

    @Provides
    @MessageSectionScope
    fun provideMessagesListAdapter(
        @Named(MESSAGE_SECTION_CONTEXT) context: Context,
        @Named(MESSAGE_SECTION_CONVERSATION_POOL) conversationPoolController: ConversationViewPoolController,
        conversationPresenter: ConversationMessagesContract.Presenter<*>,
        dateUpdater: ListDateViewUpdater
    ): MessagesListAdapter {
        val conversationViewPool = conversationPoolController.getViewPoolsHolder(context)
        return MessagesListAdapter(
            conversationViewPool,
            dateUpdater
        ).also { it.actionsListener.init(conversationPresenter) }
    }

    @Provides
    @MessageSectionScope
    @Named(MESSAGE_SECTION_CONVERSATION_POOL)
    fun provideConversationViewPoolController(): ConversationViewPoolController =
        ConversationViewPoolController()

    @Provides
    @MessageSectionScope
    fun provideListDateViewUpdater(
        dateTimeWithTodayFormatter: ListDateFormatter.DateTimeWithTodayShort
    ): ListDateViewUpdater =
        ListDateViewUpdater(dateTimeWithTodayFormatter)

    @Provides
    @MessageSectionScope
    internal fun provideRepository(
        controller: DependencyProvider<MessageController>,
        mapper: MessageControllerBinaryMapper,
        activityStatusSubscriptionInitializer: CommunicatorActivityStatusSubscriptionInitializer
    ): ConversationRepository {
        return ConversationRepository(controller, mapper, activityStatusSubscriptionInitializer)
    }

    @Provides
    @MessageSectionScope
    internal fun provideMessageControllerBinaryMapper(): MessageControllerBinaryMapper =
        MessageControllerBinaryMapper()

    @Provides
    @MessageSectionScope
    internal fun provideMessageListMapper(
        context: Context,
        messageMapper: MessageMapper
    ): BaseModelMapper<ListResultOfMessageMapOfStringString, PagedListResult<ConversationMessage>> {
        return MessageListMapper(context, messageMapper)
    }


    @Provides
    @MessageSectionScope
    internal fun provideFilter(): ConversationFilter {
        return ConversationFilter()
    }

    @Provides
    @MessageSectionScope
    internal fun provideSubscriptionManager(eventManagerServiceSubscriber: EventManagerServiceSubscriber): SubscriptionManager {
        return SubscriptionManager(eventManagerServiceSubscriber)
    }

    @Provides
    @MessageSectionScope
    internal fun provideEventManagerSubscriber(context: Context): EventManagerServiceSubscriber {
        return DefaultEventManagerServiceSubscriber(context)
    }
}

private const val MESSAGE_SECTION_CONVERSATION_POOL = "message_section_conversation_pool"
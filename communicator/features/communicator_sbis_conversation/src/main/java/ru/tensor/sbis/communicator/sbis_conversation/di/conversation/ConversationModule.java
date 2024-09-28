package ru.tensor.sbis.communicator.sbis_conversation.di.conversation;

import static ru.tensor.sbis.communicator.base.conversation.presentation.crud.ConversationComponentVmFactoryKt.createConversationComponentVM;
import static ru.tensor.sbis.communicator.sbis_conversation.CommunicatorSbisConversationPlugin.communicatorPushKeyboardHelperProvider;
import static ru.tensor.sbis.communicator.sbis_conversation.CommunicatorSbisConversationPlugin.communicatorSbisConversationDependency;

import android.content.Context;

import java.util.UUID;

import javax.inject.Named;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelStoreOwner;

import dagger.Module;
import dagger.Provides;
import ru.tensor.sbis.common.data.DependencyProvider;
import ru.tensor.sbis.common.lifecycle.AppLifecycleTracker;
import ru.tensor.sbis.common.util.ResourceProvider;
import ru.tensor.sbis.common.util.UUIDUtils;
import ru.tensor.sbis.communication_decl.communicator.media.MediaPlayer;
import ru.tensor.sbis.communicator.base.conversation.presentation.presenter.MessageCollectionFilter;
import ru.tensor.sbis.communicator.base.conversation.presentation.crud.ConversationCollectionStorage;
import ru.tensor.sbis.communicator.base.conversation.presentation.crud.ConversationListComponent;
import ru.tensor.sbis.communicator.base.conversation.presentation.crud.ConversationStubViewContentFactory;
import ru.tensor.sbis.communicator.common.conversation.ConversationEventsPublisher;
import ru.tensor.sbis.communicator.common.conversation.ConversationPrefetchManager;
import ru.tensor.sbis.communicator.common.conversation.data.ListResultOfMessageMapOfStringString;
import ru.tensor.sbis.communicator.common.util.CommunicatorActivityStatusSubscriptionInitializer;
import ru.tensor.sbis.communicator.common.util.PersonAvatarPrefetchHelper;
import ru.tensor.sbis.communicator.common.util.share.quick_share.QuickShareHelper;
import ru.tensor.sbis.communicator.generated.DecoratedOfMessage;
import ru.tensor.sbis.communicator.generated.DialogDocumentController;
import ru.tensor.sbis.communicator.generated.HierarchyCollectionOfMessage;
import ru.tensor.sbis.communicator.generated.MessageCollectionStorageProvider;
import ru.tensor.sbis.communicator.generated.PathModelOfMessageMapOfStringString;
import ru.tensor.sbis.communicator.generated.TupleOfUuidOptionalOfBool;
import ru.tensor.sbis.communicator.sbis_conversation.contract.CommunicatorSbisConversationDependency;
import ru.tensor.sbis.communicator.sbis_conversation.ui.message.delegates.ConversationActionDelegate;
import ru.tensor.sbis.communicator.sbis_conversation.ui.message.delegates.ConversationMediaActionDelegate;
import ru.tensor.sbis.communicator.sbis_conversation.ui.message.delegates.ConversationPhoneNumberDelegate;
import ru.tensor.sbis.communicator.sbis_conversation.ui.message.delegates.ConversationSenderActionListenerDelegate;
import ru.tensor.sbis.communicator.sbis_conversation.ui.message.delegates.ConversationSingAndAcceptActionDelegate;
import ru.tensor.sbis.communicator.sbis_conversation.ui.message.delegates.ConversationThreadActionDelegate;
import ru.tensor.sbis.communicator.sbis_conversation.interactor.data.ConversationDataInteractor;
import ru.tensor.sbis.communicator.sbis_conversation.interactor.data.ConversationDataInteractorImpl;
import ru.tensor.sbis.communicator.common.conversation.crud.MessageControllerBinaryMapper;
import ru.tensor.sbis.communicator.sbis_conversation.ui.crud.ConversationPrefetchManagerImpl;
import ru.tensor.sbis.crud4.ComponentViewModel;
import ru.tensor.sbis.crud4.view.StubFactory;
import ru.tensor.sbis.design.list_header.ListDateViewUpdater;
import ru.tensor.sbis.design.list_header.format.ListDateFormatter;
import ru.tensor.sbis.communication_decl.communicator.media.MediaPlayerFeature;
import ru.tensor.sbis.feature_ctrl.SbisFeatureService;
import ru.tensor.sbis.localfeaturetoggle.domain.LocalFeatureToggleService;
import ru.tensor.sbis.mvp.data.model.PagedListResult;
import ru.tensor.sbis.mvp.interactor.crudinterface.event.DefaultEventManagerServiceSubscriber;
import ru.tensor.sbis.mvp.interactor.crudinterface.event.EventManagerServiceSubscriber;
import ru.tensor.sbis.mvp.interactor.crudinterface.subscribing.SubscriptionManager;
import ru.tensor.sbis.common.modelmapper.BaseModelMapper;
import ru.tensor.sbis.communicator.sbis_conversation.data.CoreConversationInfo;
import ru.tensor.sbis.communicator.sbis_conversation.data.mapper.ConversationDataMapper;
import ru.tensor.sbis.communicator.sbis_conversation.data.mapper.DocumentMapper;
import ru.tensor.sbis.communicator.sbis_conversation.data.mapper.MessageMapper;
import ru.tensor.sbis.communicator.sbis_conversation.data.model.ConversationMessage;
import ru.tensor.sbis.communicator.sbis_conversation.interactor.ConversationInteractor;
import ru.tensor.sbis.communicator.sbis_conversation.interactor.ConversationInteractorImpl;
import ru.tensor.sbis.communicator.sbis_conversation.ui.ConversationContract;
import ru.tensor.sbis.communicator.sbis_conversation.ui.ConversationDataDispatcher;
import ru.tensor.sbis.communicator.sbis_conversation.ui.crud.ConversationRepository;
import ru.tensor.sbis.communicator.sbis_conversation.ui.crud.MessageListMapper;
import ru.tensor.sbis.communicator.sbis_conversation.ui.message.ConversationMessagesContract;
import ru.tensor.sbis.communicator.sbis_conversation.ui.message.ConversationMessagesPresenter;
import ru.tensor.sbis.communicator.sbis_conversation.ui.messagepanel.ConversationMessagePanelContract;
import ru.tensor.sbis.communicator.sbis_conversation.ui.messagepanel.ConversationMessagePanelPresenter;
import ru.tensor.sbis.communicator.sbis_conversation.ui.toolbar.ConversationToolbarContract;
import ru.tensor.sbis.communicator.sbis_conversation.ui.toolbar.ConversationToolbarPresenterImpl;
import ru.tensor.sbis.communicator.sbis_conversation.ui.viewmodel.ConversationViewModel;
import ru.tensor.sbis.common.util.ClipboardManager;
import ru.tensor.sbis.communicator.generated.ChatController;
import ru.tensor.sbis.communicator.generated.DialogController;
import ru.tensor.sbis.communicator.generated.MessageController;
import ru.tensor.sbis.communicator.generated.ThemeParticipantsController;
import ru.tensor.sbis.communicator.common.crud.ThemeRepository;
import ru.tensor.sbis.communicator.sbis_conversation.ui.ConversationPresenterImpl;
import ru.tensor.sbis.communicator.common.push.MessagesPushManager;
import ru.tensor.sbis.profiles.generated.PersonController;

/**
 * DI модуль экрана сообщений сбис
 *
 * @author vv.chekurda
 */
@Module
public class ConversationModule {

    @Provides
    @ConversationScope
    DocumentMapper provideDocumentMapper(@NonNull Context context) {
        return new DocumentMapper(context);
    }

    @Provides
    @ConversationScope
    ConversationDataMapper provideConversationDataMapper(
            @NonNull Context context,
            @NonNull DocumentMapper documentMapper,
            @NonNull MessageMapper messageMapper,
            @NonNull ListDateFormatter.DateTimeWithTodayShort dateTimeFormatter,
            @NonNull @Named("CoreConversationInfo") CoreConversationInfo coreConversationInfo
    ) {
        return new ConversationDataMapper(
                context,
                documentMapper,
                messageMapper,
                dateTimeFormatter,
                coreConversationInfo
        );
    }

    @Provides
    @ConversationScope
    ConversationListComponent<ConversationMessage> provideConversationComponentWrapper(
        @NonNull ComponentViewModel<HierarchyCollectionOfMessage, ConversationMessage, MessageCollectionFilter, DecoratedOfMessage, PathModelOfMessageMapOfStringString, TupleOfUuidOptionalOfBool> listComponent,
        @NonNull ConversationCollectionStorage storage
    ) {
        return new ConversationListComponent<ConversationMessage>(
            listComponent,
            storage
        );
    }

    @Provides
    @ConversationScope
    ConversationCollectionStorage provideConversationCollectionStorage(
        DependencyProvider<MessageCollectionStorageProvider> storageProvider
    ) {
        return new ConversationCollectionStorage(storageProvider);
    }

    @Provides
    @ConversationScope
    ComponentViewModel<HierarchyCollectionOfMessage, ConversationMessage, MessageCollectionFilter, DecoratedOfMessage, PathModelOfMessageMapOfStringString, TupleOfUuidOptionalOfBool> provideComponentViewModel(
            ViewModelStoreOwner viewModelStoreOwner,
            MessageMapper mapper,
            StubFactory stubFactory
    ) {
        return createConversationComponentVM(viewModelStoreOwner, mapper, stubFactory);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Provides
    @ConversationScope
    ConversationContract.ConversationPresenter provideConversationPresenter(
            @NonNull ConversationInteractor interactor,
            @NonNull CommunicatorSbisConversationDependency communicatorDependency,
            @NonNull @Named("CoreConversationInfo") CoreConversationInfo coreConversationInfo,
            @NonNull ConversationDataDispatcher dataDispatcher,
            @NonNull ResourceProvider resourceProvider,
            @NonNull ConversationMessagesContract.Presenter messagesPresenter,
            @NonNull ConversationToolbarContract.Presenter toolbarPresenter,
            @NonNull ConversationViewModel conversationViewModel,
            @NonNull ConversationEventsPublisher conversationEventsPublisher,
            @NonNull QuickShareHelper directShareHelper,
            @NonNull LocalFeatureToggleService featureService
    ) {
        // используется id, т.к. uuid по какой-то причине отличается от uuid-a в модели(Person) контроллера
        UUID currentUuid = UUIDUtils.fromString(communicatorDependency.getLoginInterface().getCurrentPersonId());
        if (currentUuid == null) {
            currentUuid = UUIDUtils.NIL_UUID;
        }

        ConversationMessagePanelContract.Presenter panelPresenter =
                new ConversationMessagePanelPresenter(
                        interactor,
                        coreConversationInfo,
                        dataDispatcher,
                        resourceProvider,
                        communicatorDependency.getRecipientSelectionResultManager(),
                        conversationViewModel,
                        conversationEventsPublisher,
                        communicatorDependency.viewerSliderArgsFactory(),
                        currentUuid,
                        directShareHelper,
                        featureService
                );

        return new ConversationPresenterImpl(messagesPresenter, panelPresenter, toolbarPresenter);
    }

    @SuppressWarnings("rawtypes")
    @Provides
    @ConversationScope
    ConversationMessagesContract.Presenter provideConversationMessagesPresenter(
            @NonNull ConversationListComponent<ConversationMessage> collectionComponent,
            @NonNull ConversationInteractor interactor,
            @NonNull @Named("CoreConversationInfo") CoreConversationInfo coreConversationInfo,
            @NonNull ConversationDataDispatcher dataDispatcher,
            MessageCollectionFilter builder,
            @NonNull ClipboardManager clipboardManager,
            @NonNull MessagesPushManager messagesPushManager,
            @NonNull CommunicatorSbisConversationDependency communicatorDependency,
            ConversationViewModel conversationViewModel,
            AppLifecycleTracker appLifecycleTracker,
            @NonNull LocalFeatureToggleService localFeatureService,
            @Nullable SbisFeatureService featureService,
            @NonNull ConversationActionDelegate conversationActionDelegate,
            @NonNull ConversationPrefetchManager prefetchManager
    ) {
        MediaPlayerFeature playerFeature = communicatorDependency.getMediaPlayerFeature();
        MediaPlayer mediaPlayer = null;
        if (playerFeature != null) {
            mediaPlayer = playerFeature.getMediaPlayer();
        }
        return new ConversationMessagesPresenter(
                collectionComponent,
                interactor,
                coreConversationInfo,
                dataDispatcher,
                builder,
                clipboardManager,
                messagesPushManager,
                communicatorDependency.getRecipientSelectionResultManager(),
                conversationViewModel,
                appLifecycleTracker,
                mediaPlayer,
                communicatorDependency.getLoginInterface(),
                localFeatureService,
                featureService,
                conversationActionDelegate,
                prefetchManager,
                communicatorPushKeyboardHelperProvider.get().getCommunicatorPushKeyboardHelper()
        );
    }

    @SuppressWarnings("rawtypes")
    @Provides
    @ConversationScope
    ConversationToolbarContract.Presenter provideConversationToolbarPresenter(
            @NonNull ConversationInteractor interactor,
            @NonNull @Named("CoreConversationInfo") CoreConversationInfo coreConversationInfo,
            ConversationViewModel conversationViewModel,
            @NonNull ConversationDataDispatcher dataDispatcher,
            @NonNull ResourceProvider resourceProvider,
            @NonNull CommunicatorSbisConversationDependency communicatorDependency,
            @NonNull LocalFeatureToggleService localFeatureService,
            @Nullable SbisFeatureService featureService) {
        return new ConversationToolbarPresenterImpl(
                interactor,
                coreConversationInfo,
                dataDispatcher,
                resourceProvider,
                conversationViewModel,
                null,
                communicatorDependency.getRecipientSelectionResultManager(),
                localFeatureService,
                featureService
        );
    }

    @Provides
    @ConversationScope
    ConversationInteractor provideConversationInteractor(
            @NonNull ThemeRepository themeRepository,
            @NonNull DependencyProvider<DialogController> dialogControllerProvider,
            @NonNull DependencyProvider<DialogDocumentController> dialogDocumentControllerProvider,
            @NonNull DependencyProvider<ChatController> chatControllerProvider,
            @NonNull DependencyProvider<MessageController> messageControllerProvider,
            @NonNull DependencyProvider<ThemeParticipantsController> themeParticipantsControllerProvider,
            @NonNull ConversationDataInteractor conversationDataInteractor,
            @NonNull MessageMapper messageMapper,
            @NonNull ConversationPrefetchManager prefetchManager,
            @NonNull ConversationRepository conversationRepository,
            @NonNull MessageControllerBinaryMapper messageControllerBinaryMapper,
            @NonNull CommunicatorActivityStatusSubscriptionInitializer activityStatusSubscriptionInitializer,
            @NonNull DependencyProvider<PersonController> personControllerProvider) {
        return new ConversationInteractorImpl(
                themeRepository,
                dialogControllerProvider,
                dialogDocumentControllerProvider,
                chatControllerProvider,
                messageControllerProvider,
                themeParticipantsControllerProvider,
                conversationDataInteractor,
                messageMapper,
                activityStatusSubscriptionInitializer,
                (ConversationPrefetchManagerImpl) prefetchManager,
                conversationRepository,
                messageControllerBinaryMapper,
                communicatorSbisConversationDependency.getAttachmentControllerProvider(),
                personControllerProvider
        );
    }

    @Provides
    @ConversationScope
    ConversationDataInteractor provideConversationDataInteractor(
            @NonNull DependencyProvider<DialogController> dialogControllerProvider,
            @NonNull DependencyProvider<ChatController> chatControllerProvider,
            @NonNull ConversationDataMapper conversationDataMapper,
            @NonNull PersonAvatarPrefetchHelper prefetchHelper
    ) {
        return new ConversationDataInteractorImpl(
                dialogControllerProvider,
                chatControllerProvider,
                conversationDataMapper,
                prefetchHelper
        );
    }

    @Provides
    @ConversationScope
    ConversationRepository provideConversationRepository(
            DependencyProvider<MessageController> controller,
            MessageControllerBinaryMapper mapper,
            CommunicatorActivityStatusSubscriptionInitializer activityStatusSubscriptionInitializer
    ) {
        return new ConversationRepository(controller, mapper, activityStatusSubscriptionInitializer);
    }

    @Provides
    @ConversationScope
    MessageControllerBinaryMapper provideMessageControllerBinaryMapper() {
        return new MessageControllerBinaryMapper();
    }

    @Provides
    @ConversationScope
    BaseModelMapper<ListResultOfMessageMapOfStringString, PagedListResult<ConversationMessage>> provideMessageListMapper(
            Context context,
            MessageMapper messageMapper) {
        return new MessageListMapper(context, messageMapper);
    }

    @Provides
    @ConversationScope
    MessageCollectionFilter provideMessageCollectionFilter() {
        return new MessageCollectionFilter();
    }

    @NonNull
    @Provides
    @ConversationScope
    ConversationDataDispatcher provideConversationDataDispatcher() {
        return new ConversationDataDispatcher();
    }

    @Provides
    @ConversationScope
    SubscriptionManager provideSubscriptionManager(EventManagerServiceSubscriber eventManagerServiceSubscriber) {
        return new SubscriptionManager(eventManagerServiceSubscriber);
    }

    @SuppressWarnings("deprecation")
    @Provides
    @ConversationScope
    EventManagerServiceSubscriber provideEventManagerSubscriber(Context context) {
        return new DefaultEventManagerServiceSubscriber(context);
    }

    @Provides
    @ConversationScope
    MessageMapper provideMessageMapper(
            @NonNull Context context,
            @Named("CoreConversationInfo") CoreConversationInfo coreConversationInfo
    ) {
        return new MessageMapper(
                context,
                coreConversationInfo.isInitAsGroupDialog(),
                coreConversationInfo.isChat(),
                false
        );
    }

    @Provides
    @ConversationScope
    ListDateViewUpdater provideListDateViewUpdater(
            @NonNull ListDateFormatter.DateTimeWithTodayShort dateTimeWithTodayFormatter
    ) {
        return new ListDateViewUpdater(dateTimeWithTodayFormatter);
    }

    @Provides
    @ConversationScope
    StubFactory provideConversationStubViewContentFactory(
            @Named("CoreConversationInfo") CoreConversationInfo coreConversationInfo
    ) {
        return new ConversationStubViewContentFactory(coreConversationInfo.isChat());
    }

    @Provides
    @ConversationScope
    ConversationActionDelegate provideConversationActionDelegate(
            ConversationSenderActionListenerDelegate senderActionListenerDelegate,
            ConversationPhoneNumberDelegate phoneNumberDelegate,
            ConversationSingAndAcceptActionDelegate signActionHelper,
            ConversationMediaActionDelegate mediaActionDelegate,
            ConversationThreadActionDelegate threadActionDelegate
    ) {
        return new ConversationActionDelegate(
                senderActionListenerDelegate,
                phoneNumberDelegate,
                signActionHelper,
                mediaActionDelegate,
                threadActionDelegate
        );
    }

    @Provides
    @ConversationScope
    ConversationPhoneNumberDelegate provideConversationPhoneNumberDelegate(
            ClipboardManager clipboardManager
    ) {
        return new ConversationPhoneNumberDelegate(clipboardManager);
    }

    @Provides
    @ConversationScope
    ConversationSenderActionListenerDelegate provideConversationSenderActionListenerDelegate(
            ConversationDataDispatcher dataDispatcher
    ) {
        return new ConversationSenderActionListenerDelegate(dataDispatcher);
    }

    @Provides
    @ConversationScope
    ConversationSingAndAcceptActionDelegate provideConversationSignActionHelper(
            ConversationInteractor interactor
    ) {
        return new ConversationSingAndAcceptActionDelegate(interactor);
    }

    @Provides
    @ConversationScope
    ConversationMediaActionDelegate provideConversationMediaActionDelegate() {
        return new ConversationMediaActionDelegate();
    }

    @Provides
    @ConversationScope
    ConversationThreadActionDelegate provideThreadActionDelegate(
            @Named("CoreConversationInfo") CoreConversationInfo coreConversationInfo,
            ConversationInteractor interactor,
            ConversationDataDispatcher dataDispatcher
    ) {
        return new ConversationThreadActionDelegate(
                coreConversationInfo,
                interactor,
                dataDispatcher
        );
    }
}

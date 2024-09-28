package ru.tensor.sbis.communicator.sbis_conversation

import CommunicatorPushKeyboardHelper
import ru.tensor.sbis.attachment.signing.decl.AttachmentsSigningProvider
import ru.tensor.sbis.attachments.decl.attachment_list.AttachmentListViewerIntentFactory
import ru.tensor.sbis.communication_decl.analytics.AnalyticsUtil
import ru.tensor.sbis.communication_decl.communicator.ConversationToolbarEventProvider
import ru.tensor.sbis.communication_decl.communicator.media.MediaPlayerFeature
import ru.tensor.sbis.communication_decl.communicator.ui.ConversationProvider
import ru.tensor.sbis.communication_decl.complain.ComplainDialogFragmentFeature
import ru.tensor.sbis.communication_decl.complain.ComplainService
import ru.tensor.sbis.communication_decl.meeting.MeetingActivityProvider
import ru.tensor.sbis.communication_decl.selection.recipient.RecipientSelectionProvider
import ru.tensor.sbis.communication_decl.videocall.bl.CallStateProvider
import ru.tensor.sbis.communicator.common.conversation.ConversationPrefetchManager
import ru.tensor.sbis.communicator.common.conversation.ConversationRouterProvider
import ru.tensor.sbis.communicator.common.conversation.ConversationToolbarEventManagerProvider
import ru.tensor.sbis.communicator.common.conversation.utils.pool.ConversationViewPoolInitializer
import ru.tensor.sbis.communicator.common.di.CommunicatorCommonComponent
import ru.tensor.sbis.communicator.common.navigation.contract.CommunicatorConversationRouter
import ru.tensor.sbis.communicator.common.themes_registry.AddChatParticipantsIntentFactory
import ru.tensor.sbis.communicator.common.themes_registry.ChatSettingsIntentFactory
import ru.tensor.sbis.communicator.common.themes_registry.ConversationInformationFactory
import ru.tensor.sbis.communicator.common.themes_registry.ConversationParticipantsFactory
import ru.tensor.sbis.communicator.contacts_declaration.controller.ContactsControllerWrapper
import ru.tensor.sbis.communicator.declaration.MessageListSectionProvider
import ru.tensor.sbis.communicator.declaration.send_message.SendMessageManager
import ru.tensor.sbis.communicator.sbis_conversation.contract.CommunicatorSbisConversationDependency
import ru.tensor.sbis.communicator.sbis_conversation.contract.CommunicatorSbisConversationFeatureImpl
import ru.tensor.sbis.communicator.sbis_conversation.di.singleton.CommunicatorSbisConversationSingletonComponent
import ru.tensor.sbis.communicator.common.conversation_preview.ConversationPreviewFragmentFactory
import ru.tensor.sbis.communicator.sbis_conversation.utils.DefaultMediaPlayerUriResolver
import ru.tensor.sbis.design.audio_player_view.view.message.contact.AudioMessageViewDataFactory
import ru.tensor.sbis.design.message_panel.audio_recorder.integration.contract.AudioRecorderDelegateFactory
import ru.tensor.sbis.design.message_panel.audio_recorder.integration.contract.RecordCancelConfirmationDialogProvider
import ru.tensor.sbis.design.message_panel.video_recorder.integration.contract.VideoRecorderDelegateFactory
import ru.tensor.sbis.design.message_view.contact.MessageViewComponentsFactory
import ru.tensor.sbis.design.profile.person.feature.requirePersonViewComponent
import ru.tensor.sbis.design.video_message_view.message.contract.VideoMessageViewDataFactory
import ru.tensor.sbis.edo_decl.document.DocWebViewerFeature
import ru.tensor.sbis.events_tracker.EventsTracker
import ru.tensor.sbis.feature_ctrl.SbisFeatureServiceProvider
import ru.tensor.sbis.info_decl.news.ui.NewsActivityProvider
import ru.tensor.sbis.message_panel.decl.AttachmentControllerProvider
import ru.tensor.sbis.message_panel.feature.MessagePanelFeature
import ru.tensor.sbis.person_decl.employee.person_card.PersonCardProvider
import ru.tensor.sbis.plugin_struct.BasePlugin
import ru.tensor.sbis.plugin_struct.Dependency
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.plugin_struct.feature.FeatureProvider
import ru.tensor.sbis.plugin_struct.feature.FeatureWrapper
import ru.tensor.sbis.profile_service.controller.employee_profile.EmployeeProfileControllerWrapper
import ru.tensor.sbis.profile_service.controller.person.PersonControllerWrapper
import ru.tensor.sbis.tasks.feature.DocumentFeature
import ru.tensor.sbis.tasks.feature.TasksCreateFeature
import ru.tensor.sbis.toolbox_decl.linkopener.OpenLinkController
import ru.tensor.sbis.verification_decl.login.LoginInterface
import ru.tensor.sbis.verification_decl.verification.VerificationEventProvider
import ru.tensor.sbis.verification_decl.verification.ui.VerificationFragmentProvider
import ru.tensor.sbis.viewer.decl.slider.ViewerSliderIntentFactory

/**
 * Плагин CommunicatorSbisConversation
 *
 * @author kv.martyshenko
 */
object CommunicatorSbisConversationPlugin : BasePlugin<CommunicatorSbisConversationPlugin.CustomizationOptions>() {

    private val singletonComponentHolder = object : CommunicatorSbisConversationSingletonComponent.Holder {
        override val communicatorSbisConversationSingletonComponent: CommunicatorSbisConversationSingletonComponent by lazy {
            val dependency = object : CommunicatorSbisConversationDependency,
                MessagePanelFeature by messagePanelFeatureProvider.get(),
                RecipientSelectionProvider by recipientSelectionFeatureProvider.get(),
                LoginInterface.Provider by loginInterfaceProvider.get(),
                PersonCardProvider by personCardProvider.get(),
                CommunicatorConversationRouter.Provider by communicatorConversationRouterProvider.get(),
                ConversationToolbarEventManagerProvider by conversationToolbarEventManagerProvider.get(),
                VerificationFragmentProvider by verificationFragmentProvider.get(),
                VerificationEventProvider by verificationEventProvider.get(),
                EmployeeProfileControllerWrapper.Provider by employeeProfileControllerWrapperProvider.get(),
                ContactsControllerWrapper.Provider by contactsControllerWrapperProvider.get(),
                PersonControllerWrapper.Provider by personControllerWrapperProvider.get(),
                RecordCancelConfirmationDialogProvider by recordCancelConfirmDialogProvider.get() {

                override val audioRecorderDelegateFactory: AudioRecorderDelegateFactory =
                    audioRecordDelegateFactoryProvider.get()

                override val videoRecorderDelegateFactory: VideoRecorderDelegateFactory =
                    videoRecordDelegateFactoryProvider.get()

                override val messageViewComponentsFactory: MessageViewComponentsFactory =
                    messageViewComponentsFactoryProvider.get()

                override val audioMessageViewDataFactory: AudioMessageViewDataFactory =
                    audioMessageViewDataFactoryProvider.get()

                override val videoMessageViewDataFactory: VideoMessageViewDataFactory =
                    videoMessageViewDataFactoryProvider.get()

                override val openLinkControllerProvider: OpenLinkController.Provider?
                    get() = openLinkControllerFeatureProvider?.get()

                override val documentFeature: DocumentFeature?
                    get() = documentFeatureProvider?.get()

                override val viewerSliderIntentFactory: ViewerSliderIntentFactory?
                    get() = viewerSliderIntentFactoryProvider?.get()

                override val attachmentListViewerIntentFactory: AttachmentListViewerIntentFactory?
                    get() = attachmentListViewerIntentFactoryProvider?.get()

                override val meetingActivityProvider: MeetingActivityProvider?
                    get() = meetingActivityFeatureProvider?.get()

                override val docWebViewerFeature: DocWebViewerFeature?
                    get() = docWebViewerFeatureProvider?.get()

                override val chatRecipientSelectionIntentFactory: AddChatParticipantsIntentFactory?
                    get() = chatRecipientSelectionIntentFactoryProvider?.get()

                override val conversationParticipantsFactory: ConversationParticipantsFactory?
                    get() = conversationParticipantsFactoryProvider?.get()

                override val conversationInformationFactory: ConversationInformationFactory?
                    get() = conversationInformationFactoryProvider?.get()

                override val chatSettingsIntentFactory: ChatSettingsIntentFactory?
                    get() = chatSettingsIntentFactoryProvider?.get()

                override val attachmentsSigningProvider: AttachmentsSigningProvider?
                    get() = attachmentsSigningFeatureProvider?.get()

                override val newsActivityProvider: NewsActivityProvider?
                    get() = newsActivityFeatureProvider?.get()

                override val tasksCreateFeature: TasksCreateFeature?
                    get() = tasksCreateFeatureProvider?.get()

                override val mediaPlayerFeature: MediaPlayerFeature?
                    get() = mediaPlayerFeatureProvider?.get()

                override val callStateProviderFeature: CallStateProvider?
                    get() = callStateProvider?.get()

                override val eventsTracker: EventsTracker?
                    get() = eventsTrackerProvider?.get()

                override val complainServiceProvider: ComplainService.Provider?
                    get() = complainServiceFeatureProvider?.get()

                override val complainFragmentFeature: ComplainDialogFragmentFeature?
                    get() = complainDialogFragmentFeatureProvider?.get()

                override val analyticsUtilProvider: AnalyticsUtil.Provider?
                    get() = analyticsUtilFeatureProvider?.get()

                override val sendMessageManagerProvider: SendMessageManager.Provider?
                    get() = sendMessageManagerFeatureProvider?.get()

                override val attachmentControllerProvider: AttachmentControllerProvider?
                    get() = attachmentControllerFeatureProvider?.get()

                override val sbisFeatureServiceProvider: SbisFeatureServiceProvider?
                    get() = featureServiceProvider?.get()
            }
            communicatorSbisConversationDependency = dependency
            CommunicatorSbisConversationSingletonComponent.Initializer(dependency).init(commonCommunicatorComponentProvider.get())
        }
    }

    @get:JvmStatic
    internal val singletonComponent: CommunicatorSbisConversationSingletonComponent
        @JvmName("getSingletonComponent")
        get() = singletonComponentHolder.communicatorSbisConversationSingletonComponent

    @JvmField
    internal var communicatorSbisConversationDependency: CommunicatorSbisConversationDependency? = null

    @get:JvmName("getFeature")
    internal val feature by lazy {
        CommunicatorSbisConversationFeatureImpl(singletonComponentHolder)
    }

    private lateinit var commonCommunicatorComponentProvider: FeatureProvider<CommunicatorCommonComponent>
    private lateinit var loginInterfaceProvider: FeatureProvider<LoginInterface.Provider>
    private lateinit var personCardProvider: FeatureProvider<PersonCardProvider>
    private lateinit var communicatorConversationRouterProvider: FeatureProvider<CommunicatorConversationRouter.Provider>
    private lateinit var conversationToolbarEventManagerProvider: FeatureProvider<ConversationToolbarEventManagerProvider>
    private lateinit var verificationFragmentProvider: FeatureProvider<VerificationFragmentProvider>
    private lateinit var verificationEventProvider: FeatureProvider<VerificationEventProvider>
    private lateinit var employeeProfileControllerWrapperProvider: FeatureProvider<EmployeeProfileControllerWrapper.Provider>
    private lateinit var contactsControllerWrapperProvider: FeatureProvider<ContactsControllerWrapper.Provider>
    private lateinit var personControllerWrapperProvider: FeatureProvider<PersonControllerWrapper.Provider>
    private lateinit var messagePanelFeatureProvider: FeatureProvider<MessagePanelFeature>
    private lateinit var audioRecordDelegateFactoryProvider: FeatureProvider<AudioRecorderDelegateFactory>
    private lateinit var videoRecordDelegateFactoryProvider: FeatureProvider<VideoRecorderDelegateFactory>
    private lateinit var recordCancelConfirmDialogProvider: FeatureProvider<RecordCancelConfirmationDialogProvider>
    private lateinit var recipientSelectionFeatureProvider: FeatureProvider<RecipientSelectionProvider>
    private lateinit var messageViewComponentsFactoryProvider: FeatureProvider<MessageViewComponentsFactory>
    private lateinit var audioMessageViewDataFactoryProvider: FeatureProvider<AudioMessageViewDataFactory>
    private lateinit var videoMessageViewDataFactoryProvider: FeatureProvider<VideoMessageViewDataFactory>
    internal lateinit var communicatorPushKeyboardHelperProvider:  FeatureProvider<CommunicatorPushKeyboardHelper.Provider>

    private var openLinkControllerFeatureProvider: FeatureProvider<OpenLinkController.Provider>? = null
    private var documentFeatureProvider: FeatureProvider<DocumentFeature>? = null
    private var viewerSliderIntentFactoryProvider: FeatureProvider<ViewerSliderIntentFactory>? = null
    private var attachmentListViewerIntentFactoryProvider: FeatureProvider<AttachmentListViewerIntentFactory>? = null
    private var meetingActivityFeatureProvider: FeatureProvider<MeetingActivityProvider>? = null
    private var docWebViewerFeatureProvider: FeatureProvider<DocWebViewerFeature>? = null
    private var chatRecipientSelectionIntentFactoryProvider: FeatureProvider<AddChatParticipantsIntentFactory>? = null
    private var conversationParticipantsFactoryProvider: FeatureProvider<ConversationParticipantsFactory>? = null
    private var conversationInformationFactoryProvider: FeatureProvider<ConversationInformationFactory>? = null
    private var chatSettingsIntentFactoryProvider: FeatureProvider<ChatSettingsIntentFactory>? = null
    private var attachmentsSigningFeatureProvider: FeatureProvider<AttachmentsSigningProvider>? = null
    private var newsActivityFeatureProvider: FeatureProvider<NewsActivityProvider>? = null
    private var tasksCreateFeatureProvider: FeatureProvider<TasksCreateFeature>? = null
    private var mediaPlayerFeatureProvider: FeatureProvider<MediaPlayerFeature>? = null
    private var eventsTrackerProvider: FeatureProvider<EventsTracker>? = null
    private var callStateProvider: FeatureProvider<CallStateProvider>? = null
    private var complainServiceFeatureProvider: FeatureProvider<ComplainService.Provider>? = null
    private var complainDialogFragmentFeatureProvider: FeatureProvider<ComplainDialogFragmentFeature>? = null
    private var analyticsUtilFeatureProvider: FeatureProvider<AnalyticsUtil.Provider>? = null
    private var sendMessageManagerFeatureProvider: FeatureProvider<SendMessageManager.Provider>? = null
    private var attachmentControllerFeatureProvider: FeatureProvider<AttachmentControllerProvider>? = null
    private var featureServiceProvider: FeatureProvider<SbisFeatureServiceProvider>? = null

    override val api: Set<FeatureWrapper<out Feature>> = setOf(
        FeatureWrapper(CommunicatorSbisConversationSingletonComponent::class.java) { singletonComponent },
        FeatureWrapper(ConversationRouterProvider::class.java) { feature },
        FeatureWrapper(ConversationProvider::class.java) { feature },
        FeatureWrapper(MessageListSectionProvider::class.java) { feature },
        FeatureWrapper(ConversationToolbarEventProvider::class.java) { feature },
        FeatureWrapper(ConversationToolbarEventManagerProvider::class.java) { feature },
        FeatureWrapper(ConversationPrefetchManager.Provider::class.java) { feature },
        FeatureWrapper(ConversationViewPoolInitializer::class.java) { feature },
        FeatureWrapper(ConversationPreviewFragmentFactory::class.java) { feature }
    )

    override val dependency: Dependency = Dependency.Builder()
        .require(CommunicatorCommonComponent::class.java) { commonCommunicatorComponentProvider = it }
        .require(RecipientSelectionProvider::class.java) { recipientSelectionFeatureProvider = it }
        .require(LoginInterface.Provider::class.java) { loginInterfaceProvider = it }
        .require(PersonCardProvider::class.java) { personCardProvider = it }
        .require(CommunicatorConversationRouter.Provider::class.java) { communicatorConversationRouterProvider = it }
        .require(ConversationToolbarEventManagerProvider::class.java) { conversationToolbarEventManagerProvider = it }
        .require(VerificationFragmentProvider::class.java) { verificationFragmentProvider = it }
        .require(VerificationEventProvider::class.java) { verificationEventProvider = it }
        .require(EmployeeProfileControllerWrapper.Provider::class.java) { employeeProfileControllerWrapperProvider = it }
        .require(ContactsControllerWrapper.Provider::class.java) { contactsControllerWrapperProvider = it }
        .require(PersonControllerWrapper.Provider::class.java) { personControllerWrapperProvider = it }
        .require(MessagePanelFeature::class.java) { messagePanelFeatureProvider = it }
        .require(AudioRecorderDelegateFactory::class.java) { audioRecordDelegateFactoryProvider = it }
        .require(VideoRecorderDelegateFactory::class.java) { videoRecordDelegateFactoryProvider = it }
        .require(RecordCancelConfirmationDialogProvider::class.java) { recordCancelConfirmDialogProvider = it }
        .require(MessageViewComponentsFactory::class.java) { messageViewComponentsFactoryProvider = it }
        .require(AudioMessageViewDataFactory::class.java) { audioMessageViewDataFactoryProvider = it }
        .require(VideoMessageViewDataFactory::class.java) { videoMessageViewDataFactoryProvider = it }
        .require(CommunicatorPushKeyboardHelper.Provider::class.java)  { communicatorPushKeyboardHelperProvider = it }
        .requirePersonViewComponent()
        .optional(TasksCreateFeature::class.java) { tasksCreateFeatureProvider = it }
        .optional(OpenLinkController.Provider::class.java) { openLinkControllerFeatureProvider = it }
        .optional(DocumentFeature::class.java) { documentFeatureProvider = it }
        .optional(ViewerSliderIntentFactory::class.java) { viewerSliderIntentFactoryProvider = it }
        .optional(AttachmentListViewerIntentFactory::class.java) { attachmentListViewerIntentFactoryProvider = it }
        .optional(MeetingActivityProvider::class.java) { meetingActivityFeatureProvider = it }
        .optional(DocWebViewerFeature::class.java) { docWebViewerFeatureProvider = it }
        .optional(AddChatParticipantsIntentFactory::class.java) { chatRecipientSelectionIntentFactoryProvider = it }
        .optional(ConversationParticipantsFactory::class.java) { conversationParticipantsFactoryProvider = it }
        .optional(ConversationInformationFactory::class.java) { conversationInformationFactoryProvider = it }
        .optional(ChatSettingsIntentFactory::class.java) { chatSettingsIntentFactoryProvider = it }
        .optional(AttachmentsSigningProvider::class.java) { attachmentsSigningFeatureProvider = it }
        .optional(NewsActivityProvider::class.java) { newsActivityFeatureProvider = it }
        .optional(MediaPlayerFeature::class.java) { mediaPlayerFeatureProvider = it }
        .optional(CallStateProvider::class.java) { callStateProvider = it }
        .optional(EventsTracker::class.java) { eventsTrackerProvider = it }
        .optional(ComplainService.Provider::class.java) { complainServiceFeatureProvider = it }
        .optional(ComplainDialogFragmentFeature::class.java) { complainDialogFragmentFeatureProvider = it }
        .optional(AnalyticsUtil.Provider::class.java) { analyticsUtilFeatureProvider = it }
        .optional(SendMessageManager.Provider::class.java) { sendMessageManagerFeatureProvider = it }
        .optional(AttachmentControllerProvider::class.java) { attachmentControllerFeatureProvider = it }
        .optional(SbisFeatureServiceProvider::class.java) { featureServiceProvider = it }
        .build()

    override val customizationOptions: CustomizationOptions = CustomizationOptions()

    override fun doAfterInitialize() {
        mediaPlayerFeatureProvider?.get()
            ?.getMediaPlayer()
            ?.setUriResolver(DefaultMediaPlayerUriResolver())
    }

    class CustomizationOptions internal constructor() {

        /**
         * Активна ли возможность пожаловаться на контент в диалогах и чатах.
         */
        var complainEnabled: Boolean = true
    }
}
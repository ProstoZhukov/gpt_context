package ru.tensor.sbis.communicator.sbis_conversation.contract

import ru.tensor.sbis.attachment.signing.decl.AttachmentsSigningProvider
import ru.tensor.sbis.attachments.decl.attachment_list.AttachmentListViewerIntentFactory
import ru.tensor.sbis.communication_decl.analytics.AnalyticsUtil
import ru.tensor.sbis.communication_decl.communicator.media.MediaPlayerFeature
import ru.tensor.sbis.communication_decl.complain.ComplainDialogFragmentFeature
import ru.tensor.sbis.communication_decl.complain.ComplainService
import ru.tensor.sbis.communication_decl.meeting.MeetingActivityProvider
import ru.tensor.sbis.communication_decl.selection.recipient.RecipientSelectionProvider
import ru.tensor.sbis.communication_decl.videocall.bl.CallStateProvider
import ru.tensor.sbis.communicator.common.conversation.ConversationToolbarEventManagerProvider
import ru.tensor.sbis.communicator.common.navigation.contract.CommunicatorConversationRouter
import ru.tensor.sbis.communicator.common.themes_registry.AddChatParticipantsIntentFactory
import ru.tensor.sbis.communicator.common.themes_registry.ChatSettingsIntentFactory
import ru.tensor.sbis.communicator.common.themes_registry.ConversationInformationFactory
import ru.tensor.sbis.communicator.common.themes_registry.ConversationParticipantsFactory
import ru.tensor.sbis.communicator.contacts_declaration.controller.ContactsControllerWrapper
import ru.tensor.sbis.communicator.declaration.send_message.SendMessageManager
import ru.tensor.sbis.design.audio_player_view.view.message.contact.AudioMessageViewDataFactory
import ru.tensor.sbis.design.message_panel.audio_recorder.integration.contract.AudioRecorderDelegateFactory
import ru.tensor.sbis.design.message_panel.audio_recorder.integration.contract.RecordCancelConfirmationDialogProvider
import ru.tensor.sbis.design.message_panel.video_recorder.integration.contract.VideoRecorderDelegateFactory
import ru.tensor.sbis.design.message_view.contact.MessageViewComponentsFactory
import ru.tensor.sbis.design.video_message_view.message.contract.VideoMessageViewDataFactory
import ru.tensor.sbis.edo_decl.document.DocWebViewerFeature
import ru.tensor.sbis.events_tracker.EventsTracker
import ru.tensor.sbis.feature_ctrl.SbisFeatureServiceProvider
import ru.tensor.sbis.info_decl.news.ui.NewsActivityProvider
import ru.tensor.sbis.message_panel.decl.AttachmentControllerProvider
import ru.tensor.sbis.message_panel.feature.MessagePanelFeature
import ru.tensor.sbis.person_decl.employee.person_card.PersonCardProvider
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
 * Внешние зависимости модуля сообщений сбис.
 *
 * @author vv.chekurda
 */
interface CommunicatorSbisConversationDependency :
    RecipientSelectionProvider,
    LoginInterface.Provider,
    PersonCardProvider,
    CommunicatorConversationRouter.Provider,
    ConversationToolbarEventManagerProvider,
    VerificationFragmentProvider,
    VerificationEventProvider,
    EmployeeProfileControllerWrapper.Provider,
    ContactsControllerWrapper.Provider,
    PersonControllerWrapper.Provider,
    MessagePanelFeature,
    RecordCancelConfirmationDialogProvider {

    val audioRecorderDelegateFactory: AudioRecorderDelegateFactory

    val videoRecorderDelegateFactory: VideoRecorderDelegateFactory

    val messageViewComponentsFactory: MessageViewComponentsFactory

    val audioMessageViewDataFactory: AudioMessageViewDataFactory

    val videoMessageViewDataFactory: VideoMessageViewDataFactory

    /** @SelfDocumented */
    val openLinkControllerProvider: OpenLinkController.Provider?

    /** Опциональная фича документа */
    @Suppress("DEPRECATION")
    val documentFeature: DocumentFeature?

    /** Опциональная фабрика слайдер просмотрщиков */
    val viewerSliderIntentFactory: ViewerSliderIntentFactory?

    /** Опциональная фабрика просмотра вертикального списка вложений */
    val attachmentListViewerIntentFactory: AttachmentListViewerIntentFactory?

    /** Опциональный провайдер запуска активности создания совещаний, вебинаров, событий */
    val meetingActivityProvider: MeetingActivityProvider?

    /** Опциональная фича просмотра документа в WebView */
    val docWebViewerFeature: DocWebViewerFeature?

    /** Опциональная фабрика открытия экрана выбора участников диалога/чата */
    val chatRecipientSelectionIntentFactory: AddChatParticipantsIntentFactory?

    /** Опциональная фабрика открытия экрана участников диалога/чата */
    val conversationParticipantsFactory: ConversationParticipantsFactory?

    /** Опциональная фабрика открытия экрана информации диалога/чата */
    val conversationInformationFactory: ConversationInformationFactory?

    /** Опциональная фабрика открытия экрана настроек чата */
    val chatSettingsIntentFactory: ChatSettingsIntentFactory?

    /** Опциональная фича подписания вложений */
    val attachmentsSigningProvider: AttachmentsSigningProvider?

    /** Опциональная фича для работы с новостями */
    val newsActivityProvider: NewsActivityProvider?

    /** Опциональная фича создания задач */
    val tasksCreateFeature: TasksCreateFeature?

    /** Опциональная фича создания плеера для проигрывания аудио и видео */
    val mediaPlayerFeature: MediaPlayerFeature?

    /** Опциональная фича для проверки наличия текущих звонков */
    val callStateProviderFeature: CallStateProvider?

    /** Опциональная фича для отправки логов в firebase */
    val eventsTracker: EventsTracker?

    /** Поставщик интерфейса сервиса "пожаловаться" */
    val complainServiceProvider: ComplainService.Provider?

    /** Опциональная фича функционала "пожаловаться" */
    val complainFragmentFeature: ComplainDialogFragmentFeature?

    /** Поставщик интерфейса утилиты для отправки аналитики. */
    val analyticsUtilProvider: AnalyticsUtil.Provider?

    /** Поставщик интерфейса механизма для отправки сообщений в фоне при помощи WorkManager. */
    val sendMessageManagerProvider: SendMessageManager.Provider?

    /** Поставщик контроллера вложений. */
    val attachmentControllerProvider: AttachmentControllerProvider?

    /** Поставщик сервиса облачных фиче-тоглов. */
    val sbisFeatureServiceProvider: SbisFeatureServiceProvider?
}

package ru.tensor.sbis.communicator.common

import CommunicatorPushKeyboardHelper
import ru.tensor.sbis.attachments.decl.action.AddAttachmentsUseCase
import ru.tensor.sbis.attachments.decl.action.AttachmentRequestAccessProvider
import ru.tensor.sbis.attachments.decl.action.DeleteAttachmentsUseCase
import ru.tensor.sbis.attachments.decl.mapper.AttachmentModelMapperFactory
import ru.tensor.sbis.common.di.CommonSingletonComponent
import ru.tensor.sbis.communication_decl.complain.ComplainService
import ru.tensor.sbis.communication_decl.analytics.AnalyticsUtil
import ru.tensor.sbis.communicator.common.contract.CommunicatorCommonDependency
import ru.tensor.sbis.communicator.common.contract.CommunicatorCommonFeatureImpl
import ru.tensor.sbis.communicator.common.conversation.ConversationEventsPublisher
import ru.tensor.sbis.communicator.common.crud.ThemeRepositoryProvider
import ru.tensor.sbis.communicator.common.di.CommunicatorCommonComponent
import ru.tensor.sbis.communicator.common.push.CommunicatorPushSubscriberProvider
import ru.tensor.sbis.communicator.common.push.MessagesPushManagerProvider
import ru.tensor.sbis.communicator.generated.MsgLogger
import ru.tensor.sbis.communicator.generated.OnLogCallback
import ru.tensor.sbis.communication_decl.communicator.media.waveform.WaveformDownscaleUtil
import ru.tensor.sbis.communicator.contacts_declaration.controller.ContactsControllerWrapper
import ru.tensor.sbis.design.message_panel.decl.record.AudioWaveformHelper
import ru.tensor.sbis.message_panel.decl.AttachmentControllerProvider
import ru.tensor.sbis.message_panel.decl.MessageControllerProvider
import ru.tensor.sbis.message_panel.decl.RecipientsControllerProvider
import ru.tensor.sbis.person_decl.profile.PersonActivityStatusNotifier
import ru.tensor.sbis.platform.generated.Subscription
import ru.tensor.sbis.plugin_struct.Dependency
import ru.tensor.sbis.plugin_struct.BasePlugin
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.plugin_struct.feature.FeatureProvider
import ru.tensor.sbis.plugin_struct.feature.FeatureWrapper
import ru.tensor.sbis.profile_service.controller.employee_profile.EmployeeProfileControllerWrapper
import ru.tensor.sbis.verification_decl.login.LoginInterface
import ru.tensor.sbis.verification_decl.login.event.AuthEvent
import timber.log.Timber

/**
 * Плагин основы коммуникатора
 *
 * @author kv.martyshenko
 */
object CommunicatorCommonPlugin : BasePlugin<Unit>(), CommunicatorCommonComponent.Holder {
    private val communicatorCommonFeature by lazy {
        CommunicatorCommonFeatureImpl(this)
    }

    private val attachmentControllerProvider by lazy {
        AttachmentControllerProvider { communicatorCommonComponent.attachmentController }
    }
    private lateinit var addAttachmentsUseCaseProvider: FeatureProvider<AddAttachmentsUseCase>
    private lateinit var deleteAttachmentsUseCaseProvider: FeatureProvider<DeleteAttachmentsUseCase>
    internal val messageControllerProvider by lazy {
        MessageControllerProvider { communicatorCommonComponent.messageController }
    }
    private val recipientControllerProvider by lazy {
        RecipientsControllerProvider { communicatorCommonComponent.recipientsController }
    }
    private val audioWaveformHelper by lazy {
        AudioWaveformHelper.Provider { communicatorCommonComponent.audioWaveformHelper }
    }

    private val waveformDownscaleUtil by lazy {
        WaveformDownscaleUtil.Provider { communicatorCommonComponent.waveformDownscaleUtil }
    }

    private val attachmentRequestAccessProvider: AttachmentRequestAccessProvider by lazy {
        communicatorCommonComponent.attachmentRequestAccessProvider
    }

    private val analyticsUtil by lazy {
        AnalyticsUtil.Provider { communicatorCommonComponent.analyticsUtil }
    }
    private lateinit var msgLoggerSubscription: Subscription

    internal lateinit var personActivityStatusNotifierProvider: FeatureProvider<PersonActivityStatusNotifier>
    private lateinit var commonSingletonComponentProvider: FeatureProvider<CommonSingletonComponent>
    private lateinit var messagesPushManagerProvider: FeatureProvider<MessagesPushManagerProvider>
    private lateinit var themeRepositoryProvider: FeatureProvider<ThemeRepositoryProvider>
    private lateinit var communicatorPushSubscriberProvider: FeatureProvider<CommunicatorPushSubscriberProvider>
    private lateinit var attachmentModelMapperFactoryProvider: FeatureProvider<AttachmentModelMapperFactory>
    private lateinit var employeeProfileControllerWrapperProvider: FeatureProvider<EmployeeProfileControllerWrapper.Provider>
    private var complainServiceFeatureProvider: FeatureProvider<ComplainService.Provider>? = null
    private var loginInterfaceProvider: FeatureProvider<LoginInterface>? = null

    override val api: Set<FeatureWrapper<out Feature>> = setOf(
        FeatureWrapper(CommunicatorCommonComponent::class.java) { communicatorCommonComponent },
        FeatureWrapper(ConversationEventsPublisher.Provider::class.java) { communicatorCommonFeature },
        FeatureWrapper(ContactsControllerWrapper.Provider::class.java) { communicatorCommonFeature },
        FeatureWrapper(AttachmentControllerProvider::class.java) { attachmentControllerProvider },
        FeatureWrapper(MessageControllerProvider::class.java) { messageControllerProvider },
        FeatureWrapper(RecipientsControllerProvider::class.java) { recipientControllerProvider },
        FeatureWrapper(AttachmentRequestAccessProvider::class.java) { attachmentRequestAccessProvider },
        FeatureWrapper(AudioWaveformHelper.Provider::class.java) { audioWaveformHelper },
        FeatureWrapper(WaveformDownscaleUtil.Provider::class.java) { waveformDownscaleUtil },
        FeatureWrapper(AnalyticsUtil.Provider::class.java) { analyticsUtil },
        FeatureWrapper(CommunicatorPushKeyboardHelper.Provider::class.java) { communicatorCommonFeature }
    )

    override val dependency: Dependency = Dependency.Builder()
        .require(CommonSingletonComponent::class.java) { commonSingletonComponentProvider = it }
        .require(MessagesPushManagerProvider::class.java) { messagesPushManagerProvider = it }
        .require(ThemeRepositoryProvider::class.java) { themeRepositoryProvider = it }
        .require(CommunicatorPushSubscriberProvider::class.java) { communicatorPushSubscriberProvider = it }
        .require(AttachmentModelMapperFactory::class.java) { attachmentModelMapperFactoryProvider = it }
        .require(EmployeeProfileControllerWrapper.Provider::class.java) { employeeProfileControllerWrapperProvider = it }
        .require(AddAttachmentsUseCase::class.java) { addAttachmentsUseCaseProvider = it }
        .require(DeleteAttachmentsUseCase::class.java) { deleteAttachmentsUseCaseProvider = it }
        .require(PersonActivityStatusNotifier::class.java) { personActivityStatusNotifierProvider = it }
        .optional(ComplainService.Provider::class.java) { complainServiceFeatureProvider = it }
        .optional(LoginInterface::class.java) { loginInterfaceProvider = it }
        .build()

    override val customizationOptions: Unit = Unit

    override fun doAfterInitialize() {
        sendMessageErrorLogger()
        subscribeOnLogoutEvent()
    }

    override val communicatorCommonComponent: CommunicatorCommonComponent by lazy {
        val dependency = object : CommunicatorCommonDependency,
            MessagesPushManagerProvider by messagesPushManagerProvider.get(),
            ThemeRepositoryProvider by themeRepositoryProvider.get(),
            CommunicatorPushSubscriberProvider by communicatorPushSubscriberProvider.get(),
            AttachmentModelMapperFactory by attachmentModelMapperFactoryProvider.get(),
            EmployeeProfileControllerWrapper.Provider by employeeProfileControllerWrapperProvider.get(),
            PersonActivityStatusNotifier by personActivityStatusNotifierProvider.get() {
            override val complainServiceProvider
                get() = complainServiceFeatureProvider?.get()
        }
        CommunicatorCommonComponent.Initializer(dependency).init(commonSingletonComponentProvider.get())
    }

    /**
     * Установить логгер на контроллер сообщений для non-fatal репортов на firebase по ошибкам отправки сообщений.
     * Ссылку на подписку обязательно хранить.
     * Единственный способ сделать для контроллера отдельный issue.
     */
    private fun sendMessageErrorLogger() {
        // Ссылку на подписку обязательно хранить.
        msgLoggerSubscription = MsgLogger.instance().onLog().subscribe(
            object : OnLogCallback() {
                override fun onEvent(msg: String) {
                    Timber.e(SendMessageException(msg))
                }
            }
        )
        msgLoggerSubscription.enable()
    }

    private fun subscribeOnLogoutEvent() = loginInterfaceProvider?.let {
        it.get().eventsObservable
            .subscribe { authEvent ->
                if (authEvent.eventType == AuthEvent.EventType.LOGOUT) {
                    communicatorCommonComponent.directShareHelper.removeAllAppQuickShareTargets()
                }
            }
    }
}

/**
 * Ошибка отправки сообщения.
 */
private class SendMessageException(msg: String) : Exception(msg)
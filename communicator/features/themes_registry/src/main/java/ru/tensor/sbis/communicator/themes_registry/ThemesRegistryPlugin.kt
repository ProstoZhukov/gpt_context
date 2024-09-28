package ru.tensor.sbis.communicator.themes_registry

import android.annotation.SuppressLint
import ru.tensor.sbis.android_ext_decl.MainActivityProvider
import ru.tensor.sbis.attachments.decl.attachment_list.AttachmentListViewerIntentFactory
import ru.tensor.sbis.attachments.decl.mapper.AttachmentModelMapperFactory
import ru.tensor.sbis.common.navigation.NavxId
import ru.tensor.sbis.communication_decl.analytics.AnalyticsUtil
import ru.tensor.sbis.communication_decl.communicator.ui.ConversationProvider
import ru.tensor.sbis.communication_decl.complain.ComplainDialogFragmentFeature
import ru.tensor.sbis.communication_decl.complain.ComplainService
import ru.tensor.sbis.communication_decl.selection.recipient.RecipientSelectionProvider
import ru.tensor.sbis.communication_decl.selection.recipient.manager.RecipientSelectionResultDelegate
import ru.tensor.sbis.communication_decl.videocall.bl.CallStateProvider
import ru.tensor.sbis.communication_decl.videocall.ui.CallActivityProvider
import ru.tensor.sbis.communicator.common.conversation.ConversationEventsPublisher
import ru.tensor.sbis.communicator.common.conversation.ConversationPrefetchManager
import ru.tensor.sbis.communicator.common.conversation.utils.pool.ConversationViewPoolInitializer
import ru.tensor.sbis.communicator.common.data.model.CommunicatorHostFragmentFactory
import ru.tensor.sbis.communicator.common.di.CommunicatorCommonComponent
import ru.tensor.sbis.communicator.common.import_contacts.ImportContactsConfirmationFragmentFactory
import ru.tensor.sbis.communicator.common.import_contacts.ImportContactsHelper
import ru.tensor.sbis.communicator.common.navigation.contract.CommunicatorDialogInformationRouter
import ru.tensor.sbis.communicator.common.navigation.contract.CommunicatorThemesRouter
import ru.tensor.sbis.communicator.common.push.CommunicatorPushSubscriberProvider
import ru.tensor.sbis.communicator.common.push.MessagesPushManagerProvider
import ru.tensor.sbis.communicator.common.themes_registry.AddChatParticipantsIntentFactory
import ru.tensor.sbis.communicator.common.themes_registry.ChatSettingsIntentFactory
import ru.tensor.sbis.communicator.common.themes_registry.ConversationInformationFactory
import ru.tensor.sbis.communicator.common.themes_registry.ConversationParticipantsFactory
import ru.tensor.sbis.communicator.contacts_declaration.controller.ContactsControllerWrapper
import ru.tensor.sbis.communicator.declaration.CommunicatorFilesFragmentFactory
import ru.tensor.sbis.communicator.declaration.counter.factory.CommunicatorCounterProviderFactory
import ru.tensor.sbis.communicator.declaration.counter.nav_counters.CommunicatorNavCounters
import ru.tensor.sbis.communicator.declaration.host_factory.ThemesRegistryHostFragmentFactory
import ru.tensor.sbis.communicator.declaration.send_message.SendMessageManager
import ru.tensor.sbis.communicator.declaration.tab_history.ThemeTabHistory
import ru.tensor.sbis.communicator.declaration.theme.ThemesRegistryFragmentFactory
import ru.tensor.sbis.communicator.design.R
import ru.tensor.sbis.communicator.themes_registry.ThemesRegistryFacade.themesRegistryDependency
import ru.tensor.sbis.communicator.themes_registry.contract.ThemesRegistryDependency
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.theme.delegates.ThemeNotificationsConfigHelper
import ru.tensor.sbis.communicator.themes_registry.utils.history.ThemeTabHistoryImpl
import ru.tensor.sbis.design.files_picker.decl.SbisFilesPickerFactory
import ru.tensor.sbis.feature_ctrl.SbisFeatureService
import ru.tensor.sbis.feature_ctrl.SbisFeatureServiceProvider
import ru.tensor.sbis.info_decl.notification.NotificationFilterStrategyProvider
import ru.tensor.sbis.main_screen_decl.navigation.service.NavigationService
import ru.tensor.sbis.person_decl.employee.person_card.PersonCardProvider
import ru.tensor.sbis.plugin_struct.BasePlugin
import ru.tensor.sbis.plugin_struct.Dependency
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.plugin_struct.feature.FeatureProvider
import ru.tensor.sbis.plugin_struct.feature.FeatureWrapper
import ru.tensor.sbis.plugin_struct.requireIf
import ru.tensor.sbis.profile_service.controller.employee_profile.EmployeeProfileControllerWrapper
import ru.tensor.sbis.profile_service.controller.profile_settings.ProfileSettingsControllerWrapper
import ru.tensor.sbis.pushnotification.center.PushCenter
import ru.tensor.sbis.tasks.feature.TasksFeature
import ru.tensor.sbis.toolbox_decl.BuildConfig
import ru.tensor.sbis.toolbox_decl.linkopener.LinkOpenerRegistrar
import ru.tensor.sbis.toolbox_decl.linkopener.builder.LinkOpenHandlerCreator
import ru.tensor.sbis.toolbox_decl.navigation.NavxIdDecl
import ru.tensor.sbis.toolbox_decl.toolbar.ToolbarTabsController
import ru.tensor.sbis.verification_decl.login.LoginInterface
import ru.tensor.sbis.viewer.decl.slider.ViewerSliderIntentFactory

/**
 * Плагин реестра диалогов и чатов.
 *
 * @author da.zhukov
 */
object ThemesRegistryPlugin : BasePlugin<ThemesRegistryPlugin.CustomizationOptions>() {

    private lateinit var communicatorCommonComponentProvider: FeatureProvider<CommunicatorCommonComponent>
    private lateinit var conversationProvider: FeatureProvider<ConversationProvider>
    private lateinit var viewerSliderIntentFactoryProvider: FeatureProvider<ViewerSliderIntentFactory>
    private lateinit var attachmentListViewerIntentFactoryProvider: FeatureProvider<AttachmentListViewerIntentFactory>
    private lateinit var recipientSelectionProvider: FeatureProvider<RecipientSelectionProvider>
    private lateinit var recipientSelectionResultDelegateProvider: FeatureProvider<RecipientSelectionResultDelegate.Provider>
    private lateinit var mainActivityProvider: FeatureProvider<MainActivityProvider>
    private lateinit var personCardProvider: FeatureProvider<PersonCardProvider>
    private lateinit var conversationEventsPublisherProvider: FeatureProvider<ConversationEventsPublisher.Provider>
    private lateinit var attachmentModelMapperFactoryProvider: FeatureProvider<AttachmentModelMapperFactory>
    private lateinit var communicatorThemesRouterProvider: FeatureProvider<CommunicatorThemesRouter.Provider>
    private lateinit var communicatorDialogInformationRouter: FeatureProvider<CommunicatorDialogInformationRouter.Provider>
    private lateinit var contactsControllerWrapperProvider: FeatureProvider<ContactsControllerWrapper.Provider>
    private lateinit var employeeProfileControllerWrapperProvider: FeatureProvider<EmployeeProfileControllerWrapper.Provider>
    private lateinit var loginInterfaceProvider: FeatureProvider<LoginInterface.Provider>
    private lateinit var communicatorHostFragmentFactoryProvider: FeatureProvider<CommunicatorHostFragmentFactory>
    private lateinit var pushCenterProvider: FeatureProvider<PushCenter>
    private lateinit var linkOpenHandlerCreatorFeatureProvider: FeatureProvider<LinkOpenHandlerCreator.Provider>
    private lateinit var tabsVisibilityControllerProvider: FeatureProvider<ToolbarTabsController>
    internal lateinit var filesPickerFactoryProvider: FeatureProvider<SbisFilesPickerFactory>
    internal var communicatorFilesFragmentFactory: FeatureProvider<CommunicatorFilesFragmentFactory>? = null

    private var notificationFilterStrategyFeatureProvider: FeatureProvider<NotificationFilterStrategyProvider>? = null
    private var importContactsHelperFeatureProvider: FeatureProvider<ImportContactsHelper.Provider>? = null
    private var importContactsConfirmationFragmentFactoryProvider: FeatureProvider<ImportContactsConfirmationFragmentFactory>? =
        null
    private var communicatorPushFeatureSubscriberProvider: FeatureProvider<CommunicatorPushSubscriberProvider>? = null
    private var linkOpenerRegistrarProvider: FeatureProvider<LinkOpenerRegistrar.Provider>? = null
    private var conversationListCommandPrefetchManagerProvider: FeatureProvider<ConversationPrefetchManager.Provider>? =
        null
    private var conversationPoolInitializerFeature: FeatureProvider<ConversationViewPoolInitializer>? = null
    private var messagesPushManagerProvider: FeatureProvider<MessagesPushManagerProvider>? = null
    private var complainServiceFeatureProvider: FeatureProvider<ComplainService.Provider>? = null
    private var complainDialogFragmentFeatureProvider: FeatureProvider<ComplainDialogFragmentFeature>? = null
    private var analyticsUtilFeatureProvider: FeatureProvider<AnalyticsUtil.Provider>? = null
    private var callStateProvider: FeatureProvider<CallStateProvider>? = null
    private var callActivityProvider: FeatureProvider<CallActivityProvider>? = null
    private var sendMessageManagerFeatureProvider: FeatureProvider<SendMessageManager.Provider>? = null
    private var navigationServiceProvider: FeatureProvider<NavigationService>? = null
    private var profileSettingsControllerWrapperFeatureProvider: FeatureProvider<ProfileSettingsControllerWrapper.Provider>? = null
    private var tasksFeatureProvider: FeatureProvider<TasksFeature>? = null
    private var featureServiceProvider: FeatureProvider<SbisFeatureServiceProvider>? = null

    private val themeTabHistoryImpl by lazy {
        ThemeTabHistoryImpl(communicatorCommonComponentProvider.get().context)
    }

    override val api: Set<FeatureWrapper<out Feature>> = setOf(
        FeatureWrapper(ThemesRegistryFragmentFactory::class.java) { ThemesRegistryFacade },
        FeatureWrapper(AddChatParticipantsIntentFactory::class.java) { ThemesRegistryFacade },
        FeatureWrapper(ConversationParticipantsFactory::class.java) { ThemesRegistryFacade },
        FeatureWrapper(ChatSettingsIntentFactory::class.java) { ThemesRegistryFacade },
        FeatureWrapper(CommunicatorCounterProviderFactory::class.java) { ThemesRegistryFacade },
        FeatureWrapper(ThemesRegistryHostFragmentFactory::class.java) { ThemesRegistryFacade },
        FeatureWrapper(CommunicatorNavCounters.Provider::class.java) { ThemesRegistryFacade },
        FeatureWrapper(ConversationInformationFactory::class.java) { ThemesRegistryFacade },
        FeatureWrapper(ThemeTabHistory::class.java) { themeTabHistoryImpl }
    )

    override val dependency: Dependency by lazy {
        Dependency.Builder()
            .require(CommunicatorCommonComponent::class.java) { communicatorCommonComponentProvider = it }
            .require(ConversationProvider::class.java) { conversationProvider = it }
            .require(ViewerSliderIntentFactory::class.java) { viewerSliderIntentFactoryProvider = it }
            .require(AttachmentListViewerIntentFactory::class.java) { attachmentListViewerIntentFactoryProvider = it }
            .require(RecipientSelectionProvider::class.java) { recipientSelectionProvider = it }
            .require(RecipientSelectionResultDelegate.Provider::class.java) {
                recipientSelectionResultDelegateProvider = it
            }
            .require(MainActivityProvider::class.java) { mainActivityProvider = it }
            .require(PersonCardProvider::class.java) { personCardProvider = it }
            .require(AttachmentModelMapperFactory::class.java) { attachmentModelMapperFactoryProvider = it }
            .require(ConversationEventsPublisher.Provider::class.java) { conversationEventsPublisherProvider = it }
            .require(CommunicatorThemesRouter.Provider::class.java) { communicatorThemesRouterProvider = it }
            .require(CommunicatorDialogInformationRouter.Provider::class.java) {
                communicatorDialogInformationRouter = it
            }
            .require(EmployeeProfileControllerWrapper.Provider::class.java) {
                employeeProfileControllerWrapperProvider = it
            }
            .require(ContactsControllerWrapper.Provider::class.java) { contactsControllerWrapperProvider = it }
            .require(LoginInterface.Provider::class.java) { loginInterfaceProvider = it }
            .require(CommunicatorHostFragmentFactory::class.java) { communicatorHostFragmentFactoryProvider = it }
            .require(PushCenter::class.java) { pushCenterProvider = it }
            .require(LinkOpenHandlerCreator.Provider::class.java) { linkOpenHandlerCreatorFeatureProvider = it }
            .require(ToolbarTabsController::class.java) { tabsVisibilityControllerProvider = it }
            .require(SbisFilesPickerFactory::class.java) { filesPickerFactoryProvider = it }
            .require(CommunicatorFilesFragmentFactory::class.java) { communicatorFilesFragmentFactory = it }
            .requireIf(customizationOptions.linkOpenerHandlerEnabled, LinkOpenerRegistrar.Provider::class.java) {
                linkOpenerRegistrarProvider = it
            }
            .requireIf(customizationOptions.pushMessageHandlerEnabled, MessagesPushManagerProvider::class.java) {
                messagesPushManagerProvider = it
            }
            .optional(NotificationFilterStrategyProvider::class.java) { notificationFilterStrategyFeatureProvider = it }
            .optional(ImportContactsHelper.Provider::class.java) { importContactsHelperFeatureProvider = it }
            .optional(ImportContactsConfirmationFragmentFactory::class.java) {
                importContactsConfirmationFragmentFactoryProvider = it
            }
            .optional(CommunicatorPushSubscriberProvider::class.java) { communicatorPushFeatureSubscriberProvider = it }
            .optional(ConversationPrefetchManager.Provider::class.java) {
                conversationListCommandPrefetchManagerProvider = it
            }
            .optional(ConversationViewPoolInitializer::class.java) { conversationPoolInitializerFeature = it }
            .optional(ComplainService.Provider::class.java) { complainServiceFeatureProvider = it }
            .optional(ComplainDialogFragmentFeature::class.java) { complainDialogFragmentFeatureProvider = it }
            .optional(AnalyticsUtil.Provider::class.java) { analyticsUtilFeatureProvider = it }
            .optional(CallStateProvider::class.java) { callStateProvider = it }
            .optional(CallActivityProvider::class.java) { callActivityProvider = it }
            .optional(SendMessageManager.Provider::class.java) { sendMessageManagerFeatureProvider = it }
            .optional(NavigationService::class.java) { navigationServiceProvider = it }
            .optional(ProfileSettingsControllerWrapper.Provider::class.java) { profileSettingsControllerWrapperFeatureProvider = it }
            .optional(TasksFeature::class.java) { tasksFeatureProvider = it }
            .optional(SbisFeatureServiceProvider::class.java) { featureServiceProvider = it }
            .build()
    }

    override val customizationOptions = CustomizationOptions()

    override fun initialize() {
        val dependency = object : ThemesRegistryDependency,
            ConversationProvider by conversationProvider.get(),
            AttachmentListViewerIntentFactory by attachmentListViewerIntentFactoryProvider.get(),
            ViewerSliderIntentFactory by viewerSliderIntentFactoryProvider.get(),
            RecipientSelectionProvider by recipientSelectionProvider.get(),
            RecipientSelectionResultDelegate.Provider by recipientSelectionResultDelegateProvider.get(),
            MainActivityProvider by mainActivityProvider.get(),
            PersonCardProvider by personCardProvider.get(),
            ConversationEventsPublisher.Provider by conversationEventsPublisherProvider.get(),
            AttachmentModelMapperFactory by attachmentModelMapperFactoryProvider.get(),
            CommunicatorThemesRouter.Provider by communicatorThemesRouterProvider.get(),
            CommunicatorDialogInformationRouter.Provider by communicatorDialogInformationRouter.get(),
            ContactsControllerWrapper.Provider by contactsControllerWrapperProvider.get(),
            EmployeeProfileControllerWrapper.Provider by employeeProfileControllerWrapperProvider.get(),
            LoginInterface.Provider by loginInterfaceProvider.get(),
            CommunicatorHostFragmentFactory by communicatorHostFragmentFactoryProvider.get(),
            LinkOpenHandlerCreator.Provider by linkOpenHandlerCreatorFeatureProvider.get(),
            ToolbarTabsController by tabsVisibilityControllerProvider.get() {

            override val importContactsHelperProvider: ImportContactsHelper.Provider? =
                importContactsHelperFeatureProvider?.get()

            override val importContactsConfirmationFragmentFactory: ImportContactsConfirmationFragmentFactory? =
                importContactsConfirmationFragmentFactoryProvider?.get()

            override val communicatorPushSubscriberProvider: CommunicatorPushSubscriberProvider? =
                communicatorPushFeatureSubscriberProvider?.get()

            override val conversationListCommandPrefetchManagerProvider: ConversationPrefetchManager.Provider? =
                this@ThemesRegistryPlugin.conversationListCommandPrefetchManagerProvider?.get()

            override val conversationViewPoolInitializer: ConversationViewPoolInitializer?
                get() = conversationPoolInitializerFeature?.get()

            override val complainServiceProvider: ComplainService.Provider?
                get() = complainServiceFeatureProvider?.get()

            override val complainFragmentFeature: ComplainDialogFragmentFeature?
                get() = complainDialogFragmentFeatureProvider?.get()

            override val analyticsUtilProvider: AnalyticsUtil.Provider?
                get() = analyticsUtilFeatureProvider?.get()

            override val callStateProviderFeature: CallStateProvider?
                get() = callStateProvider?.get()

            override val callActivityProviderFeature: CallActivityProvider?
                get() = callActivityProvider?.get()

            override val sendMessageManagerProvider: SendMessageManager.Provider?
                get() = sendMessageManagerFeatureProvider?.get()

            override val navigationServiceFeature: NavigationService?
                get() = navigationServiceProvider?.get()

            override val profileSettingsControllerWrapperProvider: ProfileSettingsControllerWrapper.Provider?
                get() = profileSettingsControllerWrapperFeatureProvider?.get()

            override val themeTabHistory: ThemeTabHistory
                get() = themeTabHistoryImpl

            override val notificationFilterStrategyProvider: NotificationFilterStrategyProvider?
                get() = notificationFilterStrategyFeatureProvider?.get()

            override val tasksFeature: TasksFeature?
                get() = tasksFeatureProvider?.get()

            override val sbisFeatureService: SbisFeatureService?
                get() = featureServiceProvider?.get()?.sbisFeatureService
        }

        ThemesRegistryFacade.configure(dependency)
    }

    @SuppressLint("CheckResult")
    override fun doAfterInitialize() {
        ThemeNotificationsConfigHelper.updateAvailableNoticeTypes(themesRegistryDependency.notificationFilterStrategyProvider)

        if (customizationOptions.pushMessageHandlerEnabled) {
            val messagesPushManager = messagesPushManagerProvider!!.get().messagesPushManager
            themesRegistryDependency.communicatorPushSubscriberProvider?.getCommunicatorPushSubscriber(
                application,
                messagesPushManager
            )?.subscribe(pushCenterProvider.get())
        }
        if (customizationOptions.linkOpenerHandlerEnabled) {
            linkOpenerRegistrarProvider!!.get().linkOpenerRegistrar.registerProvider(ThemesRegistryFacade)
        }
    }

    /**
     * Опции плагина модуля реестров диалогов и чатов.
     */
    class CustomizationOptions internal constructor() {

        /**
         * Есть ли у приложения элемент меню "Чаты" в боковой или нижней навигации.
         * Эта опция нужна для того чтобы знать нужно ли слать событие навигации при переключении в чаты или нет.
         */
        var appHasChatNavigationMenuItem: Boolean = false

        /**
         * Активировано ли раздереление диалогов/каналов.
         * Эта опция необходима для того чтобы отображать только один нужный реестр(диалоги/каналы).
         */
        var splittingChannelsAndDialogsEnabled: Boolean = false

        /**
         * Активирован ли обработчик пуш-уведомлений.
         */
        var pushMessageHandlerEnabled: Boolean = false

        /**
         * Название реестра с диалогами.
         * Необходимо для возможности кастомизации без изменения логики.
         */
        var dialogsRegistryTabTitle: Int = R.string.communicator_conversation_tab_dialogs

        /**
         * Название реестра с каналами.
         * Необходимо для возможности кастомизации без изменения логики.
         */
        var channelsRegistryTabTitle: Int = R.string.communicator_conversation_tab_channels

        /**
         * Навигационный идентификатор, с которым связывается заголовок раздела диалогов в шапке.
         * При наличии в данных навигации, заголовок меняется на соответствующее значение с сервиса.
         */
        var dialogsRegistryTopNavigationNavxId: NavxIdDecl? = NavxId.DIALOGS

        /**
         * Навигационный идентификатор, с которым связывается заголовок раздела чатов в шапке.
         * При наличии в данных навигации, заголовок меняется на соответствующее значение с сервиса.
         */
        var channelsRegistryTopNavigationNavxId: NavxIdDecl? = NavxId.CHATS

        /**
         * Устанавливаем обработчик для [LinkOpenerRegistrar] с базовой конфигурацией исходя из настроек [BuildConfig].
         * При необходимости изменения значения по умолчанию требуется обновить настройки через плагин в build.gradle проекта.
         * См. SabyLinkCfgPlugin
         */
        @Deprecated("Оставлен для обратной совместимости, должен быть private", ReplaceWith("SabyLinkCfgPlugin"))
        var linkOpenerHandlerEnabled: Boolean = BuildConfig.SABYLINK_THEMES_REGISTRY_FEATURE
    }
}

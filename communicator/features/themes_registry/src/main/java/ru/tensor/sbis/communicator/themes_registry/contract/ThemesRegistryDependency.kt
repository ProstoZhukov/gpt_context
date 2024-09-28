package ru.tensor.sbis.communicator.themes_registry.contract

import ru.tensor.sbis.android_ext_decl.MainActivityProvider
import ru.tensor.sbis.attachments.decl.attachment_list.AttachmentListViewerIntentFactory
import ru.tensor.sbis.attachments.decl.mapper.AttachmentModelMapperFactory
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
import ru.tensor.sbis.communicator.common.import_contacts.ImportContactsConfirmationFragmentFactory
import ru.tensor.sbis.communicator.common.import_contacts.ImportContactsHelper
import ru.tensor.sbis.communicator.common.navigation.contract.CommunicatorDialogInformationRouter
import ru.tensor.sbis.communicator.common.navigation.contract.CommunicatorThemesRouter
import ru.tensor.sbis.communicator.common.push.CommunicatorPushSubscriberProvider
import ru.tensor.sbis.communicator.declaration.send_message.SendMessageManager
import ru.tensor.sbis.communicator.declaration.tab_history.ThemeTabHistory
import ru.tensor.sbis.feature_ctrl.SbisFeatureService
import ru.tensor.sbis.info_decl.notification.NotificationFilterStrategyProvider
import ru.tensor.sbis.main_screen_decl.navigation.service.NavigationService
import ru.tensor.sbis.person_decl.employee.person_card.PersonCardProvider
import ru.tensor.sbis.profile_service.controller.employee_profile.EmployeeProfileControllerWrapper
import ru.tensor.sbis.profile_service.controller.profile_settings.ProfileSettingsControllerWrapper
import ru.tensor.sbis.tasks.feature.TasksFeature
import ru.tensor.sbis.toolbox_decl.linkopener.builder.LinkOpenHandlerCreator
import ru.tensor.sbis.toolbox_decl.toolbar.ToolbarTabsController
import ru.tensor.sbis.verification_decl.login.LoginInterface
import ru.tensor.sbis.viewer.decl.slider.ViewerSliderIntentFactory

/**
 * Внешние зависимости модуля реестра диалогов/каналов.
 * @see ConversationProvider
 * @see AttachmentListViewerIntentFactory
 * @see ViewerSliderIntentFactory
 * @see RecipientSelectionProvider
 * @see RecipientSelectionResultDelegate.Provider
 * @see MainActivityProvider
 * @see PersonCardProvider
 * @see ConversationEventsPublisher.Provider
 * @see AttachmentModelMapperFactory
 * @see CommunicatorThemesRouter.Provider
 * @see EmployeeProfileControllerWrapper.Provider
 * @see LoginInterface.Provider
 * @see ImportContactsHelper.Provider
 * @see ImportContactsConfirmationFragmentFactory
 * @see CommunicatorHostFragmentFactory
 * @see LinkOpenHandlerCreator.Provider
 * @see CommunicatorPushSubscriberProvider
 * @see ConversationPrefetchManager.Provider
 * @see ConversationViewPoolInitializer
 * @see CommunicatorDialogInformationRouter.Provider
 * @see ComplainService.Provider
 * @see AnalyticsUtil.Provider
 * @see CallStateProvider
 * @see ToolbarTabsController
 * @see SendMessageManager.Provider
 * @see NavigationService
 * @see ProfileSettingsControllerWrapper.Provider
 * @see ThemeTabHistory
 *
 * @author da.zhukov
 */
interface ThemesRegistryDependency :
    ConversationProvider,
    AttachmentListViewerIntentFactory,
    ViewerSliderIntentFactory,
    RecipientSelectionProvider,
    RecipientSelectionResultDelegate.Provider,
    MainActivityProvider,
    PersonCardProvider,
    ConversationEventsPublisher.Provider,
    AttachmentModelMapperFactory,
    CommunicatorThemesRouter.Provider,
    EmployeeProfileControllerWrapper.Provider,
    LoginInterface.Provider,
    CommunicatorHostFragmentFactory,
    LinkOpenHandlerCreator.Provider,
    CommunicatorDialogInformationRouter.Provider,
    ToolbarTabsController {

    /** Поставщик делегата для импортирования контактов с устройства */
    val importContactsHelperProvider: ImportContactsHelper.Provider?
    /** Фабрика фрагмента импорта контактов */
    val importContactsConfirmationFragmentFactory: ImportContactsConfirmationFragmentFactory?
    /** Поставщик подписки на пуш-уведомления для коммуникатора */
    val communicatorPushSubscriberProvider: CommunicatorPushSubscriberProvider?
    /** Поставщик интерфейса для предварительной загрузки переписки из реестра диалогов / каналов */
    val conversationListCommandPrefetchManagerProvider: ConversationPrefetchManager.Provider?
    /** Инициализатор пула View для реестра сообщений. */
    val conversationViewPoolInitializer: ConversationViewPoolInitializer?
    /** Поставщик интерфейса сервиса "пожаловаться" */
    val complainServiceProvider: ComplainService.Provider?
    /** Опциональная фича функционала "пожаловаться" */
    val complainFragmentFeature: ComplainDialogFragmentFeature?
    /** Поставщик интерфейса утилиты для отправки аналитики. */
    val analyticsUtilProvider: AnalyticsUtil.Provider?
    /** Поставщик интерфейса проверки наличия текущих звонков */
    val callStateProviderFeature: CallStateProvider?
    /** Проставщик активити звонка */
    val callActivityProviderFeature: CallActivityProvider?
    /** Поставщик интерфейса механизма для отправки сообщений в фоне при помощи WorkManager.  */
    val sendMessageManagerProvider: SendMessageManager.Provider?
    /** Поставщик интерфейса фичи доступных для пользователя разделов приложения. */
    val navigationServiceFeature: NavigationService?
    /** Поставщик интерфейса фичи UI обертки контроллера настроек пользователя. */
    val profileSettingsControllerWrapperProvider: ProfileSettingsControllerWrapper.Provider?
    /** Фича сохранения и восстановления вкладок диалоги/каналы. */
    val themeTabHistory: ThemeTabHistory
    /** Поставщик фильтра уведомлений по приложению. */
    val notificationFilterStrategyProvider: NotificationFilterStrategyProvider?
    /** Фича модуля задач. */
    val tasksFeature: TasksFeature?
    /** Поставщик сервиса облачных фиче-тоглов. */
    val sbisFeatureService: SbisFeatureService?

    interface Provider {

        /** @SelfDocumented */
        val themesRegistryDependency: ThemesRegistryDependency
    }
}
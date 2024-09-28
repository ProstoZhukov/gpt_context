package ru.tensor.sbis.communicator

import ru.tensor.sbis.android_ext_decl.MainActivityProvider
import ru.tensor.sbis.calendar_decl.schedule.ViolationActivityProvider
import ru.tensor.sbis.communication_decl.complain.ComplainService
import ru.tensor.sbis.communicator.common.di.CommunicatorCommonComponent
import ru.tensor.sbis.communicator.common.push.CRMChatPushSubscriberProvider
import ru.tensor.sbis.communicator.common.push.CommunicatorPushSubscriberProvider
import ru.tensor.sbis.communicator.common.push.MessagesPushManagerProvider
import ru.tensor.sbis.communicator.contract.CommunicatorPushDependency
import ru.tensor.sbis.communicator.contract.CommunicatorPushFeatureImpl
import ru.tensor.sbis.communicator.declaration.communicator_support_channel_list.SupportChannelListFragmentFactory
import ru.tensor.sbis.communicator.di.CommunicatorPushComponent
import ru.tensor.sbis.design.profile.person.feature.requirePersonViewComponent
import ru.tensor.sbis.info_decl.dialogs.DialogNotificationPushDelegate
import ru.tensor.sbis.info_decl.news.ui.NewsActivityProvider
import ru.tensor.sbis.plugin_struct.BasePlugin
import ru.tensor.sbis.plugin_struct.Dependency
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.plugin_struct.feature.FeatureProvider
import ru.tensor.sbis.plugin_struct.feature.FeatureWrapper
import ru.tensor.sbis.user_activity_track.service.UserActivityService

/**
 * Плагин для пуш-уведомлений коммуникатора
 *
 * @author kv.martyshenko
 */
object CommunicatorPushPlugin : BasePlugin<CommunicatorPushPlugin.CustomizationOptions>(), CommunicatorPushComponent.Holder {
    private val communicatorPushFeature by lazy {
        CommunicatorPushFeatureImpl()
    }

    private lateinit var commonSingletonComponentProvider: FeatureProvider<CommunicatorCommonComponent>
    private lateinit var mainActivityProvider: FeatureProvider<MainActivityProvider>
    private var userActivityServiceProvider: FeatureProvider<UserActivityService>? = null
    private var newsActivityFeatureProvider: FeatureProvider<NewsActivityProvider>? = null
    private var violationActivityFeatureProvider: FeatureProvider<ViolationActivityProvider>? = null
    private var complainServiceFeatureProvider: FeatureProvider<ComplainService.Provider>? = null
    private var supportChannelListFragmentFactoryProvider: FeatureProvider<SupportChannelListFragmentFactory>? = null
    private var dialogNotificationPushDelegateFeatureProvider: FeatureProvider<DialogNotificationPushDelegate>? = null

    override val api: Set<FeatureWrapper<out Feature>> = setOf(
        FeatureWrapper(CommunicatorPushSubscriberProvider::class.java) { communicatorPushFeature },
        FeatureWrapper(CRMChatPushSubscriberProvider::class.java) { communicatorPushFeature },
        FeatureWrapper(MessagesPushManagerProvider::class.java) { communicatorPushFeature },
        FeatureWrapper(CommunicatorPushComponent::class.java) { communicatorPushComponent }
    )

    override val dependency: Dependency = Dependency.Builder()
        .require(CommunicatorCommonComponent::class.java) { commonSingletonComponentProvider = it }
        .require(MainActivityProvider::class.java) { mainActivityProvider = it }
        .optional(NewsActivityProvider::class.java) { newsActivityFeatureProvider = it }
        .optional(ViolationActivityProvider::class.java) { violationActivityFeatureProvider = it }
        .requirePersonViewComponent()
        .optional(UserActivityService::class.java) { userActivityServiceProvider = it }
        .optional(ComplainService.Provider::class.java) { complainServiceFeatureProvider = it }
        .optional(SupportChannelListFragmentFactory::class.java) { supportChannelListFragmentFactoryProvider = it }
        .optional(DialogNotificationPushDelegate::class.java) { dialogNotificationPushDelegateFeatureProvider = it }
        .build()

    override val customizationOptions = CustomizationOptions()

    override val communicatorPushComponent: CommunicatorPushComponent by lazy {
        val dependency = object : CommunicatorPushDependency,
            MainActivityProvider by mainActivityProvider.get() {

            override val userActivityService: UserActivityService?
                get() = userActivityServiceProvider?.get() ?: super.userActivityService

            override val newsActivityProvider: NewsActivityProvider?
                get() = newsActivityFeatureProvider?.get()

            override val violationActivityProvider: ViolationActivityProvider?
                get() = violationActivityFeatureProvider?.get()

            override val complainServiceProvider: ComplainService.Provider?
                get() = complainServiceFeatureProvider?.get()

            override val supportChannelListFragmentFactory: SupportChannelListFragmentFactory?
                get() = supportChannelListFragmentFactoryProvider?.get()

            override val dialogNotificationPushDelegate: DialogNotificationPushDelegate?
                get() = dialogNotificationPushDelegateFeatureProvider?.get()
        }
        CommunicatorPushComponent.Initializer(dependency).init(commonSingletonComponentProvider.get())
    }

    /**
     * Опции плагина модуля реестров диалогов и чатов
     */
    class CustomizationOptions internal constructor() {

        /**
         * Есть ли у приложения элемент меню "Чаты" в боковой или нижней навигации.
         * Эта опция нужна для того чтобы знать нужно ли слать событие навигации при переключении в чаты или нет.
         */
        var appHasChatNavigationMenuItem: Boolean = false

        /**
         * Опция необходима для включения использования пушей каналов в разделе настройки уведомлений
         * Используется в приложении SabyMy, по умолчанию true.
         */
        var needToReceiveChatMessagesPushes: Boolean = true

        /**
         * Опция необходима для включения использования пушей каналов поддержки в разделе настройки уведомлений.
         */
        var needToReceiveSupportChatMessagesPushes: Boolean = false

        /**
         * Опция необходима для включения использования пушей каналов "Поддержка".
         */
        var needToReceiveSabySupportChatMessagesPushes: Boolean = false

        /**
         * Опция необходима для включения использования пушей в консультациях.
         * Используется в приложении CRM, по умолчанию false.
         */
        var needToReceiveConsultationMessagesPush: Boolean = false

        /**
         * Опция необходима для включения использования пушей диалогов.
         * Используется в приложении CRM, по умолчанию true.
         */
        var needToReceiveDialogMessagesPushes: Boolean = true
    }
}
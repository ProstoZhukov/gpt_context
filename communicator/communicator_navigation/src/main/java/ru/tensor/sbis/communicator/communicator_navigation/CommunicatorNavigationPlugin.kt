package ru.tensor.sbis.communicator.communicator_navigation

import ru.tensor.sbis.communicator.common.navigation.contract.*
import ru.tensor.sbis.communicator.communicator_navigation.contract.CommunicatorNavigationDependency
import ru.tensor.sbis.communicator.declaration.theme.ThemesRegistryFragmentFactory
import ru.tensor.sbis.communication_decl.communicator.ui.ConversationProvider
import ru.tensor.sbis.edo_decl.document.DocWebViewerFeature
import ru.tensor.sbis.person_decl.employee.person_card.PersonCardProvider
import ru.tensor.sbis.person_decl.employee.person_card.factory.PersonCardFragmentFactory
import ru.tensor.sbis.toolbox_decl.linkopener.OpenLinkController
import ru.tensor.sbis.info_decl.news.ui.NewsActivityProvider
import ru.tensor.sbis.calendar_decl.schedule.ViolationActivityProvider
import ru.tensor.sbis.verification_decl.verification.ui.VerificationFragmentProvider
import ru.tensor.sbis.communication_decl.videocall.ui.CallActivityProvider
import ru.tensor.sbis.communication_decl.analytics.AnalyticsUtil
import ru.tensor.sbis.communication_decl.crm.CRMConversationFragmentFactory
import ru.tensor.sbis.communicator.common.conversation_preview.ConversationPreviewFragmentFactory
import ru.tensor.sbis.communicator.declaration.crm.providers.CRMConversationProvider
import ru.tensor.sbis.tasks.feature.DocumentFeature
import ru.tensor.sbis.info_decl.notification.NotificationListFragmentProvider
import ru.tensor.sbis.plugin_struct.Dependency
import ru.tensor.sbis.plugin_struct.Plugin
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.plugin_struct.feature.FeatureProvider
import ru.tensor.sbis.plugin_struct.feature.FeatureWrapper

/**
 * Плагин наивгации коммуникатора.
 *
 * @author da.zhukov
 */
object CommunicatorNavigationPlugin : Plugin<CommunicatorNavigationPlugin.CustomizationOptions> {

    private var notificationFeatureProvider: FeatureProvider<NotificationListFragmentProvider>? = null
    private var docWebViewerFeatureProvider: FeatureProvider<DocWebViewerFeature>? = null
    private var newsActivityFeatureProvider: FeatureProvider<NewsActivityProvider>? = null
    private var verificationFragmentFeatureProvider: FeatureProvider<VerificationFragmentProvider>? = null
    private var conversationFeatureProvider: FeatureProvider<ConversationProvider>? = null
    private var personCardFeatureProvider: FeatureProvider<PersonCardProvider>? = null
    private var violationActivityFeatureProvider: FeatureProvider<ViolationActivityProvider>? = null
    private var themesRegistryFragmentFactoryProvider: FeatureProvider<ThemesRegistryFragmentFactory>? = null
    private var openLinkControllerFeatureProvider: FeatureProvider<OpenLinkController.Provider>? = null
    private var personCardFragmentFactoryProvider: FeatureProvider<PersonCardFragmentFactory>? = null
    private var documentFeatureProvider: FeatureProvider<DocumentFeature>? = null
    private var callActivityFeatureProvider: FeatureProvider<CallActivityProvider>? = null
    private var analyticsUtilFeatureProvider: FeatureProvider<AnalyticsUtil.Provider>? = null
    private var crmConversationFeatureProvider: FeatureProvider<CRMConversationProvider>? = null
    private var crmConversationFragmentFactoryProvider: FeatureProvider<CRMConversationFragmentFactory>? = null
    internal lateinit var conversationPreviewFragmentFactoryProvider: FeatureProvider<ConversationPreviewFragmentFactory>

    override val api: Set<FeatureWrapper<out Feature>> = setOf(
        FeatureWrapper(CommunicatorRouter.Provider::class.java) { CommunicatorNavigationFacade },
        FeatureWrapper(CommunicatorHostRouter.Provider::class.java) { CommunicatorNavigationFacade },
        FeatureWrapper(CommunicatorConversationRouter.Provider::class.java) { CommunicatorNavigationFacade },
        FeatureWrapper(CommunicatorThemesRouter.Provider::class.java) { CommunicatorNavigationFacade },
        FeatureWrapper(CommunicatorDialogInformationRouter.Provider::class.java) { CommunicatorNavigationFacade }
    )

    @Suppress("DEPRECATION")
    override val dependency: Dependency = Dependency.Builder()
        .require(ConversationPreviewFragmentFactory::class.java) { conversationPreviewFragmentFactoryProvider = it }
        .optional(NotificationListFragmentProvider::class.java) { notificationFeatureProvider = it }
        .optional(DocWebViewerFeature::class.java) { docWebViewerFeatureProvider = it }
        .optional(NewsActivityProvider::class.java) { newsActivityFeatureProvider = it }
        .optional(VerificationFragmentProvider::class.java) { verificationFragmentFeatureProvider = it }
        .optional(ConversationProvider::class.java) { conversationFeatureProvider = it }
        .optional(PersonCardProvider::class.java) { personCardFeatureProvider = it }
        .optional(ViolationActivityProvider::class.java) { violationActivityFeatureProvider = it }
        .optional(ThemesRegistryFragmentFactory::class.java) { themesRegistryFragmentFactoryProvider = it }
        .optional(OpenLinkController.Provider::class.java) { openLinkControllerFeatureProvider = it }
        .optional(PersonCardFragmentFactory::class.java) { personCardFragmentFactoryProvider = it }
        .optional(DocumentFeature::class.java) { documentFeatureProvider = it }
        .optional(CallActivityProvider::class.java) { callActivityFeatureProvider = it }
        .optional(AnalyticsUtil.Provider::class.java) { analyticsUtilFeatureProvider = it }
        .optional(CRMConversationProvider::class.java) { crmConversationFeatureProvider = it }
        .optional(CRMConversationFragmentFactory::class.java) { crmConversationFragmentFactoryProvider = it }
        .build()

    override val customizationOptions = CustomizationOptions()

    override fun initialize() {
        val dependency = object : CommunicatorNavigationDependency {
            override val notificationFeature: NotificationListFragmentProvider?
                get() = notificationFeatureProvider?.get()

            override val docWebViewerFeature: DocWebViewerFeature? =
                docWebViewerFeatureProvider?.get()

            override val newsActivityProvider: NewsActivityProvider? =
                newsActivityFeatureProvider?.get()

            override val verificationFragmentProvider: VerificationFragmentProvider? =
                verificationFragmentFeatureProvider?.get()

            override val conversationProvider: ConversationProvider? =
                conversationFeatureProvider?.get()

            override val personCardProvider: PersonCardProvider? =
                personCardFeatureProvider?.get()

            override val violationActivityProvider: ViolationActivityProvider? =
                violationActivityFeatureProvider?.get()

            override val themesRegistryFragmentFactory: ThemesRegistryFragmentFactory? =
                themesRegistryFragmentFactoryProvider?.get()

            override val openLinkControllerProvider: OpenLinkController.Provider? =
                openLinkControllerFeatureProvider?.get()

            override val personCardFragmentFactory: PersonCardFragmentFactory? =
                personCardFragmentFactoryProvider?.get()

            override val documentFeature: DocumentFeature?
                get() = documentFeatureProvider?.get()

            override val callActivityProvider: CallActivityProvider? =
                callActivityFeatureProvider?.get()

            override val analyticsUtilProvider: AnalyticsUtil.Provider?
                get() = analyticsUtilFeatureProvider?.get()

            override val crmConversationProvider: CRMConversationProvider?
                get() = crmConversationFeatureProvider?.get()

            override val crmConversationFragmentFactory: CRMConversationFragmentFactory?
                get() = crmConversationFragmentFactoryProvider?.get()
        }

        CommunicatorNavigationFacade.configure(dependency)
    }

    /**
     * Опции плагина модуля навигации.
     */
    class CustomizationOptions internal constructor() {

        /**
         * Навигация с сохранением фраментов.
         * Необходимо выставлять если приложение использует стратегию отличающуюся от [NonCacheFragmentInstallationStrategy].
         */
        var navigationWithCachedFragment: Boolean = false
    }
}
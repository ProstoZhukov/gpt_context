package ru.tensor.sbis.communicator.crm.conversation

import ru.tensor.sbis.common.di.CommonSingletonComponent
import ru.tensor.sbis.common.util.NetworkUtils
import ru.tensor.sbis.communication_decl.analytics.AnalyticsUtil
import ru.tensor.sbis.communication_decl.communicator.media.MediaPlayerFeature
import ru.tensor.sbis.communication_decl.complain.ComplainDialogFragmentFeature
import ru.tensor.sbis.communicator.common.contract.CommunicatorCommonDependency
import ru.tensor.sbis.communicator.common.di.CommunicatorCommonComponent
import ru.tensor.sbis.communicator.common.push.MessagesPushManagerProvider
import ru.tensor.sbis.communicator.crm.conversation.contract.CRMConversationDependency
import ru.tensor.sbis.communicator.crm.conversation.di.singleton.CRMConversationSingletonComponent
import ru.tensor.sbis.communicator.declaration.crm.CRMHostRouter
import ru.tensor.sbis.communicator.declaration.crm.providers.CRMAnotherOperatorFragmentFactory
import ru.tensor.sbis.communication_decl.crm.CRMChatListFragmentFactory
import ru.tensor.sbis.communication_decl.crm.CRMConversationFragmentFactory
import ru.tensor.sbis.communication_decl.crm.CrmChannelListFragmentFactory
import ru.tensor.sbis.communicator.declaration.crm.providers.CRMConversationProvider
import ru.tensor.sbis.design.audio_player_view.view.message.contact.AudioMessageViewDataFactory
import ru.tensor.sbis.design.message_view.contact.MessageViewComponentsFactory
import ru.tensor.sbis.person_decl.employee.person_card.PersonCardProvider
import ru.tensor.sbis.plugin_struct.BasePlugin
import ru.tensor.sbis.plugin_struct.Dependency
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.plugin_struct.feature.FeatureProvider
import ru.tensor.sbis.plugin_struct.feature.FeatureWrapper
import ru.tensor.sbis.review.decl.ReviewFeature
import ru.tensor.sbis.verification_decl.login.LoginInterface
import ru.tensor.sbis.viewer.decl.slider.ViewerSliderIntentFactory

/**
 * Плагин чата CRM.
 *
 * @author da.zhukov
 */
object CRMConversationPlugin : BasePlugin<CRMConversationPlugin.CustomizationOptions>() {
    private val crmConversationFeature by lazy {
        CRMConversationFeatureFacade
    }

    private val singletonComponentHolder = object : CRMConversationSingletonComponent.Holder {
        override val crmConversationSingletonComponent: CRMConversationSingletonComponent by lazy {
            val dependency = object : CRMConversationDependency,
                CommunicatorCommonDependency by commonCommunicatorComponentProvider.get().dependency,
                ViewerSliderIntentFactory by viewerSliderIntentFactoryProvider.get(),
                LoginInterface by loginInterfaceProvider.get(),
                CRMConversationFragmentFactory by crmConversationFeature {

                override val messageViewComponentsFactory: MessageViewComponentsFactory =
                    messageViewComponentsFactoryProvider.get()

                override val audioMessageViewDataFactory: AudioMessageViewDataFactory? =
                    audioMessageViewDataFactoryProvider?.get()

                override val complainFragmentFeature: ComplainDialogFragmentFeature?
                    get() = complainDialogFragmentFeatureProvider?.get()

                override val crmChatListFragmentFactory: CRMChatListFragmentFactory?
                    get() = crmChatListFragmentFactoryProvider?.get()

                override val crmHostRouterFeatureProvider: CRMHostRouter.Provider?
                    get() = crmHostRouterProvider?.get()

                override val analyticsUtilProvider: AnalyticsUtil.Provider?
                    get() = analyticsUtilFeatureProvider?.get()

                override val reviewFeature: ReviewFeature?
                    get() = reviewFeatureProvider?.get()

                override val mediaPlayerFeature: MediaPlayerFeature?
                    get() = mediaPlayerFeatureProvider?.get()

                override val networkUtilsFeature: NetworkUtils?
                    get() = networkUtilsFeatureProvider?.get()
            }
            crmConversationDependency = dependency
            CRMConversationSingletonComponent.Initializer(dependency).init(commonCommunicatorComponentProvider.get())
        }
    }

    @get:JvmStatic
    internal val singletonComponent: CRMConversationSingletonComponent
        @JvmName("getSingletonComponent")
        get() = singletonComponentHolder.crmConversationSingletonComponent

    @JvmField
    internal var crmConversationDependency: CRMConversationDependency? = null
    internal lateinit var commonSingletonComponentProvider: FeatureProvider<CommonSingletonComponent>
    internal lateinit var communicatorPushKeyboardHelperProvider:  FeatureProvider<CommunicatorPushKeyboardHelper.Provider>

    private lateinit var commonCommunicatorComponentProvider: FeatureProvider<CommunicatorCommonComponent>
    private lateinit var viewerSliderIntentFactoryProvider: FeatureProvider<ViewerSliderIntentFactory>
    private lateinit var loginInterfaceProvider: FeatureProvider<LoginInterface>
    private lateinit var messageViewComponentsFactoryProvider: FeatureProvider<MessageViewComponentsFactory>
    internal var crmChannelsFragmentFactoryProvider: FeatureProvider<CrmChannelListFragmentFactory>? = null
    internal var crmAnotherOperatorFragmentFactoryProvider: FeatureProvider<CRMAnotherOperatorFragmentFactory>? = null
    internal var messagesPushManagerProvider: FeatureProvider<MessagesPushManagerProvider>? = null
    private var crmHostRouterProvider: FeatureProvider<CRMHostRouter.Provider>? = null
    private var complainDialogFragmentFeatureProvider: FeatureProvider<ComplainDialogFragmentFeature>? = null
    private var crmChatListFragmentFactoryProvider: FeatureProvider<CRMChatListFragmentFactory>? = null
    private var analyticsUtilFeatureProvider: FeatureProvider<AnalyticsUtil.Provider>? = null
    private var reviewFeatureProvider: FeatureProvider<ReviewFeature>? = null
    private var mediaPlayerFeatureProvider: FeatureProvider<MediaPlayerFeature>? = null
    private var audioMessageViewDataFactoryProvider: FeatureProvider<AudioMessageViewDataFactory>? = null
    internal var networkUtilsFeatureProvider: FeatureProvider<NetworkUtils>? = null
    internal var personCardProvider: FeatureProvider<PersonCardProvider>? = null

    override val api: Set<FeatureWrapper<out Feature>> = setOf(
        FeatureWrapper(CRMConversationSingletonComponent::class.java) { singletonComponent },
        FeatureWrapper(CRMConversationFragmentFactory::class.java) { crmConversationFeature },
        FeatureWrapper(CRMConversationProvider::class.java) { crmConversationFeature },
        FeatureWrapper(CRMAnotherOperatorFragmentFactory::class.java) { crmConversationFeature }
    )

    override val dependency: Dependency = Dependency.Builder()
        .require(CommonSingletonComponent::class.java) { commonSingletonComponentProvider = it }
        .require(CommunicatorCommonComponent::class.java) { commonCommunicatorComponentProvider = it }
        .require(ViewerSliderIntentFactory::class.java) { viewerSliderIntentFactoryProvider = it }
        .require(LoginInterface::class.java) { loginInterfaceProvider = it }
        .require(MessageViewComponentsFactory::class.java) { messageViewComponentsFactoryProvider = it }
        .require(CommunicatorPushKeyboardHelper.Provider::class.java)  { communicatorPushKeyboardHelperProvider = it }
        .optional(AudioMessageViewDataFactory::class.java) { audioMessageViewDataFactoryProvider = it }
        .optional(CrmChannelListFragmentFactory::class.java) { crmChannelsFragmentFactoryProvider = it }
        .optional(CRMAnotherOperatorFragmentFactory::class.java) { crmAnotherOperatorFragmentFactoryProvider = it }
        .optional(CRMChatListFragmentFactory::class.java) { crmChatListFragmentFactoryProvider = it }
        .optional(CRMHostRouter.Provider::class.java) { crmHostRouterProvider = it }
        .optional(ComplainDialogFragmentFeature::class.java) { complainDialogFragmentFeatureProvider = it }
        .optional(MessagesPushManagerProvider::class.java) { messagesPushManagerProvider = it }
        .optional(AnalyticsUtil.Provider::class.java) { analyticsUtilFeatureProvider = it }
        .optional(ReviewFeature::class.java) { reviewFeatureProvider = it }
        .optional(MediaPlayerFeature::class.java) { mediaPlayerFeatureProvider = it }
        .optional(PersonCardProvider::class.java) { personCardProvider = it }
        .optional(NetworkUtils::class.java) { networkUtilsFeatureProvider = it }
        .build()

    override val customizationOptions = CustomizationOptions()

    class CustomizationOptions internal constructor() {

        /**
         * Активна ли возможность пожаловаться на контент в чатах.
         */
        var complainEnabled: Boolean = true
    }
}
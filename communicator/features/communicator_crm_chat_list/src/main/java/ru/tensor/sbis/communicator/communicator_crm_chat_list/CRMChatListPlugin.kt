package ru.tensor.sbis.communicator.communicator_crm_chat_list

import android.annotation.SuppressLint
import ru.tensor.sbis.clients_feature.di.ClientsFeature
import ru.tensor.sbis.common.di.CommonSingletonComponent
import ru.tensor.sbis.common.util.NetworkUtils
import ru.tensor.sbis.communication_decl.analytics.AnalyticsUtil
import ru.tensor.sbis.communication_decl.crm.CRMChatListDefaultParams
import ru.tensor.sbis.communication_decl.selection.recipient.RecipientSelectionProvider
import ru.tensor.sbis.communicator.common.push.CRMChatPushSubscriberProvider
import ru.tensor.sbis.communicator.common.push.MessagesPushManagerProvider
import ru.tensor.sbis.communicator.communicator_crm_chat_list.CRMChatListMainScreenAddon.Companion.defaultVisibilitySourceProvider
import ru.tensor.sbis.communicator.communicator_crm_chat_list.ui.hostfragment.CRMChatListHostFragment
import ru.tensor.sbis.communicator.declaration.crm.CRMHostRouter
import ru.tensor.sbis.communication_decl.crm.CRMChatListFragmentFactory
import ru.tensor.sbis.communication_decl.crm.CRMConversationFragmentFactory
import ru.tensor.sbis.communication_decl.crm.CRMChatListHostFragmentFactory
import ru.tensor.sbis.communication_decl.crm.CrmChannelListFragmentFactory
import ru.tensor.sbis.main_screen_decl.MainScreenAddon
import ru.tensor.sbis.main_screen_decl.content.install.FragmentInstallationStrategy
import ru.tensor.sbis.main_screen_decl.content.install.NonCacheFragmentInstallationStrategy
import ru.tensor.sbis.plugin_struct.BasePlugin
import ru.tensor.sbis.plugin_struct.Dependency
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.plugin_struct.feature.FeatureProvider
import ru.tensor.sbis.plugin_struct.feature.FeatureWrapper
import ru.tensor.sbis.plugin_struct.requireIf
import ru.tensor.sbis.pushnotification.center.PushCenter

/**
 * Плагин реестра CRM чатов.
 *
 * @author da.zhukov
 */
object CRMChatListPlugin : BasePlugin<CRMChatListPlugin.CustomizationOptions>() {

    private val crmCRMChatListFeature by lazy {
        CRMChatListFeatureFacade
    }

    internal lateinit var networkUtils: FeatureProvider<NetworkUtils>
    internal lateinit var commonSingletonComponentProvider: FeatureProvider<CommonSingletonComponent>
    internal lateinit var crmConversationFeature: FeatureProvider<CRMConversationFragmentFactory>
    internal lateinit var recipientSelectionFeatureProvider: FeatureProvider<RecipientSelectionProvider>
    internal lateinit var clientsFeatureProvider: FeatureProvider<ClientsFeature>

    internal var messagesPushManagerProvider: FeatureProvider<MessagesPushManagerProvider>? = null
    internal var analyticsUtilProvider: FeatureProvider<AnalyticsUtil.Provider>? = null
    private var communicatorPushFeatureSubscriberProvider: FeatureProvider<CRMChatPushSubscriberProvider>? = null
    private var pushCenterProvider: FeatureProvider<PushCenter>? = null

    override val api: Set<FeatureWrapper<out Feature>> = setOf(
        FeatureWrapper(CRMChatListHostFragmentFactory::class.java) { crmCRMChatListFeature },
        FeatureWrapper(CRMHostRouter.Provider::class.java) { crmCRMChatListFeature },
        FeatureWrapper(CRMChatListFragmentFactory::class.java) { crmCRMChatListFeature },
        FeatureWrapper(CrmChannelListFragmentFactory::class.java) { crmCRMChatListFeature },
    )

    override val dependency: Dependency by lazy {
        Dependency.Builder()
            .require(CommonSingletonComponent::class.java) { commonSingletonComponentProvider = it }
            .require(NetworkUtils::class.java) { networkUtils = it }
            .require(CRMConversationFragmentFactory::class.java) { crmConversationFeature = it }
            .require(RecipientSelectionProvider::class.java) { recipientSelectionFeatureProvider = it }
            .require(ClientsFeature::class.java) { clientsFeatureProvider = it}
            .requireIf(customizationOptions.pushMessageHandlerEnabled, MessagesPushManagerProvider::class.java) {
                messagesPushManagerProvider = it
            }
            .requireIf(customizationOptions.pushMessageHandlerEnabled, CRMChatPushSubscriberProvider::class.java) {
                communicatorPushFeatureSubscriberProvider = it
            }
            .requireIf(customizationOptions.pushMessageHandlerEnabled, PushCenter::class.java) {
                pushCenterProvider = it
            }
            .optional(AnalyticsUtil.Provider::class.java) { analyticsUtilProvider = it }
            .build()
    }

    override val customizationOptions = CustomizationOptions()

    @SuppressLint("CheckResult")
    override fun doAfterInitialize() {
        if (customizationOptions.pushMessageHandlerEnabled) {
            val messagesPushManager = messagesPushManagerProvider!!.get().messagesPushManager
            communicatorPushFeatureSubscriberProvider!!.get().getCRMChatPushSubscriber(
                application,
                messagesPushManager
            ).subscribe(pushCenterProvider!!.get())
        }
    }

    fun createAddon(
        fragmentInstallationStrategy: FragmentInstallationStrategy = NonCacheFragmentInstallationStrategy()
    ): MainScreenAddon {
        return CRMChatListMainScreenAddon(
            defaultVisibilitySourceProvider(),
            fragmentInstallationStrategy
        ) {
            CRMChatListHostFragment.createCRMChatListHostFragment(CRMChatListDefaultParams(it))
        }
    }

    class CustomizationOptions internal constructor() {
        /**
         * Активирован ли обработчик пуш-уведомлений.
         */
        var pushMessageHandlerEnabled: Boolean = false
    }
}

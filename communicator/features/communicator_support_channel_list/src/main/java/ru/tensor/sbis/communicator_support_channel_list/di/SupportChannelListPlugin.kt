package ru.tensor.sbis.communicator_support_channel_list.di

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import io.reactivex.disposables.Disposable
import ru.tensor.sbis.android_ext_decl.MainActivityProvider
import ru.tensor.sbis.common.di.CommonSingletonComponent
import ru.tensor.sbis.communicator.common.push.CommunicatorPushSubscriberProvider
import ru.tensor.sbis.communicator.common.push.MessagesPushManagerProvider
import ru.tensor.sbis.communicator.declaration.communicator_support_channel_list.SabyGetChannelListFragmentFactory
import ru.tensor.sbis.communicator.declaration.communicator_support_channel_list.SabyGetConsultationListFragmentFactory
import ru.tensor.sbis.communicator.declaration.communicator_support_channel_list.SupportChannelListFragmentFactory
import ru.tensor.sbis.communication_decl.crm.CRMConversationFragmentFactory
import ru.tensor.sbis.communicator.declaration.crm.providers.CRMConversationProvider
import ru.tensor.sbis.communicator_support_channel_list.feature.SupportChannelsMainScreenAddon
import ru.tensor.sbis.communicator_support_channel_list.feature.SupportComponentConfig
import ru.tensor.sbis.communicator_support_channel_list.presentation.SupportChannelListDetailFragment
import ru.tensor.sbis.communicator_support_channel_list.presentation.SupportChannelListHostFragment
import ru.tensor.sbis.communicator_support_consultation_list.di.SupportConsultationListPlugin
import ru.tensor.sbis.communicator_support_consultation_list.feature.SupportConsultationListFragmentFactory
import ru.tensor.sbis.company_details_decl.CompanyDetailsLiteFragmentProvider
import ru.tensor.sbis.consultations.generated.SupportChatsType
import ru.tensor.sbis.main_screen_decl.MainScreenAddon
import ru.tensor.sbis.main_screen_decl.content.install.FragmentInstallationStrategy
import ru.tensor.sbis.main_screen_decl.content.install.NonCacheFragmentInstallationStrategy
import ru.tensor.sbis.plugin_struct.BasePlugin
import ru.tensor.sbis.plugin_struct.Dependency
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.plugin_struct.feature.FeatureProvider
import ru.tensor.sbis.plugin_struct.feature.FeatureWrapper
import ru.tensor.sbis.pushnotification.center.PushCenter
import ru.tensor.sbis.verification_decl.login.LoginInterface
import ru.tensor.sbis.verification_decl.login.event.AuthEvent
import ru.tensor.sbis.viper.arch.provider.ContainerFrameIdProvider

/**
 * Плагин для создания фабрики фрагмнента с каналами поддержки
 * @see SupportChannelListFragmentFactory
 */
object SupportChannelListPlugin : BasePlugin<SupportChannelListPlugin.CustomizationOptions>() {

    private lateinit var commonSingletonComponentProvider: FeatureProvider<CommonSingletonComponent>
    private lateinit var communicatorSupportRequestsListFeature: FeatureProvider<SupportConsultationListFragmentFactory>
    private lateinit var crmConversationFeature: FeatureProvider<CRMConversationFragmentFactory>
    private lateinit var crmConversationProvider: FeatureProvider<CRMConversationProvider>
    private lateinit var mainActivityProvider: FeatureProvider<MainActivityProvider>
    private lateinit var loginInterfaceProvider: FeatureProvider<LoginInterface.Provider>

    internal var companyDetailsFragmentProvider: FeatureProvider<CompanyDetailsLiteFragmentProvider>? = null
    internal var containerFrameIdProvider: FeatureProvider<ContainerFrameIdProvider>? = null
    internal var messagesPushManagerProvider: FeatureProvider<MessagesPushManagerProvider>? = null
    internal lateinit var communicatorPushKeyboardHelperProvider:  FeatureProvider<CommunicatorPushKeyboardHelper.Provider>
    private var pushCenterProvider: FeatureProvider<PushCenter>? = null
    private var communicatorPushFeatureSubscriberProvider: FeatureProvider<CommunicatorPushSubscriberProvider>? = null

    private val clientSupportVisibility = MutableLiveData<Boolean>()

    override val api: Set<FeatureWrapper<out Feature>> = mutableSetOf<FeatureWrapper<out Feature>>(
        FeatureWrapper(SupportChannelListFragmentFactory::class.java) { component.getFeature() },
        FeatureWrapper(SabyGetChannelListFragmentFactory::class.java) { SupportChannelListHostFragment.Companion },
        FeatureWrapper(SabyGetConsultationListFragmentFactory::class.java) { SupportChannelListDetailFragment.Companion }
    ).apply {
        addAll(SupportConsultationListPlugin.api)
    }

    fun createAddon(fragmentInstallationStrategy: FragmentInstallationStrategy = NonCacheFragmentInstallationStrategy()): MainScreenAddon =
        SupportChannelsMainScreenAddon(
            clientSupportVisibility,
            fragmentInstallationStrategy
        ) {
            SupportChannelListHostFragment.newInstance(SupportComponentConfig.ClientSupport, it)
        }

    override val dependency: Dependency =
        Dependency.Builder()
            .require(SupportConsultationListFragmentFactory::class.java) { communicatorSupportRequestsListFeature = it }
            .require(CommonSingletonComponent::class.java) { commonSingletonComponentProvider = it }
            .require(CRMConversationFragmentFactory::class.java) { crmConversationFeature = it }
            .require(CRMConversationProvider::class.java) { crmConversationProvider = it }
            .require(MainActivityProvider::class.java) { mainActivityProvider = it }
            .require(LoginInterface.Provider::class.java) { loginInterfaceProvider = it }
            .require(CommunicatorPushSubscriberProvider::class.java) { communicatorPushFeatureSubscriberProvider = it }
            .require(PushCenter::class.java) { pushCenterProvider = it }
            .require(MessagesPushManagerProvider::class.java) { messagesPushManagerProvider = it }
            .require(CommunicatorPushKeyboardHelper.Provider::class.java)  { communicatorPushKeyboardHelperProvider = it }
            .optional(CompanyDetailsLiteFragmentProvider::class.java) { companyDetailsFragmentProvider = it }
            .optional(ContainerFrameIdProvider::class.java) { containerFrameIdProvider = it }
            .build()

    override val customizationOptions = CustomizationOptions()

    internal val component: SupportChatListComponent by lazy {
        DaggerSupportChatListComponent.factory().newComponent(
            communicatorSupportRequestsListFeature,
            crmConversationFeature,
            crmConversationProvider,
            mainActivityProvider,
            commonSingletonComponentProvider.get()
        )
    }

    @SuppressLint("CheckResult")
    override fun doAfterInitialize() {
        subscribeOnAuthEvent()

        if (customizationOptions.needRegistryPushSubscription) {
            val messagesPushManager = messagesPushManagerProvider!!.get().messagesPushManager
            communicatorPushFeatureSubscriberProvider!!.get().getSabySupportSubscriber(
                application,
                messagesPushManager
            ).subscribe(pushCenterProvider!!.get())
        }
    }

    private fun subscribeOnAuthEvent(): Disposable =
        loginInterfaceProvider.get().loginInterface.eventsObservable
            .filter { it.eventType == AuthEvent.EventType.LOGIN || it.eventType == AuthEvent.EventType.AUTHORIZED }
            .flatMap {
                component.registryAccessibilityServiceFactory.create(SupportChatsType.CLIENT_SUPPORT)
                    .isClientSupportAvailableObservable()
            }
            .subscribe { isSourcesNotEmpty ->
                clientSupportVisibility.postValue(isSourcesNotEmpty)
            }
    class CustomizationOptions internal constructor() {
        /**
         * Необходимо ли регистрировать обработчик пушей.
         * Не нужен для sabyget/brand.
         */
        var needRegistryPushSubscription: Boolean = true
    }
}
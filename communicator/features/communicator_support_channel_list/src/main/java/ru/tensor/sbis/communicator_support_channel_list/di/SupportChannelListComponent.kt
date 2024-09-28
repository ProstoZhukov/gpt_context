package ru.tensor.sbis.communicator_support_channel_list.di

import dagger.Binds
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableSharedFlow
import ru.tensor.sbis.android_ext_decl.MainActivityProvider
import ru.tensor.sbis.common.di.CommonSingletonComponent
import ru.tensor.sbis.communicator.declaration.communicator_support_channel_list.SupportChannelListFragmentFactory
import ru.tensor.sbis.communication_decl.crm.CRMConversationFragmentFactory
import ru.tensor.sbis.communicator.declaration.crm.providers.CRMConversationProvider
import ru.tensor.sbis.communicator_support_channel_list.data.ChannelCollectionCrud3ServiceWrapperImplFactory
import ru.tensor.sbis.communicator_support_channel_list.data.SupportUnreadCounterProvider
import ru.tensor.sbis.communicator_support_channel_list.feature.SupportChannelListFragmentFactoryImpl
import ru.tensor.sbis.communicator_support_channel_list.interactor.SupportChatsServiceFactory
import ru.tensor.sbis.communicator_support_channel_list.mapper.SupportChannelsMapperFactory
import ru.tensor.sbis.communicator_support_channel_list.presentation.SupportChannelListDetailFragment
import ru.tensor.sbis.communicator_support_channel_list.presentation.SupportChannelListFragment
import ru.tensor.sbis.communicator_support_channel_list.presentation.SupportChannelListHostFragment
import ru.tensor.sbis.communicator_support_channel_list.presentation.SupportChannelRouterFactory
import ru.tensor.sbis.communicator_support_channel_list.viewmodel.accessibility.RegistryAccessibilityServiceFactory
import ru.tensor.sbis.communicator_support_channel_list.viewmodel.accessibility.RegistryAccessibilityServiceImpl
import ru.tensor.sbis.communicator_support_consultation_list.feature.SupportConsultationListFragmentFactory
import ru.tensor.sbis.consultations.generated.ConsultationService
import ru.tensor.sbis.consultations.generated.SupportChatsCollectionProvider
import ru.tensor.sbis.consultations.generated.SupportChatsService
import ru.tensor.sbis.consultations.generated.SupportChatsType
import ru.tensor.sbis.plugin_struct.feature.FeatureProvider
import ru.tensor.sbis.service.generated.StubType

/**
 * Компонент для фрагмента реестра каналов техподдержки
 */
@SupportChannelScope
@Component(
    modules = [FeatureModule::class, Crud3Module::class, DependencyModule::class, BindsModule::class],
    dependencies = [CommonSingletonComponent::class]

)
internal interface SupportChatListComponent {

    fun getFeature(): SupportChannelListFragmentFactory

    fun supportChannelRouterFactory(): SupportChannelRouterFactory

    fun inject(fragment: SupportChannelListFragment)

    fun inject(fragment: SupportChannelListHostFragment)

    fun inject(fragment: SupportChannelListDetailFragment)

    val supportChatsMapperFactory: SupportChannelsMapperFactory

    val registryAccessibilityServiceFactory: RegistryAccessibilityServiceFactory

    val channelCollectionCrud3ServiceWrapperImplFactory: ChannelCollectionCrud3ServiceWrapperImplFactory

    @Component.Factory
    interface Factory {
        fun newComponent(
            @BindsInstance supportRequestsListFeatureProvider: FeatureProvider<SupportConsultationListFragmentFactory>,
            @BindsInstance crmConversationFeature: FeatureProvider<CRMConversationFragmentFactory>,
            @BindsInstance crmConversationProvider: FeatureProvider<CRMConversationProvider>,
            @BindsInstance mainActivityProvider: FeatureProvider<MainActivityProvider>,
            commonSingletonComponent: CommonSingletonComponent
        ): SupportChatListComponent
    }
}

/**
 * Модуль для предоставления реализации фичи
 */
@Module
internal class FeatureModule {
    @Provides
    fun provideFeature(
        supportUnreadCounterProvider: SupportUnreadCounterProvider,
        registryAccessibilityUseCase: RegistryAccessibilityServiceFactory,
        mainActivityProvider: FeatureProvider<MainActivityProvider>,
        crmConversationProvider: FeatureProvider<CRMConversationProvider>
    ): SupportChannelListFragmentFactory =
        SupportChannelListFragmentFactoryImpl(
            supportUnreadCounterProvider,
            registryAccessibilityUseCase.create(SupportChatsType.SABY_SUPPORT),
            mainActivityProvider.get(),
            crmConversationProvider.get()
        )
}

/**
 * Модуль для предоставления зависимостей для работы с crud3
 */
@Module
internal class Crud3Module {
    @Provides
    fun provideSupportChatsCollectionProvider(): SupportChatsCollectionProvider {
        return SupportChatsCollectionProvider.instance()
    }

    @Provides
    fun provideSupportChatsServiceFactory(): SupportChatsServiceFactory {
        return object : SupportChatsServiceFactory {
            override fun create(type: SupportChatsType): SupportChatsService =
                SupportChatsService.instance(type)
        }
    }

    @Provides
    fun provideConsultationService(): ConsultationService {
        return ConsultationService.instance()
    }
}

/**
 * Модуль для  предоставления зависимостей
 */
@Module
internal class DependencyModule {
    @Provides
    fun provideMapperForListCrud3(supportRequestsListFeatureProvider: FeatureProvider<SupportConsultationListFragmentFactory>): SupportConsultationListFragmentFactory {
        return supportRequestsListFeatureProvider.get()
    }

    @Provides
    fun provideCRMConversationFeature(provider: FeatureProvider<CRMConversationFragmentFactory>): CRMConversationFragmentFactory {
        return provider.get()
    }

    @Provides
    fun provideRegistryAccessibilityUseCase(
        supportChatsServiceFactory: SupportChatsServiceFactory
    ): RegistryAccessibilityServiceFactory {
        return object : RegistryAccessibilityServiceFactory {
            override fun create(supportChatsType: SupportChatsType): RegistryAccessibilityServiceImpl {
                return RegistryAccessibilityServiceImpl(
                    supportChatsServiceFactory,
                    supportChatsType
                )
            }
        }
    }
}

@Module
internal interface BindsModule {
    @Binds
    fun provideOnStubFlowCollector(flow: MutableSharedFlow<StubType>): FlowCollector<StubType>
}
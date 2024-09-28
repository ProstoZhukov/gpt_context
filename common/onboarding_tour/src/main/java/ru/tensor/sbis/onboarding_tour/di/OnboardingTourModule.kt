package ru.tensor.sbis.onboarding_tour.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import ru.tensor.sbis.onboarding_tour.contract.OnboardingTourDependency
import ru.tensor.sbis.onboarding_tour.data.storage.TourProgressDataStore
import ru.tensor.sbis.onboarding_tour.data.storage.TourProgressDataStoreHelper
import ru.tensor.sbis.onboarding_tour.domain.DevicePerformanceProviderImpl
import ru.tensor.sbis.onboarding_tour.domain.DispatcherProvider
import ru.tensor.sbis.onboarding_tour.domain.TourCreatorImpl
import ru.tensor.sbis.onboarding_tour.domain.TourEventsProviderImpl
import ru.tensor.sbis.onboarding_tour.domain.TourFragmentCreator
import ru.tensor.sbis.verification_decl.login.LoginInterface
import ru.tensor.sbis.verification_decl.onboarding_tour.DevicePerformanceProvider
import ru.tensor.sbis.verification_decl.onboarding_tour.OnboardingTourCreator
import ru.tensor.sbis.verification_decl.onboarding_tour.OnboardingTourEventsProvider
import ru.tensor.sbis.verification_decl.onboarding_tour.OnboardingTourFragmentProvider
import ru.tensor.sbis.verification_decl.permission.PermissionFeature

/**
 * @author as.chadov
 */
@Suppress("unused")
@Module
internal abstract class OnboardingTourModule {

    @Binds
    abstract fun provideCreator(impl: TourCreatorImpl): OnboardingTourCreator

    @Binds
    abstract fun provideFragmentProvider(impl: TourFragmentCreator): OnboardingTourFragmentProvider

    @Binds
    abstract fun provideTourProgressDataStore(impl: TourProgressDataStoreHelper): TourProgressDataStore

    @Binds
    abstract fun provideDevicePerformance(impl: DevicePerformanceProviderImpl): DevicePerformanceProvider

    @Binds
    abstract fun provideTourEventsProvider(impl: TourEventsProviderImpl): OnboardingTourEventsProvider

    @Suppress("unused")
    companion object {

        @Provides
        fun provideLoginInterface(dependency: OnboardingTourDependency): LoginInterface? =
            dependency.loginInterface

        @Provides
        fun providePermissionFeature(dependency: OnboardingTourDependency): PermissionFeature? =
            dependency.permissionFeature

        @Provides
        fun provideDispatcherProvider(): DispatcherProvider = DispatcherProvider()
    }
}
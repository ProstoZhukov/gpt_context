package ru.tensor.sbis.onboarding.di

import android.content.Context
import android.content.SharedPreferences
import dagger.Binds
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import ru.tensor.sbis.android_ext_decl.MainActivityProvider
import ru.tensor.sbis.onboarding.contract.OnboardingDependency
import ru.tensor.sbis.onboarding.contract.impl.NavigatorManagerImpl
import ru.tensor.sbis.onboarding.contract.impl.OnboardingFeatureImpl
import ru.tensor.sbis.onboarding.contract.impl.OnboardingNavigatorManager
import ru.tensor.sbis.onboarding.ui.utils.ONBOARDING_SHARED_PREFERENCES
import ru.tensor.sbis.onboarding.ui.utils.OnboardingPreferenceManager
import ru.tensor.sbis.onboarding.ui.utils.OnboardingPreferenceManagerDefault
import ru.tensor.sbis.onboarding.ui.utils.OnboardingProviderMediator
import ru.tensor.sbis.verification_decl.login.LoginInterface
import ru.tensor.sbis.verification_decl.onboarding.OnboardingFeature
import javax.inject.Named
import javax.inject.Singleton

/**
 * @author as.chadov
 */
@Suppress("unused")
@Module
internal abstract class OnboardingSingletonModule {

    @Singleton
    @Binds
    internal abstract fun provideOnboardingFeature(impl: OnboardingFeatureImpl): OnboardingFeature

    @Singleton
    @Binds
    internal abstract fun provideMainActivityProvider(impl: OnboardingDependency): MainActivityProvider

    @Singleton
    @Binds
    internal abstract fun provideNavigatorManager(impl: NavigatorManagerImpl): OnboardingNavigatorManager

    @Singleton
    @Binds
    internal abstract fun provideOnboardingPreferenceManager(impl: OnboardingPreferenceManagerDefault): OnboardingPreferenceManager

    @Suppress("unused")
    companion object {

        @Provides
        fun provideCoroutineDispatcher(): CoroutineDispatcher = Dispatchers.IO

        @Singleton
        @Provides
        @Named(ONBOARDING_SHARED_PREFERENCES)
        fun provideSharedPreferences(
            context: Context,
        ): SharedPreferences =
            context.getSharedPreferences(ONBOARDING_SHARED_PREFERENCES, Context.MODE_PRIVATE)

        @Singleton
        @Provides
        fun provideOnboardingProviderMediator(
            preferenceManager: OnboardingPreferenceManager,
            dependency: OnboardingDependency,
            loginInterface: LoginInterface?,
            ioDispatcher: CoroutineDispatcher
        ): OnboardingProviderMediator =
            OnboardingProviderMediator(
                preferenceManager,
                dependency.getOnboardingProviders(),
                loginInterface,
                ioDispatcher
            )
    }
}
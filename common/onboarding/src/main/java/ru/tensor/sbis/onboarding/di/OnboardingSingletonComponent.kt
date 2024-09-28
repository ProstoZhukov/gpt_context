package ru.tensor.sbis.onboarding.di

import android.content.Context
import androidx.annotation.Nullable
import dagger.BindsInstance
import dagger.Component
import ru.tensor.sbis.android_ext_decl.MainActivityProvider
import ru.tensor.sbis.verification_decl.login.LoginInterface
import ru.tensor.sbis.verification_decl.onboarding.OnboardingFeature
import ru.tensor.sbis.verification_decl.permission.PermissionFeature
import ru.tensor.sbis.onboarding.contract.OnboardingDependency
import ru.tensor.sbis.onboarding.contract.impl.OnboardingNavigatorManager
import ru.tensor.sbis.onboarding.domain.OnboardingRepository
import ru.tensor.sbis.onboarding.domain.holder.PermissionFeatureHolder
import ru.tensor.sbis.onboarding.ui.utils.OnboardingPreferenceManager
import ru.tensor.sbis.onboarding.ui.utils.OnboardingProviderMediator
import javax.inject.Singleton

/**
 * DI комопнент модуля приветственного экрана
 *
 * @author as.chadov
 */
@Singleton
@Component(modules = [OnboardingSingletonModule::class])
abstract class OnboardingSingletonComponent : OnboardingComponentContract {

    abstract fun getOnboardingFeature(): OnboardingFeature

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance context: Context,
            @BindsInstance dependency: OnboardingDependency,
            @Nullable @BindsInstance permissionFeature: PermissionFeature?,
            @Nullable @BindsInstance loginFeature: LoginInterface?
        ): OnboardingSingletonComponent
    }
}

internal interface OnboardingComponentContract {

    fun getContext(): Context

    fun getMainActivityProvider(): MainActivityProvider

    fun getOnboardingRepository(): OnboardingRepository

    fun getOnboardingPreferenceManager(): OnboardingPreferenceManager

    fun getNavigatorManager(): OnboardingNavigatorManager

    fun getPermissionFeatureHolder(): PermissionFeatureHolder

    fun getOnboardingProviderMediator(): OnboardingProviderMediator
}
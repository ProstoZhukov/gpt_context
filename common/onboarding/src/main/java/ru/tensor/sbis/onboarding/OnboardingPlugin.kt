package ru.tensor.sbis.onboarding

import android.content.Intent
import ru.tensor.sbis.android_ext_decl.MainActivityProvider
import ru.tensor.sbis.onboarding.contract.OnboardingDependency
import ru.tensor.sbis.onboarding.contract.providers.OnboardingProvider
import ru.tensor.sbis.onboarding.di.OnboardingSingletonComponent
import ru.tensor.sbis.onboarding.di.OnboardingSingletonComponentInitializer
import ru.tensor.sbis.plugin_struct.BasePlugin
import ru.tensor.sbis.plugin_struct.Dependency
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.plugin_struct.feature.FeatureProvider
import ru.tensor.sbis.plugin_struct.feature.FeatureWrapper
import ru.tensor.sbis.verification_decl.login.LoginInterface
import ru.tensor.sbis.verification_decl.onboarding.OnboardingFeature
import ru.tensor.sbis.verification_decl.permission.PermissionFeature

/**
 * Плагин для onboarding
 *
 * @author kv.martyshenko
 */
object OnboardingPlugin : BasePlugin<OnboardingPlugin.CustomizationOptions>() {

    private lateinit var mainActivityProvider: FeatureProvider<MainActivityProvider>
    private lateinit var onboardingProviders: Set<FeatureProvider<OnboardingProvider>>
    private var permissionFeature: FeatureProvider<PermissionFeature>? = null
    private var loginInterface: FeatureProvider<LoginInterface>? = null

    override val api: Set<FeatureWrapper<out Feature>> = setOf(
        FeatureWrapper(OnboardingFeature::class.java) { onboardingSingletonComponent.getOnboardingFeature() }
    )

    override val dependency: Dependency by lazy {
        Dependency.Builder()
            .require(MainActivityProvider::class.java) { mainActivityProvider = it }
            .requireSet(OnboardingProvider::class.java) { onboardingProviders = it }
            .apply {
                if (customizationOptions.permissionScopeCheckEnabled) {
                    require(PermissionFeature::class.java) { permissionFeature = it }
                }
                if (customizationOptions.userAware) {
                    require(LoginInterface::class.java) { loginInterface = it }
                }
            }
            .build()
    }

    override val customizationOptions: CustomizationOptions = CustomizationOptions()

    internal val onboardingSingletonComponent: OnboardingSingletonComponent by lazy {
        val dependency = object : OnboardingDependency {
            override fun getOnboardingProviders(): Set<FeatureProvider<OnboardingProvider>> = onboardingProviders

            override fun getMainActivityIntent(): Intent = mainActivityProvider.get().getMainActivityIntent()
        }
        OnboardingSingletonComponentInitializer(
            context = application,
            dependency = dependency,
            permission = permissionFeature?.get(),
            login = loginInterface?.get()
        ).init()
    }

    /**
     * Конфигурация плагина
     */
    class CustomizationOptions internal constructor() {

        /**
         * Поддержки страниц заглушек об отсутствии прав на область
         */
        var permissionScopeCheckEnabled: Boolean = true

        /**
         * Поддержки персонализации события отображения онбординга
         * Если включено, каждому новому пользователю будет показан онбординг
         * Если выключено, только один раз, независимо от пользователя
         */
        var userAware: Boolean = true
    }
}
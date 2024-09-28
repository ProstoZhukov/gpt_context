package ru.tensor.sbis.onboarding_tour

import ru.tensor.sbis.onboarding_tour.contract.OnboardingTourDependency
import ru.tensor.sbis.onboarding_tour.contract.OnboardingTourFacade
import ru.tensor.sbis.onboarding_tour.di.DaggerOnboardingTourComponent
import ru.tensor.sbis.onboarding_tour.di.OnboardingTourComponent
import ru.tensor.sbis.plugin_struct.BasePlugin
import ru.tensor.sbis.plugin_struct.Dependency
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.plugin_struct.feature.FeatureProvider
import ru.tensor.sbis.plugin_struct.feature.FeatureWrapper
import ru.tensor.sbis.verification_decl.login.LoginInterface
import ru.tensor.sbis.verification_decl.onboarding.OnboardingFeature
import ru.tensor.sbis.verification_decl.onboarding_tour.*
import ru.tensor.sbis.verification_decl.permission.PermissionFeature

/**
 * Плагин для приветственного экрана настроек приложения.
 *
 * @author as.chadov
 */
object OnboardingTourPlugin : BasePlugin<OnboardingTourPlugin.CustomizationOptions>() {

    private lateinit var tourFeatureProviders: Set<FeatureProvider<OnboardingTourProvider>>
    private var loginInterfaceFeatureProvider: FeatureProvider<LoginInterface>? = null
    private var permissionFeatureProvider: FeatureProvider<PermissionFeature>? = null
    private var onboardingFeatureProvider: FeatureProvider<OnboardingFeature>? = null

    private val onboardingTourFacade: OnboardingTourFacade by lazy(::OnboardingTourFacade)
    internal val dependencies: OnboardingTourDependency by lazy(::createDependency)

    internal val tourComponent: OnboardingTourComponent by lazy {
        DaggerOnboardingTourComponent.factory().create(
            application,
            dependencies
        )
    }

    override val api: Set<FeatureWrapper<out Feature>> = setOf(
        FeatureWrapper(OnboardingTourFeature::class.java) { onboardingTourFacade },
        FeatureWrapper(OnboardingTourFragmentProvider::class.java) { onboardingTourFacade },
        FeatureWrapper(OnboardingTourCreator.Provider::class.java) { onboardingTourFacade },
        FeatureWrapper(DevicePerformanceProvider::class.java) { onboardingTourFacade.devicePerformanceProvider },
        FeatureWrapper(OnboardingTourEventsProvider::class.java) { onboardingTourFacade.onboardingTourEventsProvider }
    )

    override val dependency: Dependency by lazy {
        Dependency.Builder()
            .requireSet(OnboardingTourProvider::class.java) { tourFeatureProviders = it }
            .optional(LoginInterface::class.java) { loginInterfaceFeatureProvider = it }
            .optional(PermissionFeature::class.java) { permissionFeatureProvider = it }
            .optional(OnboardingFeature::class.java) { onboardingFeatureProvider = it }
            .build()
    }

    override val customizationOptions = CustomizationOptions()

    override fun doAfterInitialize() = Unit

    private fun createDependency(): OnboardingTourDependency = object : OnboardingTourDependency {
        override val loginInterface get() = loginInterfaceFeatureProvider?.get()
        override val permissionFeature get() = permissionFeatureProvider?.get()
        override val onboardingFeature get() = onboardingFeatureProvider?.get()
        override val tourFeatureProviderSet get() = tourFeatureProviders
    }

    class CustomizationOptions internal constructor() {
        /**
         * Доступен ли компонент при запуске в режиме автотестов.
         */
        var allowInAutoTest = false
    }
}
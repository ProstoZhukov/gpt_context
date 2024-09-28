package ru.tensor.sbis.design.navigation

import android.annotation.SuppressLint
import io.reactivex.Observable
import ru.tensor.sbis.design.navigation.view.model.NavigationPreferences
import ru.tensor.sbis.plugin_struct.BasePlugin
import ru.tensor.sbis.plugin_struct.Dependency
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.plugin_struct.feature.FeatureProvider
import ru.tensor.sbis.plugin_struct.feature.FeatureWrapper
import ru.tensor.sbis.toolbox_decl.apptools.AutotestsLaunchStatusProvider
import ru.tensor.sbis.verification_decl.login.AuthEventsObservableProvider
import ru.tensor.sbis.verification_decl.login.event.AuthEvent
import ru.tensor.sbis.verification_decl.onboarding.OnboardingFeature
import ru.tensor.sbis.verification_decl.onboarding_tour.OnboardingTourEventsProvider
import ru.tensor.sbis.toolbox_decl.apptools.AutotestsParametersProvider

/**
 * Плагин модуля ННП и Аккордеона
 *
 * @author us.bessonov
 */
object NavigationPlugin : BasePlugin<NavigationPlugin.CustomizationOptions>() {

    private var authEventsObservableProvider: FeatureProvider<AuthEventsObservableProvider>? = null
    private var onboardingFeatureProvider: FeatureProvider<OnboardingFeature>? = null
    private var onboardingTourEventsProvider: FeatureProvider<OnboardingTourEventsProvider>? = null
    internal var autotestsLaunchStatusProvider: FeatureProvider<AutotestsLaunchStatusProvider>? = null
        private set

    /**
     * Подписка на событие закрытия онбординга.
     */
    internal lateinit var onboardingCloseEventObservable: Observable<Unit>
        private set

    /**
     * @see NavigationPreferences
     */
    internal lateinit var navigationPreferences: NavigationPreferences
        private set
    internal var autotestsParametersProvider: FeatureProvider<AutotestsParametersProvider>? = null
        private set

    override val api: Set<FeatureWrapper<out Feature>> = emptySet()
    override val customizationOptions = CustomizationOptions()
    override val dependency: Dependency = Dependency.Builder()
        .optional(AuthEventsObservableProvider::class.java) { authEventsObservableProvider = it }
        .optional(OnboardingFeature::class.java) { onboardingFeatureProvider = it }
        .optional(AutotestsParametersProvider::class.java) { autotestsParametersProvider = it }
        .optional(OnboardingTourEventsProvider::class.java) { onboardingTourEventsProvider = it }
        .optional(AutotestsLaunchStatusProvider::class.java) { autotestsLaunchStatusProvider = it }
        .build()

    override fun initialize() {
        super.initialize()
        navigationPreferences = NavigationPreferences(application)
    }

    override fun doAfterInitialize() {
        super.doAfterInitialize()

        /*
        При закрытии онбординга нужно показать аккордеон с анимацией. Аккордеон, как и онбординг
        показываются только при первом входе, поэтому достсточно завязаться за накрытие онбординга
         */
        onboardingCloseEventObservable = Observable.merge(
            onboardingFeatureProvider?.get()?.observeOnboardingCloseEvent() ?: Observable.never(),
            onboardingTourEventsProvider?.get()?.observeTourCloseEvent()?.toObservable() ?: Observable.never()
        )

        subscribeOnLogoutEvent()
    }

    @SuppressLint("CheckResult")
    private fun subscribeOnLogoutEvent() {
        authEventsObservableProvider?.let {
            it.get().eventsObservable
                .filter { ev -> ev.eventType == AuthEvent.EventType.LOGOUT }
                .subscribe { navigationPreferences.clear() }
        }
    }

    /**
     * Конфигурация плагина.
     */
    class CustomizationOptions internal constructor() {

        /**
         * Должны ли разворачиваемые элементы аккордеона быть развёрнуты по умолчанию
         */
        var areNavigationItemsExpandedByDefault = false

    }
}
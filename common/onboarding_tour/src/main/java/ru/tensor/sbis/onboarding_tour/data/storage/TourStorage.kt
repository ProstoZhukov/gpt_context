package ru.tensor.sbis.onboarding_tour.data.storage

import kotlinx.coroutines.withContext
import ru.tensor.sbis.application_tools.DebugTools
import ru.tensor.sbis.onboarding_tour.OnboardingTourPlugin
import ru.tensor.sbis.onboarding_tour.contract.OnboardingTourDependency
import ru.tensor.sbis.onboarding_tour.data.TourContent
import ru.tensor.sbis.onboarding_tour.domain.DispatcherProvider
import ru.tensor.sbis.plugin_struct.feature.FeatureProvider
import ru.tensor.sbis.verification_decl.login.LoginInterface
import ru.tensor.sbis.verification_decl.onboarding.OnboardingFeature
import ru.tensor.sbis.verification_decl.onboarding_tour.OnboardingTourProvider
import ru.tensor.sbis.verification_decl.onboarding_tour.data.DisplayBehavior
import ru.tensor.sbis.verification_decl.onboarding_tour.data.DisplayBehavior.PER_USER
import ru.tensor.sbis.verification_decl.onboarding_tour.data.OnboardingTour
import java.util.*
import javax.inject.Inject

/**
 * Хранилище набора провайдеров [OnboardingTourProvider] с содержимым Приветственных экранов настроект приложения.
 *
 * @author as.chadov
 */
internal class TourStorage @Inject constructor(
    private val progressStore: TourProgressDataStore,
    private val dispatcherProvider: DispatcherProvider,
    dependency: OnboardingTourDependency
) {

    private val loginInterface: LoginInterface? = dependency.loginInterface
    private val onboardingFeature: OnboardingFeature? = dependency.onboardingFeature
    private val allProviders =
        dependency.tourFeatureProviderSet.map(FeatureProvider<OnboardingTourProvider>::get).toSet()
    private val deactivatedTours = mutableSetOf<OnboardingTour.Name>()
    private var currentTour: Pair<OnboardingTour.Name, TourContent>? = null

    /**
     * Доступен ли [OnboardingTour] с именем [name].
     */
    fun hasTour(name: OnboardingTour.Name): Boolean =
        allProviders.any { it.name.value == name.value }

    /**
     * Доступен ли следующий активный в очереди на обработку [OnboardingTour].
     *
     * @return название [OnboardingTour.Name] следующего активного тура
     */
    suspend fun hasActiveTour(): OnboardingTour.Name? {
        if (DebugTools.isAutoTestLaunch && !OnboardingTourPlugin.customizationOptions.allowInAutoTest) {
            return null
        }
        return currentTour?.first ?: findCurrentTour()
    }

    /**
     * Получить [TourContent] с именем [name].
     *
     * @throws IllegalArgumentException если [OnboardingTour] с таким именем отсутствует
     */
    fun getTour(name: OnboardingTour.Name): TourContent {
        currentTour?.let {
            if (it.first == name) return it.second
        }
        val provider = allProviders.find { it.name.value == name.value }
        requireNotNull(provider)
        val content = provider.getTour() as TourContent
        currentTour = Pair(name, content)
        return content
    }

    /**
     * Деактивировать тур с именем [name].
     */
    suspend fun deactivateTour(name: OnboardingTour.Name) {
        deactivatedTours.add(name)
        if (name.value != currentTour?.first?.value) return
        val considerUser = currentTour?.second?.rules?.displayBehavior == PER_USER
        currentTour = null
        progressStore.putPreference(getProgressKey(name, considerUser).isCompletedKey, true)
    }

    private suspend fun findCurrentTour(): OnboardingTour.Name? {
        val orderedProviders = allProviders.sortedByDescending(OnboardingTourProvider::priority).toMutableList()
        orderedProviders.forEach { provider ->
            if (deactivatedTours.any { provider.name.value == it.value }) return@forEach
            val tour = provider.getTour()
            if (isActive(provider.name, tour)) {
                return provider.name
            } else {
                deactivatedTours.add(provider.name)
            }
        }
        return null
    }

    private suspend fun isActive(tourName: OnboardingTour.Name, tour: OnboardingTour): Boolean {
        require(tour is TourContent) { "Unsupported OnboardingTour type ${OnboardingTour::class.java.simpleName}" }
        if (tour.rules.showOnlyOnceConsideringOnboarding) {
            val shown = withContext(dispatcherProvider.io) {
                onboardingFeature?.isOnboardingTourShown() == true
            }
            if (shown) {
                return false
            }
        }
        if (tour.rules.displayBehavior == DisplayBehavior.ALWAYS) {
            return true
        }
        return withContext(dispatcherProvider.io) {
            val key = getProgressKey(tourName, tour.rules.displayBehavior == PER_USER).isCompletedKey
            progressStore.getPreference(key, false).not()
        }
    }

    private fun getProgressKey(tourName: OnboardingTour.Name, considerUser: Boolean): TourProgressConstants =
        if (considerUser && loginInterface != null) {
            val user = loginInterface.getCurrentAccount()?.run { personId ?: uuid.toString() }.orEmpty()
            TourProgressConstants(tourName, user)
        } else {
            TourProgressConstants(tourName)
        }
}

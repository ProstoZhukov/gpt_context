package ru.tensor.sbis.onboarding_tour.domain

import ru.tensor.sbis.application_tools.DebugTools
import ru.tensor.sbis.onboarding_tour.data.TourContent
import ru.tensor.sbis.onboarding_tour.data.storage.TourStorage
import ru.tensor.sbis.verification_decl.onboarding_tour.DevicePerformanceProvider
import ru.tensor.sbis.verification_decl.onboarding_tour.data.OnboardingTour
import javax.inject.Inject

/** @SelfDocumented */
internal class TourInteractor @Inject constructor(
    private val storage: TourStorage,
    private val performanceProvider: DevicePerformanceProvider,
    private val eventsProvider: TourEventsProviderImpl
) : TourEventsEmitter by eventsProvider {

    /**
     * Получить содержимое тура с именем [name].
     *
     * @param restoredId id активной/восстановленной страницы в туре
     */
    fun getContent(
        name: OnboardingTour.Name,
        restoredId: Int? = null
    ): TourContent {
        val tour = storage.getTour(name)
        val requiredPages = tour.pages.run {
            if (restoredId == null) {
                filter { it.requiredCommand() }
            } else {
                filter { page -> if (page.id == restoredId) true else page.requiredCommand() }
            }
        }
        return if (requiredPages.size != tour.pages.size) {
            var startPosition = tour.startPosition
            val sortedPages = requiredPages.mapIndexed { index, tourPage ->
                if (tourPage.id == restoredId) {
                    startPosition = index
                }
                tourPage.copy(position = index)
            }
            TourContent(sortedPages, startPosition, tour.rules, tour.command)
        } else if (restoredId != null && tour.pages.firstOrNull()?.id != restoredId) {
            val startPosition = tour.pages.find { it.id == restoredId }?.position
                ?.takeIf { it < tour.pages.size } ?: tour.startPosition
            TourContent(tour.pages, startPosition, tour.rules, tour.command)
        } else {
            tour
        }
    }

    /** Отметить тур пройденным. */
    suspend fun markShown(name: OnboardingTour.Name) {
        storage.deactivateTour(name)
    }

    /** Получить признак возможности анимирования тура. */
    fun isAnimated(): Boolean {
        if (DebugTools.isAutoTestLaunch) {
            return false
        }
        if (performanceProvider.isLowPerformanceDevice()) {
            return false
        }
        return true
    }
}
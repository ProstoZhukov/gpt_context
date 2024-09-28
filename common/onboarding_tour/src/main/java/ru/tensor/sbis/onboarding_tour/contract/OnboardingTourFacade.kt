package ru.tensor.sbis.onboarding_tour.contract

import ru.tensor.sbis.onboarding_tour.OnboardingTourPlugin
import ru.tensor.sbis.verification_decl.onboarding_tour.DevicePerformanceProvider
import ru.tensor.sbis.verification_decl.onboarding_tour.OnboardingTourCreator
import ru.tensor.sbis.verification_decl.onboarding_tour.OnboardingTourEventsProvider
import ru.tensor.sbis.verification_decl.onboarding_tour.OnboardingTourFeature
import ru.tensor.sbis.verification_decl.onboarding_tour.data.OnboardingTour

/**
 * Фасад [OnboardingTourFeature].
 *
 * @author as.chadov
 */
internal class OnboardingTourFacade : OnboardingTourFeature {

    private val diComponent get() = OnboardingTourPlugin.tourComponent

    override val onboardingTourCreator: OnboardingTourCreator
        get() = diComponent.creator

    override val devicePerformanceProvider: DevicePerformanceProvider
        get() = diComponent.performanceProvider

    override val onboardingTourEventsProvider: OnboardingTourEventsProvider
        get() = diComponent.eventsProvider

    override suspend fun getNext() = diComponent.uiProvider.getNext()

    override fun create(tourName: OnboardingTour.Name) = diComponent.uiProvider.create(tourName)
}
package ru.tensor.sbis.onboarding_tour.domain.builders

import ru.tensor.sbis.verification_decl.onboarding_tour.builders.OnboardingRulesConfiguration
import ru.tensor.sbis.onboarding_tour.data.TourPage
import ru.tensor.sbis.onboarding_tour.data.TourRules
import ru.tensor.sbis.verification_decl.onboarding_tour.data.BackgroundEffect
import ru.tensor.sbis.verification_decl.onboarding_tour.data.DisplayBehavior

/**
 * Билдер правил отображения [TourPage].
 *
 * @author as.chadov
 */
internal class OnboardingTourRulesBuilder :
    BaseOnboardingTourDslBuilder<TourRules>(),
    OnboardingRulesConfiguration {

    override var showOnlyOnce: Boolean = true
    override var displayBehavior: DisplayBehavior = DisplayBehavior.UNIQUE
    override var showOnlyOnceConsideringOnboarding: Boolean = true
    override var backgroundEffect = BackgroundEffect.GRADIENT
    override var swipeTransition = true
    override var swipeCloseable = false

    override fun build(): TourRules = TourRules(
        displayBehavior = displayBehavior,
        showOnlyOnceConsideringOnboarding = showOnlyOnceConsideringOnboarding,
        swipeTransition = swipeTransition,
        swipeCloseable = swipeCloseable,
        backgroundEffect = backgroundEffect
    )
}
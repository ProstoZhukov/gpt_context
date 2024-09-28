package ru.tensor.sbis.onboarding_tour.data

import ru.tensor.sbis.verification_decl.onboarding_tour.builders.OnboardingRulesConfiguration
import ru.tensor.sbis.verification_decl.onboarding_tour.data.BackgroundEffect
import ru.tensor.sbis.verification_decl.onboarding_tour.data.DisplayBehavior

/**
 * Правила отображения экрана онбординга и настроек.
 * Подробнее [OnboardingRulesConfiguration].
 *
 * @author as.chadov
 */
internal class TourRules(
    val displayBehavior: DisplayBehavior,
    val showOnlyOnceConsideringOnboarding: Boolean,
    val swipeTransition: Boolean,
    val swipeCloseable: Boolean,
    val backgroundEffect: BackgroundEffect
) {

    companion object {
        /**
         * В соответствии с состоянием [OnboardingRulesConfiguration] по умолчанию.
         */
        val default = TourRules(
            displayBehavior = DisplayBehavior.UNIQUE,
            showOnlyOnceConsideringOnboarding = true,
            swipeTransition = true,
            swipeCloseable = false,
            backgroundEffect = BackgroundEffect.GRADIENT
        )
    }
}
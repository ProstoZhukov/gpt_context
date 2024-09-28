package ru.tensor.sbis.verification_decl.onboarding_tour

import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.verification_decl.onboarding_tour.OnboardingTourProvider.Companion.DEFAULT_NAME
import ru.tensor.sbis.verification_decl.onboarding_tour.builders.OnboardingConfigurator
import ru.tensor.sbis.verification_decl.onboarding_tour.builders.TourPriority
import ru.tensor.sbis.verification_decl.onboarding_tour.data.OnboardingTour

/**
 * Интерфейс создателя Приветственного экрана настроект приложения.
 * Предоставлен для упрощённого создания посредством DSL.
 *
 * @author as.chadov
 */
interface OnboardingTourCreator : Feature {

    /**
     * Создать экран [OnboardingTour] через описание [LinkOpenHandlerBuilder].
     * Возвращает описание тура в поставщике [OnboardingTourProvider] по умолчанию.
     */
    fun createProvider(
        name: OnboardingTour.Name = DEFAULT_NAME,
        priority: TourPriority = TourPriority.NORMAL,
        init: OnboardingConfigurator.() -> Unit
    ): OnboardingTourProvider

    /** Создать экран [OnboardingTour] через описание [LinkOpenHandlerBuilder]. */
    fun create(init: OnboardingConfigurator.() -> Unit): OnboardingTour

    /**
     * Поставщик реализации [OnboardingTourCreator].
     */
    interface Provider : Feature {
        val onboardingTourCreator: OnboardingTourCreator
    }
}
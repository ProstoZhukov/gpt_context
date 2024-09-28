package ru.tensor.sbis.verification_decl.onboarding_tour

import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.verification_decl.onboarding_tour.builders.TourPriority
import ru.tensor.sbis.verification_decl.onboarding_tour.builders.TourPriority.NORMAL
import ru.tensor.sbis.verification_decl.onboarding_tour.data.OnboardingTour

/**
 * Поставщик содержимого Приветственного экрана настроект приложения.
 *
 * @author as.chadov
 */
interface OnboardingTourProvider : Feature {

    /** Опциональное имя тура приветственного экрана [OnboardingTour]. */
    val name: OnboardingTour.Name get() = DEFAULT_NAME

    /** Опциональный приоритет [OnboardingTour]. */
    val priority: TourPriority get() = NORMAL

    /** @SelfDocumented */
    fun getTour(): OnboardingTour

    companion object {
        /** @SelfDocumented */
        val DEFAULT_NAME = OnboardingTour.Name("default_tour")
    }
}
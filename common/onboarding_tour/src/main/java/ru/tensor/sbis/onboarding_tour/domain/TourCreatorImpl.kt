package ru.tensor.sbis.onboarding_tour.domain

import android.content.Context
import ru.tensor.sbis.verification_decl.onboarding_tour.OnboardingTourCreator
import ru.tensor.sbis.verification_decl.onboarding_tour.OnboardingTourProvider
import ru.tensor.sbis.verification_decl.onboarding_tour.builders.OnboardingConfigurator
import ru.tensor.sbis.verification_decl.onboarding_tour.builders.TourPriority
import ru.tensor.sbis.verification_decl.onboarding_tour.data.OnboardingTour
import ru.tensor.sbis.onboarding_tour.contract.OnboardingTourDependency
import ru.tensor.sbis.onboarding_tour.di.TourComponentScope
import ru.tensor.sbis.onboarding_tour.domain.builders.OnboardingTourBuilder
import javax.inject.Inject

/**
 * Реализация создателя [OnboardingTourCreator].
 *
 * @author as.chadov
 */
@TourComponentScope
internal class TourCreatorImpl @Inject constructor(
    private val context: Context,
    private val dependency: OnboardingTourDependency
) : OnboardingTourCreator {

    override fun createProvider(
        name: OnboardingTour.Name,
        priority: TourPriority,
        init: OnboardingConfigurator.() -> Unit
    ): OnboardingTourProvider = object : OnboardingTourProvider {
        override val name = name
        override val priority = priority
        override fun getTour() = create(init)
    }

    override fun create(init: OnboardingConfigurator.() -> Unit): OnboardingTour =
        OnboardingTourBuilder(context, dependency).run {
            init()
            build()
        }
}
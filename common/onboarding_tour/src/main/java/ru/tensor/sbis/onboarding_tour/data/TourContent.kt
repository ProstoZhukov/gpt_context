package ru.tensor.sbis.onboarding_tour.data

import android.content.Context
import ru.tensor.sbis.verification_decl.onboarding_tour.OnboardingTourCreator
import ru.tensor.sbis.verification_decl.onboarding_tour.builders.OnboardingConfigurator
import ru.tensor.sbis.verification_decl.onboarding_tour.data.OnboardingTour
import ru.tensor.sbis.onboarding_tour.domain.builders.OnboardingTourBuilder
import ru.tensor.sbis.verification_decl.onboarding_tour.builders.DismissCommand

/**
 * Содержимое тура по экранам онбординга и настроек.
 *
 * @author as.chadov
 */
class TourContent internal constructor(
    internal val pages: List<TourPage>,
    internal val startPosition: Int = pages.firstOrNull()?.position ?: TourPage.FIRST_POSITION,
    internal val rules: TourRules,
    internal var command: DismissCommand?
) : OnboardingTour {

    /**
     * Возможность создать тур без использования [OnboardingTourCreator].
     */
    companion object {
        /**
         * Декларативное объявления содержимого компонента приветственного экрана.
         */
        fun create(init: OnboardingConfigurator.() -> Unit): OnboardingTour =
            OnboardingTourBuilder().run {
                init()
                build()
            }

        /**
         * Декларативное объявления содержимого компонента приветственного экрана.
         */
        @JvmSynthetic
        operator fun invoke(
            @Suppress("UNUSED_PARAMETER") context: Context,
            init: OnboardingConfigurator.() -> Unit
        ) = create(init)
    }
}
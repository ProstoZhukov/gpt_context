package ru.tensor.sbis.onboarding_tour.domain.builders

import androidx.annotation.StringRes
import androidx.core.content.res.ResourcesCompat.ID_NULL
import ru.tensor.sbis.onboarding_tour.R
import ru.tensor.sbis.verification_decl.onboarding_tour.builders.OnboardingTermsConfiguration
import ru.tensor.sbis.onboarding_tour.data.TourTerms

/**
 * Билдер условий и положений [TourTerms].
 *
 * @author as.chadov
 */
internal class OnboardingTourTermsBuilder :
    BaseOnboardingTourDslBuilder<TourTerms>(),
    OnboardingTermsConfiguration {

    override var caption: Int = ID_NULL
    override var links: List<String> = emptyList()

    override fun build(): TourTerms =
        TourTerms(
            termsCaption = caption.takeIf { it != ID_NULL } ?: DEFAULT_CAPTION_RES_ID,
            termsLinks = links
        )

    companion object {
        /** @SelfDocumented */
        @StringRes
        private val DEFAULT_CAPTION_RES_ID = R.string.onboarding_tour_terms_caption
    }
}
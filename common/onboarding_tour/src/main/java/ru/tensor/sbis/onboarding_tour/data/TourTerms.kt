package ru.tensor.sbis.onboarding_tour.data

import androidx.annotation.StringRes
import androidx.core.content.res.ResourcesCompat.ID_NULL
import ru.tensor.sbis.verification_decl.onboarding_tour.builders.OnboardingTermsConfiguration

/**
 * Условия и положения.
 * Подробнее [OnboardingTermsConfiguration].
 *
 * @author as.chadov
 */
internal class TourTerms(
    @StringRes
    val termsCaption: Int = ID_NULL,
    val termsLinks: List<String> = emptyList()
)
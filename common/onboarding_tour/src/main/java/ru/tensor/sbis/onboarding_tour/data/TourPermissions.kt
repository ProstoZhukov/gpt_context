package ru.tensor.sbis.onboarding_tour.data

import ru.tensor.sbis.verification_decl.onboarding_tour.builders.OnboardingPermissionConfiguration
import ru.tensor.sbis.verification_decl.onboarding_tour.builders.RationaleCallback

/**
 * Требуемые разрешения и права.
 * Подробнее [OnboardingPermissionConfiguration].
 *
 * @author as.chadov
 */
internal class TourPermissions(
    val permissions: List<String>,
    val isMandatory: Boolean,
    var rationaleCommand: RationaleCallback?
)
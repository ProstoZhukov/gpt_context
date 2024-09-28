package ru.tensor.sbis.onboarding_tour.domain.builders

import ru.tensor.sbis.onboarding_tour.data.TourPermissions
import ru.tensor.sbis.verification_decl.onboarding_tour.builders.OnboardingPermissionConfiguration
import ru.tensor.sbis.verification_decl.onboarding_tour.builders.RationaleCallback

/**
 * Билдер запроса прав и разрешений.
 *
 * @author as.chadov
 */
internal class OnboardingTourPermissionsBuilder :
    BaseOnboardingTourDslBuilder<TourPermissions>(),
    OnboardingPermissionConfiguration {

    override var permissions: List<String> = emptyList()
    override var isRequired: Boolean = false

    private var rationaleCommand: RationaleCallback? = null

    override fun onRequestRationale(callback: RationaleCallback) {
        rationaleCommand = callback
    }

    override fun build(): TourPermissions {
        return TourPermissions(
            permissions = permissions,
            isMandatory = isRequired,
            rationaleCommand = rationaleCommand
        )
    }
}
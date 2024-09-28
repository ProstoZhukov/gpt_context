package ru.tensor.sbis.onboarding_tour.contract

import ru.tensor.sbis.verification_decl.onboarding_tour.OnboardingTourFeature
import ru.tensor.sbis.verification_decl.onboarding_tour.OnboardingTourProvider
import ru.tensor.sbis.plugin_struct.feature.FeatureProvider
import ru.tensor.sbis.verification_decl.login.LoginInterface
import ru.tensor.sbis.verification_decl.onboarding.OnboardingFeature
import ru.tensor.sbis.verification_decl.permission.PermissionFeature

/**
 * Перечень зависимостей необходимых для работы [OnboardingTourFeature].
 *
 * @author as.chadov
 */
internal interface OnboardingTourDependency {
    val loginInterface: LoginInterface?
    val permissionFeature: PermissionFeature?
    val onboardingFeature: OnboardingFeature?
    val tourFeatureProviderSet: Set<FeatureProvider<OnboardingTourProvider>>
}
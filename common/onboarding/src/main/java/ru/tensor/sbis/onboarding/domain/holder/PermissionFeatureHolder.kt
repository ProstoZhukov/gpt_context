package ru.tensor.sbis.onboarding.domain.holder

import ru.tensor.sbis.verification_decl.permission.PermissionFeature
import ru.tensor.sbis.onboarding.contract.providers.content.NoPermissionPage
import ru.tensor.sbis.onboarding.di.OnboardingSingletonComponent
import timber.log.Timber
import javax.inject.Inject

/**
 * Холдер функционального интерфейса модуля "полномочий"
 *
 * @author as.chadov
 */
internal class PermissionFeatureHolder @Inject constructor(
    private val feature: PermissionFeature?
) {

    operator fun invoke(consumer: PermissionFeature.() -> Unit) {
        if (feature != null) {
            feature.consumer()
        } else {
            Timber.e(
                NullPointerException(
                    "${PermissionFeature::class.java.canonicalName} is null. Provide it to " +
                            "${OnboardingSingletonComponent::class.java.canonicalName} " +
                            " for supporting ${NoPermissionPage::class.java.canonicalName}"
                )
            )
        }
    }
}
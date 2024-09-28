package ru.tensor.sbis.onboarding.domain.holder

import ru.tensor.sbis.verification_decl.login.LoginInterface
import ru.tensor.sbis.onboarding.di.OnboardingSingletonComponent
import timber.log.Timber
import javax.inject.Inject

/**
 * Холдер функционального интерфейса модуля "авторизации"
 *
 * @author as.chadov
 */
class LoginFeatureHolder @Inject constructor(private val feature: LoginInterface?) {

    internal fun isNotEmpty(): Boolean {
        return feature != null
    }

    internal operator fun invoke(consumer: LoginInterface.() -> Unit) {
        if (feature != null) {
            feature.consumer()
        } else {
            Timber.e(NullPointerException("${LoginInterface::class.java.canonicalName} is null. Provide it to " +
                                                  "${OnboardingSingletonComponent::class.java.canonicalName} "))
        }
    }
}
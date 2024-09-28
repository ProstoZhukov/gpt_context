package ru.tensor.sbis.onboarding.di

import android.content.Context
import ru.tensor.sbis.verification_decl.onboarding.OnboardingFeature
import ru.tensor.sbis.onboarding.OnboardingPlugin

/**
 * Провайдер di компонента приветственного экрана
 *
 * @author as.chadov
 */
object OnboardingSingletonComponentProvider {

    @JvmStatic
    internal fun get(context: Context): OnboardingSingletonComponent {
        /* ComponentProvider оставляем, в будущем может быть полезным для подмены реализации. */
        return OnboardingPlugin.onboardingSingletonComponent
    }

    @JvmStatic
    fun getOnboardingFeature(context: Context): OnboardingFeature {
        return get(context).getOnboardingFeature()
    }
}
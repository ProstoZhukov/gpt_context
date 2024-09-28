package ru.tensor.sbis.onboarding.contract

import ru.tensor.sbis.android_ext_decl.MainActivityProvider
import ru.tensor.sbis.onboarding.contract.providers.OnboardingProvider
import ru.tensor.sbis.plugin_struct.feature.FeatureProvider

/**
 * Интерфейс зависимостей необходимых для связывания компонента Приветственный экран с использующим его приложением
 *
 * @author as.chadov
 */
interface OnboardingDependency :
    MainActivityProvider {

    fun getOnboardingProviders(): Set<FeatureProvider<OnboardingProvider>>
}
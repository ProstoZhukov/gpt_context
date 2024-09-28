package ru.tensor.sbis.verification_decl.onboarding_tour

import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Фасад описывающий контракт модуля Приветственного экрана настроект приложения.
 *
 * @author as.chadov
 *
 * @see [OnboardingTourFragmentProvider]
 * @see [OnboardingTourCreator]
 * @see [DevicePerformanceProvider]
 * @see [OnboardingTourEventsProvider]
 */
interface OnboardingTourFeature :
    Feature,
    OnboardingTourFragmentProvider,
    OnboardingTourCreator.Provider {

    /**@SelfDocumented */
    val devicePerformanceProvider: DevicePerformanceProvider

    /** [OnboardingTourEventsProvider]. */
    val onboardingTourEventsProvider: OnboardingTourEventsProvider
}
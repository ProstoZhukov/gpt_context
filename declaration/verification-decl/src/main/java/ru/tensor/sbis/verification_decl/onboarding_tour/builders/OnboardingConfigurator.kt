package ru.tensor.sbis.verification_decl.onboarding_tour.builders

import ru.tensor.sbis.verification_decl.onboarding_tour.data.OnboardingTour

/**
 * Интерфейс билдера для создания [OnboardingTour].
 *
 * @author as.chadov
 */
interface OnboardingConfigurator {

    /** Установить опциональные правила отображения [OnboardingTour]. */
    fun rules(init: OnboardingRulesConfiguration.() -> Unit)

    /** Установить единый баннер приветственных экранов и настроект приложения. */
    fun defaultBanner(init: OnboardingBannerConfiguration.() -> Unit)

    /** Добавить новый экран. */
    fun page(init: OnboardingPageConfiguration.() -> Unit)

    /** Опциональный коллбэк вызываемый по завершении отображения тура онбординга. */
    fun onDismiss(command: DismissCommand)
}
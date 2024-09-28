package ru.tensor.sbis.verification_decl.onboarding_tour.builders

import ru.tensor.sbis.verification_decl.onboarding_tour.data.BackgroundEffect
import ru.tensor.sbis.verification_decl.onboarding_tour.data.DisplayBehavior
import ru.tensor.sbis.verification_decl.onboarding_tour.data.OnboardingTour

/**
 * Интерфейс билдера правил отображения [OnboardingTour].
 *
 * @author as.chadov
 */
interface OnboardingRulesConfiguration {

    /**
     * Следует ли компоненту отображаться единожды для конкретного пользователя. По умолчанию true.
     * В случае false при каждом вызове онбординга он будет отображен.
     */
    @Deprecated("Необходимо перейти на displayBehavior")
    var showOnlyOnce: Boolean

    /**
     * Поведение отображения тура.
     */
    var displayBehavior: DisplayBehavior

    /**
     * Следует ли учитывать ранее показанный компонент онбординга ':onboarding'. По умолчанию true.
     * В случае true тур не будет отображаться если ранее для данного пользователя отображался onboarding-компонент.
     */
    var showOnlyOnceConsideringOnboarding: Boolean

    /**
     * Поддержка переходов по свайпу пользователя. По умолчанию true.
     */
    var swipeTransition: Boolean

    /**
     * Поддержка закрытия смахиванием последней страницы. По умолчанию false.
     */
    var swipeCloseable: Boolean

    /**
     * Эффект для бэкграунда.
     * По умолчанию [BackgroundEffect.GRADIENT].
     */
    var backgroundEffect: BackgroundEffect
}
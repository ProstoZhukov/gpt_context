package ru.tensor.sbis.onboarding_tour.domain.builders

/**
 * Билдер приветственного экрана настроект приложения.
 *
 * @author as.chadov
 */
@OnboardingTourDslMarker
internal abstract class BaseOnboardingTourDslBuilder<T> {
    /** @SelfDocumented */
    abstract fun build(): T
}

/**
 * Аннотация для отметки о принадлежности к DSL построения экранов приветсвенного экрана.
 *
 * @author as.chadov
 */
@DslMarker
internal annotation class OnboardingTourDslMarker
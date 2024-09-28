package ru.tensor.sbis.onboarding.contract.impl

import io.reactivex.Observable
import ru.tensor.sbis.verification_decl.onboarding.OnboardingNavigator
import ru.tensor.sbis.onboarding.domain.event.NavigateEvent

/**
 * Менеджер навигаци по фрагментам "Приветственного Экрана" из модуля onboarding
 *
 * @author as.chadov
 */
internal abstract class OnboardingNavigatorManager :
    OnboardingNavigator {

    /**
     * Наблюдать переходы по экранам фрагмент-фич извне
     */
    abstract fun observeHostTransitions(): Observable<NavigateEvent>

    /**
     * Наблюдать закрытие компонента "Приветственного Экрана"
     */
    abstract fun observeOnboardingDismiss(): Observable<Unit>

    /**
     * Сообщить о закрытии компонента "Приветственного Экрана"
     */
    abstract fun onDismissOnboarding()

    /**
     * Подписка на событие закрытия "Приветственного экрана".
     * Работает вне зависимости от условий запуска, в отличии от [observeOnboardingDismiss].
     */
    abstract fun observeOnboardingCloseEvent(): Observable<Unit>

    /**
     * Сообщить о закрытии "Приветственного экрана".
     * Для обработки нужно подписаться, используя [observeOnboardingCloseEvent].
     */
    abstract fun onOnboardingCloseEvent()
}
package ru.tensor.sbis.onboarding.contract.impl

import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import ru.tensor.sbis.onboarding.domain.event.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Реализация навигатора по фрагментам "Приветственного Экрана" из модуля onboarding
 *
 * @author as.chadov
 */
@Singleton
internal class NavigatorManagerImpl @Inject constructor() :
    OnboardingNavigatorManager() {

    override fun moveNextPage() {
        navigationChannel.onNext(NavigateForwardEvent())
    }

    override fun movePreviousPage() {
        navigationChannel.onNext(NavigateBackwardEvent())
    }

    override fun showFragmentOnTop(
        creator: () -> Fragment,
        screenKey: String,
        @IdRes containerId: Int
    ) {
        val event = OpenCustomScreen(
            creator = creator,
            screenKey = screenKey,
            containerId = containerId
        )
        navigationChannel.onNext(event)
    }

    override fun dismissFragment(screenKey: String) {
        navigationChannel.onNext(DismissCustomScreen(screenKey))
    }

    override fun onDismissOnboarding() = dismissChannel.onNext(Unit)

    override fun onOnboardingCloseEvent() = closeEventChannel.onNext(Unit)

    override fun observeHostTransitions(): Observable<NavigateEvent> = navigationChannel

    override fun observeOnboardingDismiss(): Observable<Unit> =
        dismissChannel.throttleFirst(DISMISS_EVENT_WINDOW_DELAY_MILL_SEC, TimeUnit.MILLISECONDS)

    override fun observeOnboardingCloseEvent(): Observable<Unit> =
        closeEventChannel.throttleFirst(DISMISS_EVENT_WINDOW_DELAY_MILL_SEC, TimeUnit.MILLISECONDS)

    private val navigationChannel = PublishSubject.create<NavigateEvent>()
    private val dismissChannel = PublishSubject.create<Unit>()
    private val closeEventChannel = BehaviorSubject.create<Unit>()
}

private const val DISMISS_EVENT_WINDOW_DELAY_MILL_SEC = 500L
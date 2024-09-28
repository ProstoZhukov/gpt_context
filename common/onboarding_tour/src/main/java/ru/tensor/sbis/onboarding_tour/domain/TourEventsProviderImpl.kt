package ru.tensor.sbis.onboarding_tour.domain

import io.reactivex.Maybe
import io.reactivex.subjects.BehaviorSubject
import ru.tensor.sbis.onboarding_tour.di.TourComponentScope
import ru.tensor.sbis.verification_decl.onboarding_tour.OnboardingTourEventsProvider
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Поставщик соыбтий жизненного цикла Приветственного экрана.
 */
@TourComponentScope
internal class TourEventsProviderImpl @Inject constructor() :
    OnboardingTourEventsProvider,
    TourEventsEmitter {

    private val closeEventChannel = BehaviorSubject.create<Unit>()

    /**
     * Сообщить о закрытии "Приветственного экрана".
     */
    override fun onTourClose() {
        closeEventChannel.onNext(Unit)
    }

    override fun observeTourCloseEvent(): Maybe<Unit> =
        closeEventChannel
            .throttleFirst(DISMISS_EVENT_WINDOW_DELAY_MILL_SEC, TimeUnit.MILLISECONDS)
            .firstElement()

    private companion object {
        const val DISMISS_EVENT_WINDOW_DELAY_MILL_SEC = 300L
    }
}

/**
 * Эмиттер событий [OnboardingTourEventsProvider].
 */
interface TourEventsEmitter {
    /**
     * Сообщить о закрытии "Приветственного экрана".
     */
    fun onTourClose()
}
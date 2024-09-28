package ru.tensor.sbis.onboarding_tour.ui.store

import com.arkivanov.essenty.statekeeper.StateKeeper
import com.arkivanov.essenty.statekeeper.consume
import com.arkivanov.mvikotlin.core.store.SimpleBootstrapper
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import kotlinx.coroutines.CoroutineDispatcher
import ru.tensor.sbis.mvi_extension.create
import ru.tensor.sbis.onboarding_tour.ui.di.IoDispatcher
import ru.tensor.sbis.onboarding_tour.domain.TourInteractor
import ru.tensor.sbis.onboarding_tour.ui.di.TourFragmentScope
import ru.tensor.sbis.verification_decl.onboarding_tour.data.OnboardingTour
import javax.inject.Inject

/**
 * Фабрика для создания [OnboardingTourStore] и инициализации бустрапера, экзекьютора и редьюсера.
 */
@TourFragmentScope
internal class OnboardingTourStoreFactory @Inject constructor(
    private val tourName: OnboardingTour.Name,
    private val storeFactory: StoreFactory,
    private val tourInteractor: TourInteractor,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {

    /** @SelfDocumented*/
    fun create(stateKeeper: StateKeeper): OnboardingTourStore = object :
        OnboardingTourStore,
        Store<OnboardingTourStore.Intent, OnboardingTourStore.State, OnboardingTourStore.Label>
        by storeFactory.create(
            stateKeeper = stateKeeper,
            name = OnboardingTourStore.NAME,
            autoInit = true,
            initialState = OnboardingTourStore.State(),
            bootstrapper = getBootstrapper(stateKeeper),
            executorFactory = { TourExecutor(tourName, tourInteractor, ioDispatcher) },
            reducer = TourReducer(),
            saveStateSupplier = TourStateSupplier()
        ) {}

    private fun getBootstrapper(stateKeeper: StateKeeper): SimpleBootstrapper<Action> {
        // Ключ по умолчанию из StoreExt.kt
        val key = OnboardingTourStore.State::class.toString()
        val state = stateKeeper.consume<OnboardingTourStore.State>(key)
        return if (state == null) {
            SimpleBootstrapper<Action>(Action.LoadTour(tourName))
        } else {
            SimpleBootstrapper<Action>(Action.RestoreTour(tourName, state))
        }
    }
}

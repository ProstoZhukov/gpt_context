package ru.tensor.sbis.onboarding_tour.ui.store

/**@SelfDocumented */
internal class TourStateSupplier : (OnboardingTourStore.State) -> OnboardingTourStore.State {

    override fun invoke(state: OnboardingTourStore.State): OnboardingTourStore.State {
        return state
    }
}
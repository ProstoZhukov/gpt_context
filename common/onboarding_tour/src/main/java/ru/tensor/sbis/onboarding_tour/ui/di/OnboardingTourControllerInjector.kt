package ru.tensor.sbis.onboarding_tour.ui.di

import android.view.View
import androidx.fragment.app.Fragment
import dagger.assisted.AssistedFactory
import ru.tensor.sbis.onboarding_tour.ui.TourController
import ru.tensor.sbis.onboarding_tour.ui.TourView

/**
 * Фабрика для создания [TourController].
 */
@TourFragmentScope
@AssistedFactory
internal interface OnboardingTourControllerInjector {
    fun inject(
        fragment: Fragment,
        viewFactory: (View) -> TourView
    ): TourController
}
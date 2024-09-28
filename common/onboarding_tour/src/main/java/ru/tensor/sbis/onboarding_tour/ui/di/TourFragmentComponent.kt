package ru.tensor.sbis.onboarding_tour.ui.di

import dagger.BindsInstance
import dagger.Subcomponent
import dagger.android.AndroidInjector
import ru.tensor.sbis.onboarding_tour.ui.TourFragment

/**
 * Компонент фрагмента Приветственного экрана и настроект приложения.
 */
@TourFragmentScope
@Subcomponent(
    modules = [TourModule::class]
)
internal interface TourFragmentComponent : AndroidInjector<TourFragment> {

    @Subcomponent.Factory
    interface Factory {
        fun create(@BindsInstance fragment: TourFragment): TourFragmentComponent
    }
}
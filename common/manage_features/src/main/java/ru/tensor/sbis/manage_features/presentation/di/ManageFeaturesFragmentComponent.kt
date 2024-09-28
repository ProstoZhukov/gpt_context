package ru.tensor.sbis.manage_features.presentation.di

import dagger.BindsInstance
import dagger.Component
import ru.tensor.sbis.manage_features.data.di.ManageFeaturesComponent
import ru.tensor.sbis.manage_features.presentation.ManageFeaturesFragment

/**
 * Компонент основного фрагмента модуля
 */
@Component(dependencies = [ManageFeaturesComponent::class], modules = [ManageFeaturesFragmentModule::class])
internal interface ManageFeaturesFragmentComponent {

    /** @SelfDocumented */
    fun inject(fragment: ManageFeaturesFragment)

    @Component.Factory
    interface Factory {

        /** @SelfDocumented */
        fun create(@BindsInstance fragment: ManageFeaturesFragment, dependency: ManageFeaturesComponent): ManageFeaturesFragmentComponent
    }
}
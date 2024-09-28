package ru.tensor.sbis.localfeaturetoggle.presentation.di

import android.content.Context
import android.view.View
import androidx.fragment.app.Fragment
import dagger.BindsInstance
import dagger.Component
import dagger.assisted.AssistedFactory
import ru.tensor.sbis.localfeaturetoggle.presentation.ui.LocalFeatureToggleController
import ru.tensor.sbis.localfeaturetoggle.presentation.ui.LocalFeatureToggleView

/**
 * @author mb.kruglova
 */
@LocalFeatureToggleScope
@Component(
    modules = [LocalFeatureToggleModule::class]
)
internal interface LocalFeatureToggleComponent {
    fun injector(): Injector

    @Component.Factory
    interface Factory {

        fun create(
            @BindsInstance context: Context,
            @BindsInstance viewFactory: (View) -> LocalFeatureToggleView
        ): LocalFeatureToggleComponent
    }

    @AssistedFactory
    interface Injector {
        fun inject(fragment: Fragment): LocalFeatureToggleController
    }
}
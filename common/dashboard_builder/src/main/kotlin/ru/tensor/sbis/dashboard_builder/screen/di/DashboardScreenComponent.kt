package ru.tensor.sbis.dashboard_builder.screen.di

import androidx.fragment.app.Fragment
import dagger.BindsInstance
import dagger.Component
import dagger.assisted.AssistedFactory
import ru.tensor.sbis.dashboard_builder.screen.ui.DashboardScreenController
import ru.tensor.sbis.dashboard_builder.screen.ui.DashboardScreenView
import ru.tensor.sbis.plugin_struct.utils.SbisThemedContext
import ru.tensor.sbis.toolbox_decl.dashboard.DashboardRequest

/**
 * @author am.boldinov
 */
@DashboardScreenScope
@Component(modules = [DashboardScreenModule::class])
internal interface DashboardScreenComponent {

    fun injector(): Injector

    @Component.Factory
    interface Factory {

        fun create(
            @BindsInstance request: DashboardRequest,
            @BindsInstance themedContext: SbisThemedContext,
            @BindsInstance viewFactory: DashboardScreenView.Factory
        ): DashboardScreenComponent
    }

    @AssistedFactory
    interface Injector {
        fun inject(fragment: Fragment): DashboardScreenController
    }
}
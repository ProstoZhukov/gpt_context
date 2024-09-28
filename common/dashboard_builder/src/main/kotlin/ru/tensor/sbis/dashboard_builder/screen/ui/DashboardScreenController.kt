package ru.tensor.sbis.dashboard_builder.screen.ui

import androidx.fragment.app.Fragment
import com.arkivanov.mvikotlin.core.binder.BinderLifecycleMode
import com.arkivanov.mvikotlin.extensions.coroutines.bind
import com.arkivanov.mvikotlin.extensions.coroutines.events
import com.arkivanov.mvikotlin.extensions.coroutines.states
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.map
import ru.tensor.sbis.base_components.fragment.FragmentBackPress
import ru.tensor.sbis.dashboard_builder.config.DashboardConfiguration
import ru.tensor.sbis.dashboard_builder.screen.store.DashboardScreenStoreFactory
import ru.tensor.sbis.dashboard_builder.screen.store.DashboardScreenStore.Intent
import ru.tensor.sbis.dashboard_builder.screen.store.DashboardScreenStore.State
import ru.tensor.sbis.dashboard_builder.screen.ui.DashboardScreenView.Event
import ru.tensor.sbis.dashboard_builder.screen.ui.DashboardScreenView.Model
import ru.tensor.sbis.mvi_extension.attachBinder
import ru.tensor.sbis.mvi_extension.provideStore
import ru.tensor.sbis.toolbox_decl.dashboard.DashboardRequest

/**
 * @author am.boldinov
 */
internal class DashboardScreenController @AssistedInject constructor(
    @Assisted private val host: Fragment,
    viewFactory: DashboardScreenView.Factory,
    request: DashboardRequest,
    configuration: DashboardConfiguration,
    storeFactory: DashboardScreenStoreFactory
) {

    private val store = host.provideStore {
        storeFactory.create(request, configuration)
    }

    init {
        host.attachBinder(BinderLifecycleMode.CREATE_DESTROY, viewFactory) { view ->
            bind {
                view.events.map { it.toIntent() } bindTo store
                store.states.map { it.toModel() } bindTo view
            }
        }
    }

    fun dispatchOnBackPressed(): Boolean {
        // TODO router
        host.childFragmentManager.fragments.forEach {
            if (it is FragmentBackPress && it.onBackPressed()) {
                return true
            }
        }
        if (!host.isStateSaved && host.childFragmentManager.backStackEntryCount >= 1) {
            host.childFragmentManager.popBackStack()
            return true
        }
        return false
    }

    private fun Event.toIntent(): Intent {
        error("No events")
    }

    private fun State.toModel(): Model {
        return Model(
            topNavigationContent = topNavigationContent,
            source = source
        )
    }
}
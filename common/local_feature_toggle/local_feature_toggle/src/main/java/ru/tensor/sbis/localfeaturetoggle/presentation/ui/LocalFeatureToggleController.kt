package ru.tensor.sbis.localfeaturetoggle.presentation.ui

import android.view.View
import androidx.fragment.app.Fragment
import com.arkivanov.mvikotlin.core.binder.BinderLifecycleMode
import com.arkivanov.mvikotlin.extensions.coroutines.bind
import com.arkivanov.mvikotlin.extensions.coroutines.events
import com.arkivanov.mvikotlin.extensions.coroutines.states
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.map
import ru.tensor.sbis.localfeaturetoggle.presentation.store.LocalFeatureToggleStore
import ru.tensor.sbis.localfeaturetoggle.presentation.store.LocalFeatureToggleStoreFactory
import ru.tensor.sbis.mvi_extension.attachBinder
import ru.tensor.sbis.mvi_extension.provideStore

/**
 * Контроллер, обеспечивающий связку компонентов Android с компонентами MVI.
 *
 * @author mb.kruglova
 */
internal class LocalFeatureToggleController @AssistedInject constructor(
    @Assisted private val fragment: Fragment,
    viewFactory: (View) -> LocalFeatureToggleView,
    private val localFeatureToggleStoreFactory: LocalFeatureToggleStoreFactory
) {
    private val localFeatureToggleStore = fragment.provideStore {
        localFeatureToggleStoreFactory.create(it)
    }

    init {
        fragment.attachBinder(
            BinderLifecycleMode.CREATE_DESTROY,
            viewFactory
        ) { view ->
            bind {
                view.events.map { it.toIntent() } bindTo localFeatureToggleStore
                localFeatureToggleStore.states.map(stateToModel) bindTo view
            }
        }
    }

    private fun LocalFeatureToggleView.Event.toIntent(): LocalFeatureToggleStore.Intent {
        return when (this) {
            is LocalFeatureToggleView.Event.ClickSwitchItem ->
                LocalFeatureToggleStore.Intent.SwitchItem(feature, isActivated)
        }
    }

    private val stateToModel: LocalFeatureToggleStore.State.() -> LocalFeatureToggleView.Model = {
        LocalFeatureToggleView.Model(listItems)
    }
}
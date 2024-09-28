package ru.tensor.sbis.our_organisations.presentation.list.ui

import android.view.View
import androidx.fragment.app.Fragment
import com.arkivanov.mvikotlin.core.binder.BinderLifecycleMode
import com.arkivanov.mvikotlin.extensions.coroutines.bind
import com.arkivanov.mvikotlin.extensions.coroutines.events
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.states
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.map
import ru.tensor.sbis.mvi_extension.attachBinder
import ru.tensor.sbis.mvi_extension.provideStore
import ru.tensor.sbis.mvi_extension.router.navigator.WeakLifecycleNavigator
import ru.tensor.sbis.our_organisations.presentation.list.store.OurOrgListStore.Intent
import ru.tensor.sbis.our_organisations.presentation.list.store.OurOrgListStore.Label
import ru.tensor.sbis.our_organisations.presentation.list.store.OurOrgListStore.State
import ru.tensor.sbis.our_organisations.presentation.list.store.OurOrgListStoreFactory
import ru.tensor.sbis.our_organisations.presentation.list.ui.OurOrgListView.Event
import ru.tensor.sbis.our_organisations.presentation.list.ui.OurOrgListView.Model

/**
 * Посредник между Бизнес-логикой [ourOrgListStore] и UI [OurOrgListView] экрана нашей организации.
 *
 * @author mv.ilin
 */
internal class OurOrgListController @AssistedInject constructor(
    @Assisted private val fragment: Fragment,
    viewFactory: (View) -> OurOrgListView,
    private val ourOrgListStoreFactory: OurOrgListStoreFactory,
    private val router: OurOrgListRouter
) {

    private val ourOrgListStore = fragment.provideStore {
        ourOrgListStoreFactory.create()
    }

    private var onCancelAction: () -> Unit = { }
    private var onStopAction: () -> Unit = { }

    init {
        router.attachNavigator(WeakLifecycleNavigator(fragment))
        fragment.attachBinder(
            BinderLifecycleMode.CREATE_DESTROY,
            viewFactory
        ) { connectedKegView ->
            bind {
                onCancelAction = { connectedKegView.cancel() }
                onStopAction = { connectedKegView.onStop() }

                ourOrgListStore.states.map { state -> state.toModel() } bindTo connectedKegView
                ourOrgListStore.labels bindTo { label ->
                    when (label) {
                        Label.OnShowContent -> router.onShowContent()
                        is Label.ClickOrganisation -> router.clickOrganisation(label.organisations)
                        is Label.ClickApply -> router.clickApply(label.organisations)
                        is Label.LoadingError -> router.showErrorMessage(label.message)
                        is Label.OpenFilter -> router.openFilter(label.anchor, label.sbisMenuItems)
                    }
                }
                connectedKegView.events.map { event -> event.toIntent() } bindTo ourOrgListStore
            }
        }
    }

    private fun Event.toIntent(): Intent {
        return when (this) {
            Event.Refresh -> Intent.Refresh
            Event.OnShowContent -> Intent.OnShowContent
            Event.RequestPage -> Intent.RequestPage
            is Event.OpenFilter -> Intent.OpenFilter(anchor)
            is Event.SearchTextChanged -> Intent.SearchTextChanged(searchText)
            is Event.OrganisationItemClicked -> Intent.OrganisationItemClicked(organisationVm, needClose)
        }
    }

    private fun State.toModel(): Model {
        return Model(
            data,
            emptyProgress,
            showPageProgress,
            swipeRefreshing,
            stubContent,
            setSelectedFilter,
            needShowSearch
        )
    }

    fun onBackPressed(): Boolean {
        if (!ourOrgListStore.state.discardResultAfterClick) {
            ourOrgListStore.accept(Intent.OnApply)
            return true
        } else {
            return false
        }
    }

    fun cancel() {
        ourOrgListStore.accept(Intent.OnCleared)
        onCancelAction()
    }

    fun onStart() {
        ourOrgListStore.accept(Intent.SubscribeDataEvent)
    }

    fun onStop() {
        ourOrgListStore.accept(Intent.UnSubscribeDataEvent)
        onStopAction()
    }

    fun onApply() {
        ourOrgListStore.accept(Intent.OnApply)
    }

    fun onReset() {
        ourOrgListStore.accept(Intent.OnReset)
    }
}

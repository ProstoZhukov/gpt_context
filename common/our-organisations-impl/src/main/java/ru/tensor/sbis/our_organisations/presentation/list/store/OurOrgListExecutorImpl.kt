package ru.tensor.sbis.our_organisations.presentation.list.store

import android.view.View
import androidx.core.content.res.ResourcesCompat
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.launch
import ru.tensor.sbis.design.context_menu.MenuItem
import ru.tensor.sbis.design.context_menu.MenuItemState
import ru.tensor.sbis.design.stubview.ImageStubContent
import ru.tensor.sbis.design.stubview.StubViewCase
import ru.tensor.sbis.design.stubview.StubViewImageType
import ru.tensor.sbis.design.theme.res.PlatformSbisString
import ru.tensor.sbis.design.theme.res.SbisString
import ru.tensor.sbis.our_organisations.R
import ru.tensor.sbis.our_organisations.data.OurOrgFilter
import ru.tensor.sbis.our_organisations.feature.data.Organisation
import ru.tensor.sbis.our_organisations.feature.data.OurOrgParams
import ru.tensor.sbis.our_organisations.presentation.list.OurOrgListContract
import ru.tensor.sbis.our_organisations.presentation.list.interactor.OurOrgListInteractor
import ru.tensor.sbis.our_organisations.presentation.list.store.OurOrgListStore.Intent
import ru.tensor.sbis.our_organisations.presentation.list.store.OurOrgListStore.Label
import ru.tensor.sbis.our_organisations.presentation.list.store.OurOrgListStore.State
import ru.tensor.sbis.our_organisations.presentation.list.store.OurOrgListStoreFactory.Action
import ru.tensor.sbis.our_organisations.presentation.list.store.OurOrgListStoreFactory.Message
import ru.tensor.sbis.our_organisations.presentation.list.ui.OurOrgListStateController
import ru.tensor.sbis.our_organisations.presentation.list.ui.adapter.OrganisationVM
import timber.log.Timber

/**
 * Mvi-сущность executor экрана нашей организации.
 *
 * @author mv.ilin
 */
internal class OurOrgListExecutorImpl(
    private val ourOrgListInteractor: OurOrgListInteractor,
    private val initParams: OurOrgParams
) : CoroutineExecutor<Intent, Action, State, Message, Label>(), OurOrgListStateController.ViewController<Organisation> {

    private var isFirstSyncComplete: Boolean = false
    private var dataRefreshDisposable: Disposable? = null
    private var blockingWhileReadOrganisation: Boolean = false

    private var organisations: List<Organisation> = emptyList()

    private var ourOrgFilter = OurOrgFilter(
        withEliminated = initParams.withEliminated,
        headsOnly = initParams.headsOnly,
        scopesAreas = initParams.scopesAreas,
        showInnerCompany = initParams.showInnerCompany,
        ids = initParams.displayIds
    )

    private val stateController = OurOrgListStateController(
        requestFactory = { filter ->
            if (filter.requestNewData) {
                ourOrgListInteractor.listRx(filter)
            } else {
                ourOrgListInteractor.refreshRx(filter)
            }
        },
        viewController = this,
        scope = scope,
        itemsPage = OurOrgListContract.ITEMS_PAGE
    )

    override fun executeAction(action: Action, getState: () -> State) {
        when (action) {
            Action.Init -> {
                getOrganisationsById(initParams.selectedOrganisations) {
                    dispatch(
                        Message.UpdateSelectedOrganisations(
                            getState().selectedOrganisations.toMutableList().apply {
                                addAll(it)
                            }
                        )
                    )
                    executeIntent(Intent.SubscribeDataEvent)
                }
            }

            Action.LoadData -> {
                ourOrgFilter.requestNewData = true
                stateController.refreshAllPage(ourOrgFilter)
            }

            Action.RefreshAllData -> {
                ourOrgFilter.requestNewData = false
                stateController.refreshAllPage(ourOrgFilter)
            }

            Action.Refresh -> {
                ourOrgFilter.requestNewData = true
                stateController.refresh(ourOrgFilter)
            }

            Action.RequestPage -> {
                ourOrgFilter.requestNewData = true
                stateController.loadNewPage(ourOrgFilter)
            }
        }
    }

    override fun executeIntent(intent: Intent, getState: () -> State) {
        when (intent) {
            Intent.RequestPage -> executeAction(Action.RequestPage)

            Intent.Refresh -> executeAction(Action.Refresh)

            Intent.OnCleared -> stateController.release()

            is Intent.OpenFilter -> openFilter(intent.anchor)

            Intent.OnApply -> publish(Label.ClickApply(getState().selectedOrganisations))
            Intent.OnReset -> {
                getState().data?.items?.forEach {
                    (it as? OrganisationVM)?.isSelected?.set(false)
                }
                dispatch(
                    Message.UpdateSelectedOrganisations(emptyList())
                )
            }

            is Intent.OrganisationItemClicked -> {
                val selectedOrganisations = getState().selectedOrganisations.toMutableList()
                if (initParams.isMultipleChoice) {
                    if (intent.organisationVm.isSelected.get()) {
                        if (!intent.needClose) {
                            selectedOrganisations.remove(intent.organisationVm.organisation)
                            dispatch(Message.UpdateSelectedOrganisations(selectedOrganisations))
                        }
                    } else {
                        selectedOrganisations.add(intent.organisationVm.organisation)
                        dispatch(Message.UpdateSelectedOrganisations(selectedOrganisations))
                    }

                    if (intent.needClose) {
                        publish(Label.ClickOrganisation(selectedOrganisations))
                        publish(Label.ClickApply(selectedOrganisations))
                        return
                    }
                } else {
                    selectedOrganisations.clear()
                    selectedOrganisations.add(intent.organisationVm.organisation)
                    dispatch(Message.UpdateSelectedOrganisations(selectedOrganisations))
                }

                getState().data?.items?.forEach {
                    val organisationVM = (it as? OrganisationVM)
                    organisationVM?.isSelected?.set(
                        selectedOrganisations.any { org -> org.uuid == organisationVM.organisation.uuid }
                    )
                }

                publish(Label.ClickOrganisation(selectedOrganisations))
            }

            Intent.OnShowContent -> publish(Label.OnShowContent)
            Intent.SubscribeDataEvent -> {
                subscribeDataRefreshEvent()
                executeAction(Action.LoadData)
            }

            Intent.UnSubscribeDataEvent -> unsubscribeDataEvent()
            is Intent.SearchTextChanged -> {
                if (intent.searchText != ourOrgFilter.searchString) {
                    ourOrgFilter.searchString = intent.searchText
                    executeAction(Action.Refresh)
                }
            }

            is Intent.Loaded -> {
                dispatch(
                    Message.Loaded(
                        OurOrgListStore.ShowData(
                            intent.organisation.map {
                                OrganisationVM(it, ourOrgFilter.searchString).apply {
                                    isSelected.set(getState().selectedOrganisations.contains(it))
                                }
                            },
                            intent.updatedAll,
                            intent.refreshState
                        )
                    )
                )
            }

            Intent.ShowEmptyView -> {
                val isSearch = ourOrgFilter.searchString?.isNotEmpty() ?: false
                val stubContent =
                    if (isSearch) {
                        StubViewCase.NO_SEARCH_RESULTS.getContent()
                    } else {
                        ImageStubContent(
                            imageType = StubViewImageType.EMPTY,
                            messageRes = R.string.our_org_empty_title,
                            detailsRes = ResourcesCompat.ID_NULL
                        )
                    }

                dispatch(Message.Empty(stubContent, isSearch))
            }
        }
    }

    @Suppress("WHEN_ENUM_CAN_BE_NULL_IN_JAVA")
    private fun subscribeDataRefreshEvent() {
        dataRefreshDisposable?.dispose()
        dataRefreshDisposable = ourOrgListInteractor.subscribeDataRefreshEvents()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { event ->
                    when (event) {
                        OurOrgListInteractor.DataEvent.ERROR -> {
                            if (!isFirstSyncComplete) {
                                dispatch(Message.Empty(StubViewCase.SBIS_ERROR.getContent(), false))
                            }
                        }

                        OurOrgListInteractor.DataEvent.NETWORK_ERROR -> {
                            if (!isFirstSyncComplete) {
                                dispatch(Message.Empty(StubViewCase.NO_CONNECTION.getContent(), false))
                            }
                        }

                        OurOrgListInteractor.DataEvent.REFRESH -> {
                            isFirstSyncComplete = true
                            executeAction(Action.RefreshAllData)
                        }
                    }
                },
                Timber::e
            )
    }

    private fun unsubscribeDataEvent() {
        dataRefreshDisposable?.dispose()
        dataRefreshDisposable = null
    }

    override fun showEmptyError(error: Throwable?) {
        Timber.e(error)
        error?.localizedMessage
            ?.takeIf { it.isNotEmpty() }
            ?.also {
                publish(Label.LoadingError(PlatformSbisString.Value(it)))
            }
    }

    override fun showEmptyProgress() {
        if (!blockingWhileReadOrganisation) {
            dispatch(Message.Progress(emptyProgress = true))
        }
    }

    override fun showEmptyView() {
        if (!isFirstSyncComplete) {
            showEmptyProgress()

            return
        }

        executeIntent(Intent.ShowEmptyView)
    }

    override fun showErrorMessage(error: Throwable) {
        Timber.e(error)
        error.localizedMessage
            ?.takeIf { it.isNotEmpty() }
            ?.also {
                publish(Label.LoadingError(PlatformSbisString.Value(it)))
            }
    }

    override fun showRefreshProgress(show: Boolean) {
        dispatch(Message.Progress(swipeRefreshing = true))
    }

    override fun showPageProgress(show: Boolean) {
        dispatch(Message.Progress(showPageProgress = true))
    }

    override fun showData(data: List<Organisation>, updatedAll: Boolean, refreshState: Boolean) {
        isFirstSyncComplete = isFirstSyncComplete || data.isNotEmpty()

        organisations = ArrayList(data)
        executeIntent(Intent.Loaded(organisations, updatedAll, refreshState))
    }

    private fun openFilter(anchor: View) {
        publish(
            Label.OpenFilter(
                anchor,
                listOf(
                    MenuItem(
                        title = PlatformSbisString.Res(R.string.our_org_filter_state_active) as SbisString,
                        state =
                        if (ourOrgFilter.withEliminated) {
                            MenuItemState.MIXED
                        } else {
                            MenuItemState.ON
                        },
                        handler = ::onShowActiveOrganisation
                    ),
                    MenuItem(
                        title = PlatformSbisString.Res(R.string.our_org_filter_state_all) as SbisString,
                        state =
                        if (ourOrgFilter.withEliminated) {
                            MenuItemState.ON
                        } else {
                            MenuItemState.MIXED
                        },
                        handler = ::onShowAllOrganisation
                    )
                )
            )
        )
    }

    private fun onShowActiveOrganisation() {
        if (ourOrgFilter.withEliminated) {
            ourOrgFilter.withEliminated = false
            dispatch(Message.ApplyFilter(null))
            executeAction(Action.LoadData)
        }
    }

    private fun onShowAllOrganisation() {
        if (!ourOrgFilter.withEliminated) {
            ourOrgFilter.withEliminated = true
            dispatch(
                Message.ApplyFilter(
                    PlatformSbisString.Res(R.string.our_org_filter_state_all)
                )
            )
            executeAction(Action.LoadData)
        }
    }

    private fun getOrganisationsById(selectedIds: List<Int>, onSuccess: (List<Organisation>) -> Unit) {
        if (blockingWhileReadOrganisation) return

        if (selectedIds.isEmpty()) {
            onSuccess(emptyList())
            return
        }

        scope.launch {
            try {
                dispatch(Message.Progress(emptyProgress = true))
                blockingWhileReadOrganisation = true

                val organisations = ourOrgListInteractor.listRx(
                    OurOrgFilter(
                        withEliminated = initParams.withEliminated,
                        headsOnly = initParams.headsOnly,
                        ids = selectedIds,
                        count = selectedIds.size,
                        scopesAreas = initParams.scopesAreas
                    )
                ).result

                onSuccess(organisations)
                blockingWhileReadOrganisation = false
                dispatch(Message.Progress(emptyProgress = false))
            } catch (exception: Exception) {
                dispatch(Message.Progress(emptyProgress = false))
                blockingWhileReadOrganisation = false
                showErrorMessage(exception)
            }
        }
    }
}

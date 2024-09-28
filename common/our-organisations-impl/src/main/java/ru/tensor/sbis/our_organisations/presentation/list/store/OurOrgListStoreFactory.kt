package ru.tensor.sbis.our_organisations.presentation.list.store

import com.arkivanov.mvikotlin.core.store.SimpleBootstrapper
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import ru.tensor.sbis.design.stubview.StubViewContent
import ru.tensor.sbis.design.theme.res.PlatformSbisString
import ru.tensor.sbis.design.theme.res.SbisString
import ru.tensor.sbis.our_organisations.R
import ru.tensor.sbis.our_organisations.feature.data.Organisation
import ru.tensor.sbis.our_organisations.feature.data.OurOrgParams
import ru.tensor.sbis.our_organisations.presentation.list.di.DISCARD_RESULT_AFTER_CLICK_KEY
import ru.tensor.sbis.our_organisations.presentation.list.interactor.OurOrgListInteractor
import ru.tensor.sbis.our_organisations.presentation.list.store.OurOrgListStore.Intent
import ru.tensor.sbis.our_organisations.presentation.list.store.OurOrgListStore.Label
import ru.tensor.sbis.our_organisations.presentation.list.store.OurOrgListStore.State
import javax.inject.Inject
import javax.inject.Named

/**
 * Фабрика для создания [OurOrgListStore] экрана нашей организации.
 *
 * @author mv.ilin
 */
internal class OurOrgListStoreFactory @Inject constructor(
    private val initParams: OurOrgParams,
    @Named(DISCARD_RESULT_AFTER_CLICK_KEY) private val discardResultAfterClick: Boolean,
    private val storeFactory: StoreFactory,
    private val ourOrgListInteractor: OurOrgListInteractor
) {

    fun create(): OurOrgListStore =
        object :
            OurOrgListStore,
            Store<Intent, State, Label> by storeFactory.create(
                name = "OurOrgListStore",
                initialState = State(
                    emptyProgress = false,
                    showPageProgress = false,
                    swipeRefreshing = false,
                    stubContent = null,
                    data = null,
                    needShowSearch = true,
                    setSelectedFilter = when (initParams.withEliminated) {
                        true -> PlatformSbisString.Res(R.string.our_org_filter_state_all)
                        false -> null
                    },
                    selectedOrganisations = emptyList(),
                    discardResultAfterClick = discardResultAfterClick
                ),
                bootstrapper = SimpleBootstrapper(Action.Init),
                executorFactory = { OurOrgListExecutorImpl(ourOrgListInteractor, initParams) },
                reducer = OurOrgListReducerImpl()
            ) {}

    sealed interface Action {
        object Init : Action
        object LoadData : Action
        object RefreshAllData : Action
        object RequestPage : Action
        object Refresh : Action
    }

    sealed interface Message {
        data class Loaded(val data: OurOrgListStore.ShowData) : Message
        data class Progress(
            val emptyProgress: Boolean = false,
            val showPageProgress: Boolean = false,
            val swipeRefreshing: Boolean = false
        ) : Message

        data class Empty(val stubContent: StubViewContent, val needShowSearch: Boolean) : Message
        data class ApplyFilter(val organisation: SbisString?) : Message
        data class UpdateSelectedOrganisations(val selectedOrganisations: List<Organisation>) : Message
    }
}

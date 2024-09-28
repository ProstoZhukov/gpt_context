package ru.tensor.sbis.our_organisations.presentation.list.store

import android.view.View
import com.arkivanov.mvikotlin.core.store.Store
import ru.tensor.sbis.base_components.adapter.universal.UniversalBindingItem
import ru.tensor.sbis.design.context_menu.Item
import ru.tensor.sbis.design.stubview.StubViewContent
import ru.tensor.sbis.design.theme.res.PlatformSbisString
import ru.tensor.sbis.design.theme.res.SbisString
import ru.tensor.sbis.our_organisations.feature.data.Organisation
import ru.tensor.sbis.our_organisations.presentation.list.store.OurOrgListStore.Intent
import ru.tensor.sbis.our_organisations.presentation.list.store.OurOrgListStore.Label
import ru.tensor.sbis.our_organisations.presentation.list.store.OurOrgListStore.State
import ru.tensor.sbis.our_organisations.presentation.list.ui.adapter.OrganisationVM

/**
 * Хранилище данных бизнес-логики нашей организации.
 *
 * @author mv.ilin
 */
internal interface OurOrgListStore :
    Store<Intent, State, Label> {

    sealed interface Intent {
        object Refresh : Intent
        object OnCleared : Intent
        object OnShowContent : Intent
        object SubscribeDataEvent : Intent
        object UnSubscribeDataEvent : Intent
        object RequestPage : Intent
        object OnApply : Intent
        object OnReset : Intent
        data class OrganisationItemClicked(val organisationVm: OrganisationVM, val needClose: Boolean = false) : Intent
        data class OpenFilter(val anchor: View) : Intent
        data class SearchTextChanged(val searchText: String) : Intent
        data class Loaded(
            val organisation: List<Organisation>,
            val updatedAll: Boolean,
            val refreshState: Boolean
        ) : Intent

        object ShowEmptyView : Intent
    }

    sealed interface Label {
        data class LoadingError(val message: PlatformSbisString) : Label
        object OnShowContent : Label
        data class ClickOrganisation(val organisations: List<Organisation>) : Label
        data class ClickApply(val organisations: List<Organisation>) : Label
        data class OpenFilter(val anchor: View, val sbisMenuItems: Iterable<Item>) : Label
    }

    data class State(
        val data: ShowData?,
        val emptyProgress: Boolean,
        val showPageProgress: Boolean,
        val swipeRefreshing: Boolean,
        val stubContent: StubViewContent?,
        val setSelectedFilter: SbisString?,
        val needShowSearch: Boolean,
        val selectedOrganisations: List<Organisation>,
        val discardResultAfterClick: Boolean
    )

    data class ShowData(
        val items: List<UniversalBindingItem>,
        val updatedAll: Boolean,
        val refreshState: Boolean
    )
}

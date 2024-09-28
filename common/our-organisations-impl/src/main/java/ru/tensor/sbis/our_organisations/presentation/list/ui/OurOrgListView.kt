package ru.tensor.sbis.our_organisations.presentation.list.ui

import android.view.View
import com.arkivanov.mvikotlin.core.view.MviView
import ru.tensor.sbis.design.stubview.StubViewContent
import ru.tensor.sbis.design.theme.res.SbisString
import ru.tensor.sbis.our_organisations.presentation.list.store.OurOrgListStore
import ru.tensor.sbis.our_organisations.presentation.list.ui.adapter.OrganisationVM

/**
 * Описание событий и модели экрана нашей организации.
 *
 * @author mv.ilin
 */
internal interface OurOrgListView : MviView<OurOrgListView.Model, OurOrgListView.Event> {

    sealed interface Event {
        object Refresh : Event
        object OnShowContent : Event
        object RequestPage : Event
        data class OrganisationItemClicked(val organisationVm: OrganisationVM, val needClose: Boolean = false) : Event
        data class OpenFilter(val anchor: View) : Event
        data class SearchTextChanged(val searchText: String) : Event
    }

    data class Model(
        val data: OurOrgListStore.ShowData?,
        val emptyProgress: Boolean,
        val showPageProgress: Boolean,
        val swipeRefreshing: Boolean,
        val stubContent: StubViewContent?,
        val setSelectedFilter: SbisString?,
        val needShowSearch: Boolean
    )

    fun cancel()

    fun onStop()
}

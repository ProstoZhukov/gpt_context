package ru.tensor.sbis.our_organisations.presentation.list.store

import com.arkivanov.mvikotlin.core.store.Reducer
import ru.tensor.sbis.our_organisations.presentation.list.store.OurOrgListStore.State
import ru.tensor.sbis.our_organisations.presentation.list.store.OurOrgListStoreFactory.Message

/**
 * Редуктор сообщений от Executor экрана нашей организации.
 *
 * @author mv.ilin
 */
internal class OurOrgListReducerImpl : Reducer<State, Message> {

    override fun State.reduce(msg: Message): State {
        return when (msg) {
            is Message.Loaded -> copy(
                data = msg.data,
                stubContent = null,
                needShowSearch = true,
                emptyProgress = false,
                showPageProgress = false,
                swipeRefreshing = false
            )

            is Message.Progress -> copy(
                emptyProgress = msg.emptyProgress,
                showPageProgress = msg.showPageProgress,
                swipeRefreshing = msg.swipeRefreshing
            )

            is Message.Empty -> copy(
                data = data?.copy(items = listOf(), updatedAll = true),
                stubContent = msg.stubContent,
                needShowSearch = msg.needShowSearch,
                emptyProgress = false,
                showPageProgress = false,
                swipeRefreshing = false
            )

            is Message.ApplyFilter -> copy(
                setSelectedFilter = msg.organisation
            )

            is Message.UpdateSelectedOrganisations -> copy(selectedOrganisations = msg.selectedOrganisations)
        }
    }
}

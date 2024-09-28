package ru.tensor.sbis.communicator.communicator_crm_chat_list.filter.connections.store

import android.os.Parcelable
import com.arkivanov.mvikotlin.core.store.Store
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.communicator.communicator_crm_chat_list.filter.connections.store.CRMConnectionListStore.Intent
import ru.tensor.sbis.communicator.communicator_crm_chat_list.filter.connections.store.CRMConnectionListStore.Label
import ru.tensor.sbis.communicator.communicator_crm_chat_list.filter.connections.store.CRMConnectionListStore.State
import java.util.UUID

/**
 * Стор источников CRM.
 *
 * @author da.zhukov
 */
internal interface CRMConnectionListStore : Store<Intent, State, Label> {

    /**
     * Намерения стора источников CRM.
     */
    sealed interface Intent {
        data class SearchQuery(val query: String?) : Intent
        data class ItemSelected(val idAndLabel: Pair<UUID, String>) : Intent
    }

    /**
     * Состояние стора источников CRM.
     */
    @Parcelize
    data class State(
        val query: String? = null
    ) : Parcelable

    /**
     * События стора источников CRM.
     */
    sealed interface Label {
        data class ItemSelected(val idAndLabel: Pair<UUID, String>) : Label
    }
}

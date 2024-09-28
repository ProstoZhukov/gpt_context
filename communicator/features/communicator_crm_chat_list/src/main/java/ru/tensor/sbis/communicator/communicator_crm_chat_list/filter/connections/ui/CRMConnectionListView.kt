package ru.tensor.sbis.communicator.communicator_crm_chat_list.filter.connections.ui

import com.arkivanov.mvikotlin.core.view.MviView
import java.util.UUID

/**
 * @author da.zhukov
 */
internal interface CRMConnectionListView :
    MviView<CRMConnectionListView.Model, CRMConnectionListView.Event> {

    /**@SelfDocumented*/
    sealed interface Event {
        data class EnterSearchQuery(val query: String?) : Event
        data class ItemSelected(val idAndLabel: Pair<UUID, String>) : Event
    }

    /**
     * Модель для отображения.
     */
    data class Model(val query: String? = null)
} 
        
        
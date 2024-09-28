package ru.tensor.sbis.communicator.crm.conversation.reassing_operator.another_operator.store

import android.os.Parcelable
import com.arkivanov.mvikotlin.core.store.Store
import kotlinx.parcelize.Parcelize
import java.util.UUID

/**
 * Стор переназначения оператору.
 *
 * @author da.zhukov
 */
interface CRMAnotherOperatorStore :
    Store<CRMAnotherOperatorStore.Intent, CRMAnotherOperatorStore.State, CRMAnotherOperatorStore.Label> {
    /**
     * Намерения переназначения оператору.
     */
    sealed interface Intent {
        data class InitialLoading(val query: String? = null) : Intent
        data class SearchQuery(val query: String?) : Intent
        data class OnItemClick(val operatorId: UUID) : Intent
        object BackButtonClick : Intent
        object FilterClick : Intent
        data class ApplyFilter(val channelId: UUID?, val filter: String) : Intent
    }

    /**
     * События переназначения оператору.
     */
    sealed interface Label {
        object BackButtonClick : Label
        object FilterClick : Label
        object ShowNetworkError : Label
    }

    /**
     * Состояние стора переназначения оператору.
     */
    @Parcelize
    data class State(
        val query: String? = null,
        val filter: String
    ) : Parcelable
}
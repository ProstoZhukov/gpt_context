package ru.tensor.sbis.communicator.crm.conversation.reassing_operator.another_operator.ui

import com.arkivanov.mvikotlin.core.view.MviView
import java.util.UUID

/** @SelfDocumented */
internal interface CRMAnotherOperatorView : MviView<CRMAnotherOperatorView.Model, CRMAnotherOperatorView.Event> {

    /** @SelfDocumented */
    sealed interface Event {
        data class EnterSearchQuery(val query: String?) : Event
        data class OnItemClick(val operatorId: UUID) : Event
        object BackButtonClick : Event
        object FilterClick : Event
    }

    /** @SelfDocumented */
    data class Model(
        val query: String? = null,
        val filter: String? = null
    )

}
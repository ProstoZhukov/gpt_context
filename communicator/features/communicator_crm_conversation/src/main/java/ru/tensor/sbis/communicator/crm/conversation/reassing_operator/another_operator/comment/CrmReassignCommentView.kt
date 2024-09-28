package ru.tensor.sbis.communicator.crm.conversation.reassing_operator.another_operator.comment

import com.arkivanov.mvikotlin.core.view.MviView

/**
 * @author da.zhukov
 */
internal interface CrmReassignCommentView :
    MviView<CrmReassignCommentView.Model, CrmReassignCommentView.Event> {

    /**@SelfDocumented*/
    sealed interface Event {

        /**@SelfDocumented*/
        data class ReassignClick(val comment: CharSequence) : Event
    }

    /**
     * Модель для отображения.
     */
    data class Model(val comment: CharSequence)
}
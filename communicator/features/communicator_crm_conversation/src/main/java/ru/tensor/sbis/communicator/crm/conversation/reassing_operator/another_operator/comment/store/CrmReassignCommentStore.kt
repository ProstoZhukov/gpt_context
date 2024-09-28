package ru.tensor.sbis.communicator.crm.conversation.reassing_operator.another_operator.comment.store

import com.arkivanov.mvikotlin.core.store.Store
import ru.tensor.sbis.communicator.declaration.crm.model.CRMChatFilterModel
import ru.tensor.sbis.communicator.declaration.crm.model.CRMCheckableFilterType
import ru.tensor.sbis.communicator.declaration.crm.model.CRMOpenableFilterType
import ru.tensor.sbis.communicator.declaration.crm.model.CRMRadioButtonFilterType
import ru.tensor.sbis.communicator.declaration.crm.providers.CRMAnotherOperatorParams
import java.util.UUID
import ru.tensor.sbis.common.R as RCommon

/**
 * @author da.zhukov
 */
internal interface CrmReassignCommentStore :
    Store<CrmReassignCommentStore.Intent, CrmReassignCommentStore.State, CrmReassignCommentStore.Label> {

    /**
     * Намерение, отражающее действие со стороны пользователя. Подается в [Store] как входной параметр.
     *
     * @author da.zhukov
     */
    sealed interface Intent {
        data class ReassignClick(val comment: CharSequence) : Intent
    }

    /**
     * Выходной параметр [Store] для взаимодействия с сущностями вне MVI. Является ответом на пришедшим [Intent].
     *
     * @author da.zhukov
     */
    sealed interface Label {
        data class ReassignClick(val params: CRMAnotherOperatorParams) : Label
    }

    /**
     * Содержит текущее состояние.
     *
     * @property currentComment текущий комментарий.
     */
    data class State(
        val currentComment: CharSequence,
    )
}

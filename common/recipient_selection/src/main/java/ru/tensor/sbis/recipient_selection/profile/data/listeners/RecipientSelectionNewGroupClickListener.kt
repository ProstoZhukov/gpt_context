package ru.tensor.sbis.recipient_selection.profile.data.listeners

import androidx.fragment.app.FragmentActivity
import ru.tensor.sbis.design.selection.ui.contract.listeners.NewGroupClickListener
import ru.tensor.sbis.recipient_selection.profile.ui.resultmanager.RecipientSelectionResultData
import ru.tensor.sbis.recipient_selection.profile.data.RecipientsSearchFilter
import ru.tensor.sbis.recipient_selection.profile.ui.getRecipientSelectionComponent

/**
 * Реализация слушателя кликов по кнопке новой группы в одиночном выборе получателей для нового чата
 *
 * @author vv.chekurda
 */
internal class RecipientSelectionNewGroupClickListener(
    private val filter: RecipientsSearchFilter
) : NewGroupClickListener<FragmentActivity> {

    override fun onButtonClicked(activity: FragmentActivity) {
        activity.getRecipientSelectionComponent(filter)
            .getRecipientSelectionResultManager()
            .putNewData(emptySuccessResult)
    }

    private val emptySuccessResult
        get()= RecipientSelectionResultData(RecipientSelectionResultData.RESULT_SUCCESS, filter.requestCode, emptyList())
}
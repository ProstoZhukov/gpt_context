package ru.tensor.sbis.recipient_selection.profile.data.listeners

import androidx.fragment.app.FragmentActivity
import ru.tensor.sbis.design.selection.ui.contract.listeners.SelectionListener
import ru.tensor.sbis.design.selection.ui.model.recipient.RecipientSelectorItemModel
import ru.tensor.sbis.mvp.multiselection.data.BaseSelectionResultData.RESULT_SUCCESS
import ru.tensor.sbis.recipient_selection.profile.data.MultiSelectionItemContainer
import ru.tensor.sbis.recipient_selection.profile.data.RecipientsSearchFilter
import ru.tensor.sbis.recipient_selection.profile.ui.castTo
import ru.tensor.sbis.recipient_selection.profile.ui.getRecipientSelectionComponent
import ru.tensor.sbis.recipient_selection.profile.ui.resultmanager.RecipientSelectionResultData

/**
 * Реализация слушателя на одиночный выбор получателя
 *
 * @author vv.chekurda
 */
internal class SingleCompleteListener(
    private val filter: RecipientsSearchFilter,
    private val needCloseOnComplete: Boolean,
) : SelectionListener<RecipientSelectorItemModel, FragmentActivity> {

    override fun onComplete(activity: FragmentActivity, result: RecipientSelectorItemModel) {
        activity.getRecipientSelectionComponent(filter).getRecipientSelectionResultManager().putNewData(
            RecipientSelectionResultData(
                RESULT_SUCCESS,
                filter.requestCode,
                listOf(result.castTo<MultiSelectionItemContainer>()!!.item)
            )
        )
        if (needCloseOnComplete) activity.onBackPressed()
    }
}
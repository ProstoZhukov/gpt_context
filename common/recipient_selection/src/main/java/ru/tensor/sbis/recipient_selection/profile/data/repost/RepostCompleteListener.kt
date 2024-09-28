package ru.tensor.sbis.recipient_selection.profile.data.repost

import androidx.fragment.app.FragmentActivity
import ru.tensor.sbis.design.selection.ui.contract.listeners.MultiSelectionListener
import ru.tensor.sbis.design.selection.ui.model.recipient.RecipientSelectorItemModel
import ru.tensor.sbis.recipient_selection.profile.data.MultiSelectionItemContainer
import ru.tensor.sbis.recipient_selection.profile.di.RecipientSelectionComponentProvider
import ru.tensor.sbis.recipient_selection.profile.ui.castTo
import ru.tensor.sbis.recipient_selection.profile.ui.resultmanager.RecipientSelectionResultData

/**
 * Реализация слушателя на подтверждение множественного выбора контактов для репоста
 *
 * @author vv.chekurda
 */
internal class RepostCompleteListener : MultiSelectionListener<RecipientSelectorItemModel, FragmentActivity> {

    override fun onComplete(activity: FragmentActivity, result: List<RecipientSelectorItemModel>) {
        val items = result.map { it.castTo<MultiSelectionItemContainer>()!!.item }
        RecipientSelectionComponentProvider.getRecipientSelectionSingletonComponent(activity)
            .getContactsSelectionResultManagerForRepost()
            .putNewData(RecipientSelectionResultData(items))
    }
}
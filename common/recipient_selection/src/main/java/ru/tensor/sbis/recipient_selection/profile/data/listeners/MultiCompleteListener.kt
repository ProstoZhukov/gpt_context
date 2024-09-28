package ru.tensor.sbis.recipient_selection.profile.data.listeners

import androidx.fragment.app.FragmentActivity
import ru.tensor.sbis.design.selection.ui.contract.listeners.MultiSelectionListener
import ru.tensor.sbis.design.selection.ui.model.recipient.RecipientSelectorItemModel
import ru.tensor.sbis.mvp.multiselection.data.BaseSelectionResultData.RESULT_SUCCESS
import ru.tensor.sbis.recipient_selection.profile.ui.RecipientSelectionActivity
import ru.tensor.sbis.recipient_selection.profile.ui.castTo
import ru.tensor.sbis.recipient_selection.profile.ui.getRecipientSelectionComponent
import ru.tensor.sbis.recipient_selection.profile.data.group_profiles.GroupItem
import ru.tensor.sbis.recipient_selection.profile.ui.resultmanager.RecipientSelectionResultData
import ru.tensor.sbis.recipient_selection.profile.data.MultiSelectionItemContainer
import ru.tensor.sbis.recipient_selection.profile.data.RecipientsSearchFilter

/**
 * Реализация слушателя на подтверждение множественного выбора получателей
 *
 * @author vv.chekurda
 */
internal class MultiCompleteListener(
    private val filter: RecipientsSearchFilter,
    private val needCloseOnComplete: Boolean
) : MultiSelectionListener<RecipientSelectorItemModel, FragmentActivity> {

    override fun onComplete(activity: FragmentActivity, result: List<RecipientSelectorItemModel>) {
        activity.castTo<RecipientSelectionActivity>()?.isSelectionCompleted = true
        val items = result.map { it.castTo<MultiSelectionItemContainer>()!!.item }
        val groupItems = items.filter { GroupItem.GROUP_TYPE == it.itemType }.castTo<List<GroupItem>>()
        val closeFunction = if (needCloseOnComplete) activity::onBackPressed else null
        val component = activity.getRecipientSelectionComponent(filter)
        if (!groupItems.isNullOrEmpty()) {
            component.getRecipientSelectionInteractor().loadProfilesByGroups(groupItems)
                .subscribe(
                    {
                        component.getRecipientSelectionResultManager().putNewData(
                            RecipientSelectionResultData(RESULT_SUCCESS, filter.requestCode, items)
                        )
                        closeFunction?.invoke()
                    }, {
                        closeFunction?.invoke()
                    }
                ).uncheckResultAnnotationStub()
        } else {
            component.getRecipientSelectionResultManager().putNewData(
                RecipientSelectionResultData(RESULT_SUCCESS, filter.requestCode, items)
            )
            closeFunction?.invoke()
        }
    }
}

private fun <T> T.uncheckResultAnnotationStub(): T = this
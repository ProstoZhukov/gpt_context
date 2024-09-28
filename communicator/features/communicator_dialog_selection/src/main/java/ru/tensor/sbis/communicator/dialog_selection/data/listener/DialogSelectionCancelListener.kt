package ru.tensor.sbis.communicator.dialog_selection.data.listener

import androidx.fragment.app.FragmentActivity
import ru.tensor.sbis.communicator.common.dialog_selection.CancelDialogSelectionResult
import ru.tensor.sbis.communicator.dialog_selection.di.getDialogSelectionComponent
import ru.tensor.sbis.communicator.dialog_selection.presentation.DialogSelectionActivity
import ru.tensor.sbis.design.selection.ui.contract.listeners.SelectionCancelListener

/**
 * Реализация слушателя на отмену выбора диалога/участников
 *
 * @author vv.chekurda
 */
internal class DialogSelectionCancelListener : SelectionCancelListener<FragmentActivity> {

    override fun onCancel(activity: FragmentActivity) {
        activity.run {
            if (activity !is DialogSelectionActivity) {
                getDialogSelectionComponent().resultManager.putNewData(CancelDialogSelectionResult)
            }
            onBackPressed()
        }
    }
}
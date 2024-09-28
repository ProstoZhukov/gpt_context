package ru.tensor.sbis.recipient_selection.profile.data.listeners

import androidx.fragment.app.FragmentActivity
import ru.tensor.sbis.design.selection.ui.contract.listeners.SelectionCancelListener

/**
 * Реализация слушателя на отмену выбора получателей
 *
 * @author vv.chekurda
 */
internal class CancelListener : SelectionCancelListener<FragmentActivity> {

    override fun onCancel(activity: FragmentActivity) {
        activity.onBackPressed()
    }
}
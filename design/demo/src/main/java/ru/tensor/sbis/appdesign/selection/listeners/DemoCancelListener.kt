package ru.tensor.sbis.appdesign.selection.listeners

import android.app.Activity
import androidx.fragment.app.FragmentActivity
import ru.tensor.sbis.design.selection.ui.contract.listeners.SelectionCancelListener

/**
 * @author ma.kolpakov
 */
class DemoCancelListener : SelectionCancelListener<FragmentActivity> {

    override fun onCancel(activity: FragmentActivity) {
        activity.setResult(Activity.RESULT_CANCELED)
        activity.finish()
    }
}
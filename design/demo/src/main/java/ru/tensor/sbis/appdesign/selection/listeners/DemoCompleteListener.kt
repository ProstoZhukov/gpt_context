package ru.tensor.sbis.appdesign.selection.listeners

import android.app.Activity
import android.widget.Toast
import ru.tensor.sbis.appdesign.selection.SelectorActivity
import ru.tensor.sbis.design.selection.ui.contract.listeners.MultiSelectionListener
import ru.tensor.sbis.design.selection.ui.model.SelectorItemModel

/**
 * @author ma.kolpakov
 */
class DemoCompleteListener : MultiSelectionListener<SelectorItemModel, SelectorActivity> {

    override fun onComplete(activity: SelectorActivity, result: List<SelectorItemModel>) {
        Toast.makeText(activity, "Selected items $result", Toast.LENGTH_LONG).show()
        activity.setResult(Activity.RESULT_OK)
        activity.finish()
    }
}
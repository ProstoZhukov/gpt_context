package ru.tensor.sbis.appdesign.selection.listeners

import android.app.Activity
import android.widget.Toast
import ru.tensor.sbis.appdesign.selection.SingleSelectorActivity
import ru.tensor.sbis.design.selection.ui.contract.listeners.SelectionListener
import ru.tensor.sbis.design.selection.ui.model.SelectorItemModel

/**
 * @author us.bessonov
 */
class DemoSingleCompleteListener : SelectionListener<SelectorItemModel, SingleSelectorActivity> {

    override fun onComplete(activity: SingleSelectorActivity, result: SelectorItemModel) {
        Toast.makeText(activity, "Selected item $result", Toast.LENGTH_LONG).show()
        activity.setResult(Activity.RESULT_OK)
        activity.finish()
    }
}
package ru.tensor.sbis.appdesign.selection.listeners

import android.app.Activity
import android.widget.Toast
import ru.tensor.sbis.appdesign.selection.RecipientSingleSelectorActivity
import ru.tensor.sbis.design.selection.ui.contract.listeners.SelectionListener
import ru.tensor.sbis.design.selection.ui.model.recipient.DefaultRecipientSelectorItemModel

/**
 * @author ma.kolpakov
 */
class DemoRecipientSingleCompleteListener : SelectionListener<DefaultRecipientSelectorItemModel, RecipientSingleSelectorActivity> {

    override fun onComplete(activity: RecipientSingleSelectorActivity, result: DefaultRecipientSelectorItemModel) {
        Toast.makeText(activity, "Selected items $result", Toast.LENGTH_LONG).show()
        activity.setResult(Activity.RESULT_OK)
        activity.finish()
    }
}
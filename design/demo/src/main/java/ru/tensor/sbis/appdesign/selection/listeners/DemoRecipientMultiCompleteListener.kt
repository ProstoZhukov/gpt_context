package ru.tensor.sbis.appdesign.selection.listeners

import android.app.Activity
import android.widget.Toast
import ru.tensor.sbis.appdesign.selection.RecipientMultiSelectorActivity
import ru.tensor.sbis.design.selection.ui.contract.listeners.MultiSelectionListener
import ru.tensor.sbis.design.selection.ui.model.recipient.DefaultRecipientSelectorItemModel

/**
 * @author ma.kolpakov
 */
class DemoRecipientMultiCompleteListener : MultiSelectionListener<DefaultRecipientSelectorItemModel, RecipientMultiSelectorActivity> {

    override fun onComplete(activity: RecipientMultiSelectorActivity, result: List<DefaultRecipientSelectorItemModel>) {
        Toast.makeText(activity, "Selected items $result", Toast.LENGTH_LONG).show()
        activity.setResult(Activity.RESULT_OK)
        activity.finish()
    }
}
package ru.tensor.sbis.appdesign.selection.listeners

import android.widget.Toast
import ru.tensor.sbis.appdesign.selection.RecipientSingleSelectorActivity
import ru.tensor.sbis.design.selection.ui.contract.listeners.NewGroupClickListener

/**
 * @author ma.kolpakov
 */
internal class DemoNewGroupClickListener : NewGroupClickListener<RecipientSingleSelectorActivity> {

    override fun onButtonClicked(activity: RecipientSingleSelectorActivity) {
        Toast.makeText(activity, "New group button clicked", Toast.LENGTH_LONG).show()
    }
}
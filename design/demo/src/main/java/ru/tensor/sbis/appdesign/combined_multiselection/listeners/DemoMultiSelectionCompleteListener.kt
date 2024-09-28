package ru.tensor.sbis.appdesign.combined_multiselection.listeners

import android.app.Activity
import android.content.Context
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import ru.tensor.sbis.design.selection.ui.contract.listeners.MultiSelectionListener
import ru.tensor.sbis.design.selection.ui.model.SelectorItemModel
import ru.tensor.sbis.design.selection.ui.model.recipient.DefaultPersonSelectorItemModel
import ru.tensor.sbis.design.selection.ui.model.share.dialog.DefaultDialogSelectorItemModel

/**
 * @author ma.kolpakov
 */
class DemoMultiSelectionCompleteListener : MultiSelectionListener<SelectorItemModel, FragmentActivity> {

    override fun onComplete(activity: FragmentActivity, result: List<SelectorItemModel>) {
        when {
            result.size > 1                                                      ->
                activity.showToast("Multiple Users Selected ${result.map { it.string() + "\n" }}")

            result.size == 1 && result.first() is DefaultPersonSelectorItemModel ->
                activity.showToast("Single User Selected ${result.first().string()}")

            result.size == 1 && result.first() is DefaultDialogSelectorItemModel ->
                activity.showToast("Dialog Selected ${result.first().string()}")

        }
        activity.setResult(Activity.RESULT_OK)
        activity.finish()
    }

    private fun Context.showToast(message: String) =
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()

    private fun SelectorItemModel.string(): String =
        "\nid=$id, \ntitle=$title, \nsubtitle=$subtitle"
}
